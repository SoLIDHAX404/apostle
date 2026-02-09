package org.solidhax.apostle.modules.impl

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.network.chat.Component
import org.lwjgl.glfw.GLFW
import org.solidhax.apostle.Apostle.Companion.mc
import kotlin.math.roundToInt

object WidgetEditor : Screen(Component.literal("Widget Editor")) {

    private var hoveredWidget: Widget? = null
    private var draggingWidget: Widget? = null

    private var mouseX: Int = 0
    private var mouseY: Int = 0

    private var dragOffsetX: Int = 0
    private var dragOffsetY: Int = 0

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, deltaTracker: Float) {

        this.mouseX = mouseX
        this.mouseY = mouseY

        hoveredWidget = ModuleManager.widgets.firstOrNull { intersects(it) }

        draggingWidget?.let { widget ->
            widget.x = mouseX - dragOffsetX
            widget.y = mouseY - dragOffsetY
        }

        val widgetName = hoveredWidget?.id ?: "None"
        val editingText = "Currently editing widget: $widgetName"
        val widgetTextWidth = mc.font.width(editingText)

        guiGraphics.drawString(mc.font, editingText, guiGraphics.guiWidth() / 2 - widgetTextWidth / 2, 20, 0xFFFFFFFF.toInt())

        for(widget in ModuleManager.widgets) {
            widget.render(guiGraphics)
        }

        super.render(guiGraphics, mouseX, mouseY, deltaTracker)
    }

    override fun mouseClicked(event: MouseButtonEvent, bl: Boolean): Boolean {
        if (event.input() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {

            hoveredWidget = ModuleManager.widgets.firstOrNull {
                event.x >= it.x && event.x <= it.x + it.width &&
                        event.y >= it.y && event.y <= it.y + it.height
            }

            hoveredWidget?.let {
                draggingWidget = it

                dragOffsetX = mouseX - it.x
                dragOffsetY = mouseY - it.y
                return true
            }
        }

        return super.mouseClicked(event, bl)
    }

    override fun mouseReleased(event: MouseButtonEvent): Boolean {
        if (event.input() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            draggingWidget = null
            return true
        }

        return super.mouseReleased(event)
    }

    override fun onClose() {
        ModuleManager.saveHudPositions()
        super.onClose()
    }

    override fun isPauseScreen(): Boolean = false

    private fun intersects(widget: Widget): Boolean {
        return mouseX >= widget.x && mouseX <= widget.x + widget.width && mouseY >= widget.y && mouseY <= widget.y + widget.height
    }
}
