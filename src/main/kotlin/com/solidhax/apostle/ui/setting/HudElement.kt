package com.solidhax.apostle.ui.setting

import net.minecraft.client.gui.GuiGraphics

enum class HudAlignment {
    LEFT,
    CENTER,
    RIGHT;

    fun next(): HudAlignment = entries[(ordinal + 1) % entries.size]
}

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
        pose.pushMatrix()
        pose.translate(left().toFloat(), y.toFloat())
        pose.scale(scale, scale)

        val (measuredWidth, measuredHeight) = guiGraphics.render(preview)

        pose.popMatrix()

        width = measuredWidth
        height = measuredHeight
    }

    fun scaledWidth(): Int = (width * scale).toInt()

    fun scaledHeight(): Int = (height * scale).toInt()

    fun left(): Int = when (alignment) {
        HudAlignment.LEFT -> x
        HudAlignment.CENTER -> x - (scaledWidth() / 2)
        HudAlignment.RIGHT -> x - scaledWidth()
    }

    fun right(): Int = left() + scaledWidth()

    fun setLeft(left: Int) {
        x = when (alignment) {
            HudAlignment.LEFT -> left
            HudAlignment.CENTER -> left + (scaledWidth() / 2)
            HudAlignment.RIGHT -> left + scaledWidth()
        }
    }
}
