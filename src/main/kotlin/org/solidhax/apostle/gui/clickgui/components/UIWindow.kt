package org.solidhax.apostle.gui.clickgui.components

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.input.MouseButtonEvent
import org.solidhax.apostle.utils.mouse.cursorX
import org.solidhax.apostle.utils.mouse.cursorY
import org.solidhax.apostle.utils.render.Colors
import org.solidhax.apostle.utils.render.NVGRenderer

class UIWindow(x: Float, y: Float, width: Float, height: Float, val title: String) : UIComponent(x, y, width, height) {
    private val children = mutableListOf<UIComponent>()

    private var dragOffsetX = 0f
    private var dragOffsetY = 0f
    private var dragging = false

    fun addChild(component: UIComponent) {
        component.parent = this
        children.add(component)
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {

        if(dragging) {
            x = cursorX - dragOffsetX
            y = cursorY - dragOffsetY
        }

        NVGRenderer.filledRect(x, y, width, height, Colors.UI_BACKGROUND, 5f)
        NVGRenderer.rect(x, y, width, height, 2f, Colors.UI_SURFACE, 5f)

        children.forEach { it.render(guiGraphics, mouseX, mouseY, delta) }
    }

    override fun mouseClicked(mouseButtonEvent: MouseButtonEvent, bl: Boolean) {
        if(mouseButtonEvent.button() == 0) {
            if(intersects(cursorX, cursorY)) {
                dragging = true

                dragOffsetX = cursorX - x
                dragOffsetY = cursorY - y
            }
        }

        children.forEach { it.mouseClicked(mouseButtonEvent, bl) }
    }

    override fun mouseReleased(mouseButtonEvent: MouseButtonEvent) {
        if(mouseButtonEvent.button() == 0)
            dragging = false

        children.forEach { it.mouseReleased(mouseButtonEvent) }
    }
}