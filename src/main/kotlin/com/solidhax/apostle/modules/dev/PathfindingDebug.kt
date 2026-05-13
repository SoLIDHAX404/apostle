package com.solidhax.apostle.modules.dev

import com.solidhax.apostle.Apostle.mc
import com.solidhax.apostle.events.RenderEvent
import com.solidhax.apostle.events.TickEvent
import com.solidhax.apostle.modules.internal.Category
import com.solidhax.apostle.modules.internal.Module
import com.solidhax.apostle.utils.Pathfinder
import com.solidhax.apostle.utils.Pathfinder.PathResult
import com.solidhax.apostle.utils.drawLine
import com.solidhax.apostle.utils.drawWireFrameBox
import com.solidhax.apostle.utils.playerMessage
import meteordevelopment.orbit.EventHandler
import net.minecraft.core.BlockPos
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import java.awt.Color

object PathfindingDebug : Module("Pathfinding Debug", "Visualises paths to a target block", Category.DEV) {

    private const val PARTIAL_RECOMPUTE_INTERVAL = 8

    private var finalGoal: BlockPos? = null
    private var currentPath: List<BlockPos> = emptyList()
    private var isPartial = false
    private var dirty = false
    private var ticksSinceRecompute = 0
    private var lastCheckpointKey: Long = Long.MIN_VALUE

    private val pathColor = Color(0, 200, 255, 200)
    private val partialPathColor = Color(255, 165, 0, 200)
    private val targetColor = Color(255, 80, 80, 200)
    private val checkpointColor = Color(255, 200, 0, 200)
    private val nodeColor = Color(0, 255, 120, 80)

    fun setTarget(pos: BlockPos) {
        finalGoal = pos
        currentPath = emptyList()
        isPartial = false
        dirty = true
        ticksSinceRecompute = 0
        lastCheckpointKey = Long.MIN_VALUE
        playerMessage("§7Target set to §e(${pos.x}, ${pos.y}, ${pos.z})§7.")
    }

    fun clearTarget() {
        finalGoal = null
        currentPath = emptyList()
        isPartial = false
        dirty = false
        playerMessage("§7Target cleared.")
    }

    init {
        @EventHandler
        fun onTickEnd(event: TickEvent.End) {
            if (!enabled) return
            val level = mc.level ?: return
            val player = mc.player ?: return
            val goal = finalGoal ?: return

            ticksSinceRecompute++
            val shouldRecompute = dirty ||
                (isPartial && ticksSinceRecompute >= PARTIAL_RECOMPUTE_INTERVAL)
            if (!shouldRecompute) return
            if (!player.onGround()) return

            ticksSinceRecompute = 0
            dirty = false

            when (val result = Pathfinder.findPath(player.blockPosition(), goal, level)) {
                null -> {
                    if (currentPath.isNotEmpty()) {
                        playerMessage("§cLost path: nowhere walkable from current position.")
                    }
                    currentPath = emptyList()
                    isPartial = false
                    lastCheckpointKey = Long.MIN_VALUE
                }
                is PathResult.Complete -> {
                    val firstComplete = isPartial || currentPath.isEmpty()
                    currentPath = result.path
                    isPartial = false
                    lastCheckpointKey = Long.MIN_VALUE
                    if (firstComplete) {
                        playerMessage("§aPath complete: §e${result.path.size}§a nodes, §e${result.iterations}§a iterations.")
                    }
                }
                is PathResult.Partial -> {
                    currentPath = result.path
                    isPartial = true
                    val cp = result.checkpoint
                    val key = cp.asLong()
                    if (key != lastCheckpointKey) {
                        playerMessage("§ePartial: §e${result.path.size}§e nodes to §e(${cp.x}, ${cp.y}, ${cp.z})§e (${result.iterations} iter).")
                        lastCheckpointKey = key
                    }
                }
            }
        }

        @EventHandler
        fun onRenderExtract(event: RenderEvent.Extract) {
            if (!enabled) return

            finalGoal?.let { event.drawWireFrameBox(AABB(it), targetColor, thickness = 2f, depth = false) }

            val path = currentPath
            if (path.size < 2) return

            val color = if (isPartial) partialPathColor else pathColor
            event.drawLine(path.map { it.center() }, color, depth = false, thickness = 2f)
            path.forEach { event.drawWireFrameBox(AABB(it), nodeColor, thickness = 1f, depth = false) }
            if (isPartial) {
                event.drawWireFrameBox(AABB(path.last()), checkpointColor, thickness = 2f, depth = false)
            }
        }
    }

    private fun BlockPos.center() = Vec3(x + 0.5, y + 0.5, z + 0.5)
}
