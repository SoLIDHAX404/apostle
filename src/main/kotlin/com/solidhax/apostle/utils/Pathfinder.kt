package com.solidhax.apostle.utils

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.chunk.status.ChunkStatus
import net.minecraft.world.phys.shapes.CollisionContext
import java.util.PriorityQueue
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sign

object Pathfinder {

    private const val SQRT2 = 1.4142135623730951

    private const val MIN_ITERATIONS = 2_000
    private const val MAX_ITERATIONS = 60_000
    private const val ITER_PER_DISTANCE = 25

    private const val JUMP_COST = 1.5
    private const val FALL_COST = 0.5
    private const val MAX_FALL_DISTANCE = 3

    private const val SOFT_TURN_COST = 0.5
    private const val HARD_TURN_COST = 1.0

    private const val OPENNESS_COST = 0.18
    private const val OPENNESS_MAX_BLOCKED = 2

    private const val VERTICAL_HEURISTIC_WEIGHT = 1.15

    // Max collision-shape height (in block units) for a block to count as a valid
    // floor. Fences/walls (1.5) and similar tall obstacles fail this check, so the
    // pathfinder won't try to step onto them.
    private const val MAX_GROUND_HEIGHT = 1.0

    private val OPENNESS_SIDES = arrayOf(
        intArrayOf(1, 0), intArrayOf(-1, 0), intArrayOf(0, 1), intArrayOf(0, -1)
    )

    sealed class PathResult {
        data class Complete(val path: List<BlockPos>, val iterations: Int) : PathResult()
        data class Partial(val path: List<BlockPos>, val checkpoint: BlockPos, val iterations: Int) : PathResult()
    }

    fun findPath(start: BlockPos, goal: BlockPos, level: LevelReader): PathResult? {
        val ctx = SearchCtx(level)
        val goalLoaded = ctx.isChunkLoadedAt(goal.x, goal.z) &&
            ctx.isWalkableAt(goal.x, goal.y, goal.z)

        val open = PriorityQueue<Node>(compareBy({ it.f }, { it.h }))
        val closed = HashSet<Long>()
        val gScores = HashMap<Long, Double>()

        val startKey = BlockPos.asLong(start.x, start.y, start.z)
        val startNode = Node(start, startKey, 0.0, heuristic(start, goal), null, 0, 0)
        open.add(startNode)
        gScores[startKey] = 0.0

        var bestNode: Node = startNode
        val iterCap = iterationCap(start, goal)
        var iterations = 0

        while (open.isNotEmpty() && iterations < iterCap) {
            iterations++
            val current = open.poll()

            if (goalLoaded && current.pos == goal) {
                return PathResult.Complete(smoothPath(reconstructPath(current), ctx), iterations)
            }

            if (current.h < bestNode.h) bestNode = current
            if (!closed.add(current.posLong)) continue

            forEachNeighbor(current, ctx) { nx, ny, nz, moveCost, ndx, ndz ->
                val neighborKey = BlockPos.asLong(nx, ny, nz)
                if (neighborKey in closed) return@forEachNeighbor

                val turnPenalty = if (current.parent == null) 0.0
                    else turnCost(current.dx, current.dz, ndx, ndz)
                val openness = opennessCost(nx, ny, nz, ctx)

                val tentativeG = current.g + moveCost + turnPenalty + openness
                if (tentativeG >= (gScores[neighborKey] ?: Double.MAX_VALUE)) return@forEachNeighbor

                gScores[neighborKey] = tentativeG
                open.add(
                    Node(
                        pos = BlockPos(nx, ny, nz),
                        posLong = neighborKey,
                        g = tentativeG,
                        h = heuristic(nx, ny, nz, goal),
                        parent = current,
                        dx = ndx,
                        dz = ndz
                    )
                )
            }
        }

        if (bestNode.pos == start) return null
        return PathResult.Partial(smoothPath(reconstructPath(bestNode), ctx), bestNode.pos, iterations)
    }

    fun isWalkable(pos: BlockPos, level: LevelReader): Boolean {
        val feet = level.getBlockState(pos)
        val head = level.getBlockState(pos.above())
        val ground = level.getBlockState(pos.below())
        return passable(feet, level, pos) &&
               passable(head, level, pos.above()) &&
               standableGround(ground, level, pos.below())
    }

    private class SearchCtx(val level: LevelReader) {
        private val scratch = BlockPos.MutableBlockPos()
        private val walkCache = HashMap<Long, Boolean>()
        private val passCache = HashMap<Long, Boolean>()
        private val chunkCache = HashMap<Long, Boolean>()

        fun isPassableAt(x: Int, y: Int, z: Int): Boolean =
            passCache.getOrPut(BlockPos.asLong(x, y, z)) {
                scratch.set(x, y, z)
                val state: BlockState = level.getBlockState(scratch)
                state.fluidState.isEmpty &&
                    state.getCollisionShape(level, scratch, CollisionContext.empty()).isEmpty
            }

        fun isWalkableAt(x: Int, y: Int, z: Int): Boolean =
            walkCache.getOrPut(BlockPos.asLong(x, y, z)) {
                isPassableAt(x, y, z) &&
                    isPassableAt(x, y + 1, z) &&
                    isStandableGroundAt(x, y - 1, z)
            }

        fun isChunkLoadedAt(x: Int, z: Int): Boolean {
            val cx = x shr 4
            val cz = z shr 4
            val key = (cx.toLong() shl 32) or (cz.toLong() and 0xFFFFFFFFL)
            return chunkCache.getOrPut(key) {
                level.getChunk(cx, cz, ChunkStatus.FULL, false) != null
            }
        }

        private fun isStandableGroundAt(x: Int, y: Int, z: Int): Boolean {
            scratch.set(x, y, z)
            val state = level.getBlockState(scratch)
            if (!state.fluidState.isEmpty) return false
            val shape = state.getCollisionShape(level, scratch, CollisionContext.empty())
            if (shape.isEmpty) return false
            return shape.max(Direction.Axis.Y) <= MAX_GROUND_HEIGHT
        }
    }

    private inline fun forEachNeighbor(
        from: Node,
        ctx: SearchCtx,
        action: (nx: Int, ny: Int, nz: Int, cost: Double, dx: Int, dz: Int) -> Unit
    ) {
        val px = from.pos.x
        val py = from.pos.y
        val pz = from.pos.z

        for (dx in -1..1) {
            for (dz in -1..1) {
                if (dx == 0 && dz == 0) continue
                if (from.parent != null && dx == -from.dx && dz == -from.dz) continue

                val isDiagonal = dx != 0 && dz != 0
                val horizontalCost = if (isDiagonal) SQRT2 else 1.0
                val wx = px + dx
                val wz = pz + dz

                if (!ctx.isChunkLoadedAt(wx, wz)) continue

                if (ctx.isWalkableAt(wx, py, wz) &&
                    (!isDiagonal || cardinalsClear(px, py, pz, dx, dz, 0, ctx))
                ) {
                    action(wx, py, wz, horizontalCost, dx, dz)
                    continue
                }

                val jy = py + 1
                if (ctx.isWalkableAt(wx, jy, wz) &&
                    ctx.isPassableAt(px, py + 2, pz) &&
                    (!isDiagonal || cardinalsClear(px, py, pz, dx, dz, 1, ctx))
                ) {
                    action(wx, jy, wz, horizontalCost + JUMP_COST, dx, dz)
                    continue
                }

                for (drop in 1..MAX_FALL_DISTANCE) {
                    val fy = py - drop
                    if (!ctx.isPassableAt(wx, fy, wz)) break
                    if (ctx.isWalkableAt(wx, fy, wz)) {
                        if (!isDiagonal || cardinalsClear(px, py, pz, dx, dz, -drop, ctx)) {
                            action(wx, fy, wz, horizontalCost + FALL_COST + (drop - 1) * 0.25, dx, dz)
                        }
                        break
                    }
                }
            }
        }
    }

    private fun cardinalsClear(px: Int, py: Int, pz: Int, dx: Int, dz: Int, dy: Int, ctx: SearchCtx): Boolean =
        ctx.isPassableAt(px + dx, py + dy, pz) && ctx.isPassableAt(px, py + dy, pz + dz)

    private fun turnCost(fromDx: Int, fromDz: Int, toDx: Int, toDz: Int): Double {
        if (fromDx == toDx && fromDz == toDz) return 0.0
        val dot = fromDx * toDx + fromDz * toDz
        return if (dot >= 1) SOFT_TURN_COST else HARD_TURN_COST
    }

    private fun opennessCost(x: Int, y: Int, z: Int, ctx: SearchCtx): Double {
        val hy = y + 1
        var blocked = 0
        for (side in OPENNESS_SIDES) {
            if (!ctx.isPassableAt(x + side[0], hy, z + side[1])) {
                blocked++
                if (blocked >= OPENNESS_MAX_BLOCKED) break
            }
        }
        return blocked * OPENNESS_COST
    }

    private fun heuristic(a: BlockPos, b: BlockPos): Double = heuristic(a.x, a.y, a.z, b)

    private fun heuristic(ax: Int, ay: Int, az: Int, b: BlockPos): Double {
        val dx = abs(ax - b.x).toDouble()
        val dy = abs(ay - b.y).toDouble()
        val dz = abs(az - b.z).toDouble()
        val octile = max(dx, dz) + (SQRT2 - 1.0) * min(dx, dz)
        return octile + dy * VERTICAL_HEURISTIC_WEIGHT
    }

    private fun iterationCap(start: BlockPos, goal: BlockPos): Int =
        (heuristic(start, goal) * ITER_PER_DISTANCE).toInt().coerceIn(MIN_ITERATIONS, MAX_ITERATIONS)

    private fun reconstructPath(node: Node): List<BlockPos> {
        val path = ArrayDeque<BlockPos>()
        var cur: Node? = node
        while (cur != null) {
            path.addFirst(cur.pos)
            cur = cur.parent
        }
        return path
    }

    private fun smoothPath(path: List<BlockPos>, ctx: SearchCtx): List<BlockPos> {
        val reduced = collinearReduce(path)
        if (reduced.size < 3) return reduced

        val result = ArrayList<BlockPos>(reduced.size)
        var anchor = 0
        result.add(reduced[anchor])

        while (anchor < reduced.size - 1) {
            var farthest = anchor + 1
            for (j in reduced.size - 1 downTo anchor + 2) {
                if (canTraverseStraight(reduced[anchor], reduced[j], ctx)) {
                    farthest = j
                    break
                }
            }
            result.add(reduced[farthest])
            anchor = farthest
        }
        return result
    }

    private fun collinearReduce(path: List<BlockPos>): List<BlockPos> {
        if (path.size < 3) return path
        val result = ArrayList<BlockPos>(path.size)
        result.add(path.first())
        for (i in 1 until path.size - 1) {
            val prev = path[i - 1]
            val curr = path[i]
            val next = path[i + 1]
            val collinear =
                curr.x - prev.x == next.x - curr.x &&
                curr.y - prev.y == next.y - curr.y &&
                curr.z - prev.z == next.z - curr.z
            if (!collinear) result.add(curr)
        }
        result.add(path.last())
        return result
    }

    private fun canTraverseStraight(a: BlockPos, b: BlockPos, ctx: SearchCtx): Boolean {
        if (a.y != b.y) return false
        val totalDx = b.x - a.x
        val totalDz = b.z - a.z
        val absDx = abs(totalDx)
        val absDz = abs(totalDz)
        val steps = max(absDx, absDz)
        if (steps <= 1) return true

        val sx = totalDx.sign
        val sz = totalDz.sign
        val y = a.y
        var x = a.x
        var z = a.z
        var errX = 0
        var errZ = 0

        for (i in 1..steps) {
            errX += absDx
            errZ += absDz
            var stepX = 0
            var stepZ = 0
            if (errX >= steps) { stepX = sx; errX -= steps }
            if (errZ >= steps) { stepZ = sz; errZ -= steps }

            val nx = x + stepX
            val nz = z + stepZ
            if (!ctx.isChunkLoadedAt(nx, nz)) return false
            if (!ctx.isWalkableAt(nx, y, nz)) return false

            if (stepX != 0 && stepZ != 0) {
                if (!ctx.isPassableAt(x + stepX, y, z)) return false
                if (!ctx.isPassableAt(x, y, z + stepZ)) return false
                if (!ctx.isPassableAt(x + stepX, y + 1, z)) return false
                if (!ctx.isPassableAt(x, y + 1, z + stepZ)) return false
            }

            x = nx
            z = nz
        }
        return true
    }

    private fun passable(state: BlockState, level: LevelReader, pos: BlockPos): Boolean =
        state.fluidState.isEmpty &&
        state.getCollisionShape(level, pos, CollisionContext.empty()).isEmpty

    private fun standableGround(state: BlockState, level: LevelReader, pos: BlockPos): Boolean {
        if (!state.fluidState.isEmpty) return false
        val shape = state.getCollisionShape(level, pos, CollisionContext.empty())
        if (shape.isEmpty) return false
        return shape.max(Direction.Axis.Y) <= MAX_GROUND_HEIGHT
    }

    private class Node(
        val pos: BlockPos,
        val posLong: Long,
        val g: Double,
        val h: Double,
        val parent: Node?,
        val dx: Int,
        val dz: Int
    ) {
        val f: Double get() = g + h
    }
}
