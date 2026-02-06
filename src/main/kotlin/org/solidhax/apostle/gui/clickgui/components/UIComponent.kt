package org.solidhax.apostle.gui.clickgui.components

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.input.MouseButtonEvent

abstract class UIComponent(
    var parent: UIComponent? = null,
    var x: Float,
    var y: Float,
    var width: Float,
    var height: Float,
) {

    open fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {}
    open fun mouseClicked(mouseButtonEvent: MouseButtonEvent, bl: Boolean) {}
    open fun mouseReleased(mouseButtonEvent: MouseButtonEvent) {}

    fun absoluteX(): Float = (parent?.absoluteX() ?: 0f) + x
    fun absoluteY(): Float = (parent?.absoluteY() ?: 0f) + y

    fun intersects(mouseX: Float, mouseY: Float): Boolean {
        return mouseX >= x && mouseX <= absoluteX() + width && mouseY >= absoluteY() && mouseY <= absoluteY() + height
    }
}