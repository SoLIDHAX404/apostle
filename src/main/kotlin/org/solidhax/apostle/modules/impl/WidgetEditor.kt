package org.solidhax.apostle.modules.impl

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.network.chat.Component
import org.solidhax.apostle.Apostle.Companion.mc
import org.solidhax.apostle.utils.render.Colors
import org.solidhax.apostle.utils.mouseX as apostleMouseX
import org.solidhax.apostle.utils.mouseY as apostleMouseY
import kotlin.math.sign

object WidgetEditor : Screen(Component.literal("Widget Editor")) {

    private var dragging: HudElement? = null

    private var deltaX = 0f
    private var deltaY = 0f

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, deltaTracker: Float) {
        super.render(guiGraphics, mouseX, mouseY, deltaTracker)

        dragging?.let {
            it.x = (apostleMouseX + deltaX).coerceIn(0f, (mc.window.width - (it.width * it.scale))).toInt()
            it.y = (apostleMouseY + deltaY).coerceIn(0f, (mc.window.height - (it.height * it.scale))).toInt()
        }

        guiGraphics.pose()?.pushMatrix()
        val sf = mc.window.guiScale
        guiGraphics.pose().scale(1f / sf, 1f / sf)

        for (widget in ModuleManager.widgets) {
            widget.draw(guiGraphics, true)
        }

        ModuleManager.widgets.firstOrNull { it.isHovered() }?.let { hoveredHud ->
            guiGraphics.pose().pushMatrix()
            guiGraphics.pose().translate(
                (hoveredHud.x + hoveredHud.width * hoveredHud.scale + 10f),
                hoveredHud.y.toFloat(),
            )
            guiGraphics.pose().scale(2f, 2f)
            guiGraphics.drawString(mc.font, hoveredHud.owner?.name, 0, 0, Colors.WHITE.rgba)
            guiGraphics.drawString(mc.font, Component.literal("%.2f".format(hoveredHud.scale)), 0, 10, Colors.WHITE.rgba)
            guiGraphics.pose().popMatrix()
        }

        guiGraphics.pose().popMatrix()
    }

    override fun mouseClicked(event: MouseButtonEvent, bl: Boolean): Boolean {
        ModuleManager.widgets.firstOrNull { it.isHovered() }?.let { hovered ->
            dragging = hovered

            deltaX = (hovered.x - apostleMouseX)
            deltaY = (hovered.y - apostleMouseY)
            return true
        }

        return super.mouseClicked(event, bl)
    }

    override fun mouseReleased(event: MouseButtonEvent): Boolean {
        dragging = null
        return super.mouseReleased(event)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, horizontalAmount: Double, verticalAmount: Double): Boolean {
        val actualAmount = verticalAmount.sign.toFloat() * 0.2f

        ModuleManager.widgets.firstOrNull { it.isHovered() }?.let { hovered ->
            hovered.scale = (hovered.scale + actualAmount).coerceIn(1f, 10f)
            return true
        }

        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)
    }

    override fun onClose() {
        ModuleManager.saveHudPositions()
        super.onClose()
    }

    override fun isPauseScreen(): Boolean = false
}
