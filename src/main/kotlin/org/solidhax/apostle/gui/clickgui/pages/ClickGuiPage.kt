package org.solidhax.apostle.gui.clickgui.pages

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.input.MouseButtonEvent

data class UIBounds(val x: Float, val y: Float, val width: Float, val height: Float) {
    fun contains(pointX: Float, pointY: Float): Boolean {
        return pointX >= x && pointX <= x + width && pointY >= y && pointY <= y + height
    }
}

interface ClickGuiPage {
    fun render(guiGraphics: GuiGraphics, bounds: UIBounds, mouseX: Int, mouseY: Int, delta: Float)
    fun mouseClicked(mouseButtonEvent: MouseButtonEvent, bounds: UIBounds) {}
    fun mouseReleased(mouseButtonEvent: MouseButtonEvent, bounds: UIBounds) {}
}
