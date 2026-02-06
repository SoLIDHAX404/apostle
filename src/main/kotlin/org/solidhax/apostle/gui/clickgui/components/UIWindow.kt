package org.solidhax.apostle.gui.clickgui.components

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.input.MouseButtonEvent
import org.solidhax.apostle.Apostle
import org.solidhax.apostle.utils.mouse.cursorX
import org.solidhax.apostle.utils.mouse.cursorY
import org.solidhax.apostle.utils.render.Colors
import org.solidhax.apostle.utils.render.NVGRenderer

class UIWindow(x: Float, y: Float, width: Float, height: Float, val title: String) : UIComponent(null, x, y, width, height) {
    private val children = mutableListOf<UIComponent>()

    private val headerWidth = width
    private val headerHeight = height / 10

    private var dragOffsetX = 0f
    private var dragOffsetY = 0f
    private var dragging = false

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {

        if(dragging) {
            x = cursorX - dragOffsetX
            y = cursorY - dragOffsetY
        }

        NVGRenderer.filledRect(x, y, width, height, Colors.UI_BACKGROUND, 5f)
        NVGRenderer.rect(x - 1, y - 1, width + 1, height + 1, 2f, Colors.UI_SURFACE, 5f)

        super.render(guiGraphics, mouseX, mouseY, delta)
    }

    override fun mouseClicked(mouseButtonEvent: MouseButtonEvent, bl: Boolean) {
        if(mouseButtonEvent.button() == 0) {
            if(intersects(cursorX, cursorY)) {
                dragging = true

                dragOffsetX = cursorX - x
                dragOffsetY = cursorY - y
            }
        }

        super.mouseClicked(mouseButtonEvent, bl)
    }

    override fun mouseReleased(mouseButtonEvent: MouseButtonEvent) {
        if(mouseButtonEvent.button() == 0)
            dragging = false

        super.mouseReleased(mouseButtonEvent)
    }
}