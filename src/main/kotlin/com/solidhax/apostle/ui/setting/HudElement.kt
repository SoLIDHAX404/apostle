package com.solidhax.apostle.ui.setting

import net.minecraft.client.gui.GuiGraphics

class HudElement(
    var x: Int,
    var y: Int,
    var scale: Float,
    var enabled: Boolean = true,
    val render: GuiGraphics.(Boolean) -> Pair<Int, Int>
) {
    var width: Int = 0
        private set
    var height: Int = 0
        private set

    fun draw(guiGraphics: GuiGraphics, preview: Boolean = false) {
        val pose = guiGraphics.pose()
        pose.pushMatrix()
        pose.translate(x.toFloat(), y.toFloat())
        pose.scale(scale, scale)

        val (measuredWidth, measuredHeight) = guiGraphics.render(preview)

        pose.popMatrix()

        width = measuredWidth
        height = measuredHeight
    }
}
