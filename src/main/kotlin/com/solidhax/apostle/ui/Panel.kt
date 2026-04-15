package com.solidhax.apostle.ui

import com.solidhax.apostle.modules.internal.Category
import com.solidhax.apostle.modules.internal.Module
import com.solidhax.apostle.modules.internal.ModuleManager
import com.solidhax.apostle.ui.setting.RenderableSetting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.input.CharacterEvent
import net.minecraft.client.input.KeyEvent
import net.minecraft.client.input.MouseButtonEvent
import kotlin.math.floor

class Panel(private val category: Category, var x: Float, var y: Float) {

    data class HoveredEntry(val title: String, val description: String)

    private val width = ClickGuiStyle.PANEL_WIDTH
    private val headerHeight = ClickGuiStyle.PANEL_HEADER_HEIGHT
    private val rowHeight = ClickGuiStyle.MODULE_ROW_HEIGHT
    private val moduleTopPadding = 8
    private val panelBottomPadding = 8
    private val accent = ClickGuiStyle.accent(category)
    private val accentSoft = ClickGuiStyle.accentSoft(category)

    var dragging = false
        private set

    var collapsed = false
        private set

    private var deltaX = 0f
    private var deltaY = 0f

    val widthPx: Int
        get() = width

    private val height: Int
        get() {
            if (collapsed) {
                return headerHeight
            }

            val modules = ModuleManager.modulesByCategory[category].orEmpty()
            if (modules.isEmpty()) {
                return headerHeight + moduleTopPadding + panelBottomPadding
            }

            return headerHeight + moduleTopPadding + modules.sumOf { moduleHeight(it) } + panelBottomPadding
        }

    fun render(guiGraphics: GuiGraphics, mouseX: Float, mouseY: Float): HoveredEntry? {
        if (dragging) {
            x = floor(deltaX + mouseX)
            y = floor(deltaY + mouseY)
        }

        val left = x.toInt()
        val top = y.toInt()
        val right = left + width
        val bottom = top + height
        val font = Minecraft.getInstance().font
        val modules = ModuleManager.modulesByCategory[category].orEmpty()
        var hoveredEntry: HoveredEntry? = null

        guiGraphics.fill(left + 3, top + 4, right + 3, bottom + 4, ClickGuiStyle.SHADOW)
        guiGraphics.fill(left, top, right, bottom, ClickGuiStyle.CARD)
        guiGraphics.fill(left + 1, top + 1, right - 1, bottom - 1, ClickGuiStyle.CARD_RAISED)
        guiGraphics.fill(left, top, right, top + headerHeight, ClickGuiStyle.withAlpha(accent, 210))
        guiGraphics.fill(left, top + headerHeight, right, top + headerHeight + 1, accentSoft)
        guiGraphics.fill(left, top, right, top + 1, ClickGuiStyle.BORDER_BRIGHT)
        guiGraphics.fill(left, bottom - 1, right, bottom, ClickGuiStyle.BORDER)
        guiGraphics.fill(left, top, left + 1, bottom, ClickGuiStyle.BORDER)
        guiGraphics.fill(right - 1, top, right, bottom, ClickGuiStyle.BORDER)

        val countLabel = modules.size.toString()
        guiGraphics.drawString(font, category.displayName, left + 8, top + 8, ClickGuiStyle.TITLE, false)
        guiGraphics.drawString(font, countLabel, right - 10 - font.width(countLabel), top + 8, ClickGuiStyle.TITLE, false)

        if (isOverHeader(mouseX.toDouble(), mouseY.toDouble())) {
            hoveredEntry = HoveredEntry(category.displayName, "Drag to move. Right click the header to collapse this category.")
        }

        if (collapsed) {
            return hoveredEntry
        }

        if (modules.isEmpty()) {
            return hoveredEntry
        }

        var currentTop = top + headerHeight + moduleTopPadding
        modules.forEach { module ->
            val rowTop = currentTop
            val rowBottom = rowTop + rowHeight
            val textY = rowTop + 6
            val rowHovered = mouseX.toInt() in (left + 6) until (right - 6) && mouseY.toInt() in rowTop until rowBottom
            val backgroundColor = if (module.enabled) ClickGuiStyle.withAlpha(accent, 72) else 0x60303F4E

            guiGraphics.fill(left + 6, rowTop, right - 6, rowBottom, backgroundColor)
            if (module.enabled) {
                guiGraphics.fill(left + 6, rowTop, left + 8, rowBottom, accent)
            }

            guiGraphics.drawString(font, module.name, left + 12, textY, ClickGuiStyle.TITLE, false)

            if (module.settings.isNotEmpty()) {
                guiGraphics.drawString(font, if (module.expanded) "-" else "+", right - 14, textY, ClickGuiStyle.SOFT_TEXT, false)
            }

            if (rowHovered) {
                hoveredEntry = HoveredEntry(
                    module.name,
                    module.description.ifBlank { "Left click to toggle. Right click to expand module settings." }
                )
            }

            currentTop += rowHeight

            if (!module.expanded) {
                return@forEach
            }

            visibleRenderableSettings(module).forEach { setting ->
                setting.render(guiGraphics, (left + 6).toFloat(), currentTop.toFloat(), mouseX, mouseY)

                val settingBottom = currentTop + setting.height
                val settingHovered = mouseX.toInt() in (left + 6) until (right - 6) &&
                    mouseY.toInt() in currentTop until settingBottom
                if (settingHovered) {
                    hoveredEntry = HoveredEntry(
                        setting.name,
                        setting.description.ifBlank { "Left click to change this setting." }
                    )
                }

                currentTop += setting.height
            }
        }

        return hoveredEntry
    }

    fun contains(mouseX: Double, mouseY: Double): Boolean {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height
    }

    fun isOverHeader(mouseX: Double, mouseY: Double): Boolean {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + headerHeight
    }

    private fun visibleRenderableSettings(module: Module): List<RenderableSetting<*>> =
        module.settings.values.mapNotNull { setting ->
            (setting as? RenderableSetting<*>)?.takeIf { it.isVisible }
        }

    private fun moduleHeight(module: Module): Int {
        val settingsHeight = if (module.expanded) visibleRenderableSettings(module).sumOf { it.height } else 0
        return rowHeight + settingsHeight
    }

    private fun findModuleRow(mouseX: Double, mouseY: Double): Module? {
        if (collapsed || !contains(mouseX, mouseY) || isOverHeader(mouseX, mouseY)) {
            return null
        }

        var currentTop = y.toInt() + headerHeight + moduleTopPadding
        for (module in ModuleManager.modulesByCategory[category].orEmpty()) {
            val rowBottom = currentTop + rowHeight
            if (mouseY >= currentTop && mouseY <= rowBottom) {
                return module
            }
            currentTop += moduleHeight(module)
        }

        return null
    }

    private fun clickSettingAt(mouseX: Double, mouseY: Double, event: MouseButtonEvent): Boolean {
        if (collapsed || !contains(mouseX, mouseY) || isOverHeader(mouseX, mouseY)) {
            return false
        }

        var currentTop = y.toInt() + headerHeight + moduleTopPadding
        for (module in ModuleManager.modulesByCategory[category].orEmpty()) {
            currentTop += rowHeight
            if (module.expanded) {
                for (setting in visibleRenderableSettings(module)) {
                    val rowBottom = currentTop + setting.height
                    if (mouseY >= currentTop && mouseY <= rowBottom) {
                        return setting.mouseClicked(mouseX.toFloat(), mouseY.toFloat(), event)
                    }
                    currentTop += setting.height
                }
            }
        }

        return false
    }

    private fun dragSettings(mouseX: Double, mouseY: Double, event: MouseButtonEvent): Boolean {
        if (collapsed) {
            return false
        }

        for (module in ModuleManager.modulesByCategory[category].orEmpty()) {
            if (!module.expanded) {
                continue
            }

            for (setting in visibleRenderableSettings(module)) {
                if (setting.mouseDragged(mouseX.toFloat(), mouseY.toFloat(), event)) {
                    return true
                }
            }
        }

        return false
    }

    private fun releaseSettings(event: MouseButtonEvent): Boolean {
        if (collapsed) {
            return false
        }

        var handled = false
        for (module in ModuleManager.modulesByCategory[category].orEmpty()) {
            if (!module.expanded) {
                continue
            }

            for (setting in visibleRenderableSettings(module)) {
                setting.mouseReleased(event)
                handled = true
            }
        }

        return handled
    }

    fun charTyped(characterEvent: CharacterEvent): Boolean {
        if (collapsed) {
            return false
        }

        for (module in ModuleManager.modulesByCategory[category].orEmpty()) {
            if (!module.expanded) {
                continue
            }

            for (setting in visibleRenderableSettings(module)) {
                if (setting.charTyped(characterEvent)) {
                    return true
                }
            }
        }

        return false
    }

    fun keyPressed(keyEvent: KeyEvent): Boolean {
        if (collapsed) {
            return false
        }

        for (module in ModuleManager.modulesByCategory[category].orEmpty()) {
            if (!module.expanded) {
                continue
            }

            for (setting in visibleRenderableSettings(module)) {
                if (setting.keyPressed(keyEvent)) {
                    return true
                }
            }
        }

        return false
    }

    fun mouseClicked(event: MouseButtonEvent): Boolean {
        if (!contains(event.x(), event.y())) {
            return false
        }

        if (event.button() == 1 && isOverHeader(event.x(), event.y())) {
            toggleCollapsed()
            return true
        }

        if (event.button() != 0) {
            if (event.button() == 1) {
                findModuleRow(event.x(), event.y())?.let { module ->
                    if (module.settings.isNotEmpty()) {
                        module.toggleExpanded()
                    }
                }
            }
            return true
        }

        if (clickSettingAt(event.x(), event.y(), event)) {
            return true
        }

        findModuleRow(event.x(), event.y())?.let { module ->
            module.toggle()
            return true
        }

        if (isOverHeader(event.x(), event.y())) {
            startDragging(event.x(), event.y())
        }

        return true
    }

    fun mouseDragged(event: MouseButtonEvent, dragX: Double, dragY: Double): Boolean {
        if (event.button() == 0 && dragging) {
            return true
        }

        return dragSettings(event.x(), event.y(), event)
    }

    fun mouseReleased(event: MouseButtonEvent): Boolean {
        val releasedSetting = releaseSettings(event)

        if (event.button() == 0 && dragging) {
            stopDragging()
            return true
        }

        return releasedSetting
    }

    fun startDragging(mouseX: Double, mouseY: Double) {
        dragging = true
        deltaX = (x - mouseX).toFloat()
        deltaY = (y - mouseY).toFloat()
    }

    fun toggleCollapsed() {
        collapsed = !collapsed
    }

    fun stopDragging() {
        dragging = false
    }
}
