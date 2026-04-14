package com.solidhax.apostle.ui

import com.solidhax.apostle.modules.internal.Category
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.network.chat.Component
import kotlin.math.max

object ConfigScreen : Screen(Component.literal("Apostle")) {

    private val panels = Category.entries.map { category -> Panel(category, 0f, 0f) }.toMutableList()

    override fun init() {
        super.init()
        layoutPanels()
    }

    override fun resize(height: Int, j: Int) {
        super.resize(width, height)
        layoutPanels()
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, deltaTracker: Float) {
        var hoveredEntry: Panel.HoveredEntry? = null
        panels.forEach { panel ->
            hoveredEntry = panel.render(guiGraphics, mouseX.toFloat(), mouseY.toFloat()) ?: hoveredEntry
        }

        super.render(guiGraphics, mouseX, mouseY, deltaTracker)
    }

    override fun renderBackground(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) { return }

    override fun mouseClicked(event: MouseButtonEvent, doubleClick: Boolean): Boolean {
        panels.asReversed().firstOrNull { it.contains(event.x(), event.y()) }?.let { panel ->
            panels.remove(panel)
            panels.add(panel)
            return panel.mouseClicked(event)
        }

        return super.mouseClicked(event, doubleClick)
    }

    override fun mouseDragged(event: MouseButtonEvent, dragX: Double, dragY: Double): Boolean {
        panels.asReversed().firstOrNull { it.dragging }?.let { panel ->
            return if (panel.mouseDragged(event, dragX, dragY)) {
                true
            } else {
                super.mouseDragged(event, dragX, dragY)
            }
        }

        panels.asReversed().forEach { panel ->
            if (panel.mouseDragged(event, dragX, dragY)) {
                return true
            }
        }

        return super.mouseDragged(event, dragX, dragY)
    }

    override fun mouseReleased(event: MouseButtonEvent): Boolean {
        panels.asReversed().firstOrNull { it.dragging }?.let { panel ->
            return if (panel.mouseReleased(event)) {
                true
            } else {
                super.mouseReleased(event)
            }
        }

        panels.asReversed().forEach { panel ->
            if (panel.mouseReleased(event)) {
                return true
            }
        }

        return super.mouseReleased(event)
    }

    private fun layoutPanels() {
        if (width <= 0) {
            return
        }

        val panelWidth = panels.firstOrNull()?.widthPx ?: ClickGuiStyle.PANEL_WIDTH
        val availableWidth = (width - ClickGuiStyle.SCREEN_PADDING * 2).coerceAtLeast(panelWidth)
        val columns = max(1, (availableWidth + ClickGuiStyle.PANEL_GAP) / (panelWidth + ClickGuiStyle.PANEL_GAP))
        val usedWidth = columns * panelWidth + (columns - 1) * ClickGuiStyle.PANEL_GAP
        val startX = ((width - usedWidth) / 2f).coerceAtLeast(ClickGuiStyle.SCREEN_PADDING.toFloat())

        panels.forEachIndexed { index, panel ->
            val column = index % columns
            val row = index / columns
            panel.x = startX + column * (panelWidth + ClickGuiStyle.PANEL_GAP)
            panel.y = (ClickGuiStyle.PANEL_TOP + row * 164).toFloat()
        }
    }
}
