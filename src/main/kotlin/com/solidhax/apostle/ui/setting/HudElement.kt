package com.solidhax.apostle.ui.setting

import com.solidhax.apostle.Apostle.mc
import net.minecraft.client.gui.GuiGraphics

class HudElement(
    var x: Int,
    var y: Int,
    var scale: Float,
    var enabled: Boolean = true,
    var alignment: HudAlignment = HudAlignment.LEFT,
    val render: GuiGraphics.(Boolean) -> Pair<Int, Int>
) {
    var width: Int = 0
        private set
    var height: Int = 0
        private set

    fun draw(guiGraphics: GuiGraphics, preview: Boolean = false) {
        val pose = guiGraphics.pose()
        val drawX = resolvedX()
        pose.pushMatrix()
        pose.translate(drawX.toFloat(), y.toFloat())
        pose.scale(scale, scale)

        val (measuredWidth, measuredHeight) = guiGraphics.render(preview)

        pose.popMatrix()

        width = measuredWidth
        height = measuredHeight
    }

    fun resolvedX(screenWidth: Int = mc.window.guiScaledWidth): Int {
        val scaledWidth = scaledWidth()
        return when (alignment) {
            HudAlignment.LEFT -> x
            HudAlignment.CENTER -> (screenWidth / 2f - scaledWidth / 2f + x).toInt()
            HudAlignment.RIGHT -> screenWidth - scaledWidth - x
        }
    }

    fun scaledWidth(): Int = (width * scale).toInt()

    fun scaledHeight(): Int = (height * scale).toInt()
}
