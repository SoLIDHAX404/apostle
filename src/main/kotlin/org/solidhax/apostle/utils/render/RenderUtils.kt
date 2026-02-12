package org.solidhax.apostle.utils.render

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.world.phys.AABB
import org.solidhax.apostle.event.RenderEvent
import org.solidhax.apostle.event.impl.on

private data class OutlineBox(val aabb: AABB, val color: Color)

object RenderUtils {
    private val boxes = ObjectArrayList<OutlineBox>()

    init {
        on<RenderEvent.Last> {
            val matrices = context.matrices() ?: return@on
            val buffers = context.consumers() as? MultiBufferSource.BufferSource ?: return@on
            val camera = context.gameRenderer().mainCamera?.position ?: return@on

            matrices.pushPose()
            matrices.translate(-camera.x, -camera.y, -camera.z)

            val pose = matrices.last()
            val buffer = buffers.getBuffer(RenderLayer.LINE_LIST_ESP)

            for (b in boxes) {
                renderAabbOutline(
                    pose,
                    buffer,
                    b.aabb,
                    b.color.redFloat,
                    b.color.greenFloat,
                    b.color.blueFloat,
                    b.color.alpha
                )
            }

            matrices.popPose()
            buffers.endBatch(RenderLayer.LINE_LIST_ESP)
            boxes.clear()
        }
    }

    fun add(aabb: AABB, color: Color) {
        boxes.add(OutlineBox(aabb, color))
    }
}

private val EDGES = intArrayOf(
    0, 1,  1, 5,  5, 4,  4, 0,
    3, 2,  2, 6,  6, 7,  7, 3,
    0, 3,  1, 2,  5, 6,  4, 7
)

private fun renderAabbOutline(
    pose: PoseStack.Pose,
    buffer: VertexConsumer,
    aabb: AABB,
    r: Float, g: Float, b: Float, a: Float
) {
    val x0 = aabb.minX.toFloat()
    val y0 = aabb.minY.toFloat()
    val z0 = aabb.minZ.toFloat()
    val x1 = aabb.maxX.toFloat()
    val y1 = aabb.maxY.toFloat()
    val z1 = aabb.maxZ.toFloat()

    val corners = floatArrayOf(
        x0, y0, z0,
        x1, y0, z0,
        x1, y1, z0,
        x0, y1, z0,
        x0, y0, z1,
        x1, y0, z1,
        x1, y1, z1,
        x0, y1, z1
    )

    for (i in EDGES.indices step 2) {
        val i0 = EDGES[i] * 3
        val i1 = EDGES[i + 1] * 3

        val ax = corners[i0]
        val ay = corners[i0 + 1]
        val az = corners[i0 + 2]
        val bx = corners[i1]
        val by = corners[i1 + 1]
        val bz = corners[i1 + 2]

        val dx = bx - ax
        val dy = by - ay
        val dz = bz - az

        buffer.addVertex(pose, ax, ay, az).setColor(r, g, b, a).setNormal(pose, dx, dy, dz)
        buffer.addVertex(pose, bx, by, bz).setColor(r, g, b, a).setNormal(pose, dx, dy, dz)
    }
}

fun RenderEvent.Extract.drawOutlineBox(aabb: AABB, color: Color) {
    RenderUtils.add(aabb, color)
}