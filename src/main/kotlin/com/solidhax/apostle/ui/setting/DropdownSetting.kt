package com.solidhax.apostle.ui.setting

import com.solidhax.apostle.ui.ClickGuiStyle
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.input.MouseButtonEvent

class DropdownSetting<T>(
    name: String,
    description: String = "",
    override val default: T,
    private val options: List<T>,
    private val multiSelect: Boolean = false,
    private val emptyLabel: String = "None",
    private val formatter: (T) -> String = { it.toString() }
) : RenderableSetting<T>(name, description) {

    init {
        require(options.isNotEmpty()) { "options must not be empty" }
        require(options.contains(default)) { "default must be one of the options" }
    }

    override var value: T = default
    private var expanded = false
    private val selectedOptions = linkedSetOf(default)
    val hasSelection: Boolean
        get() = selectedOptions.isNotEmpty()
    val selectedValueOrNull: T?
        get() = selectedOptions.firstOrNull()
    val selectedValues: Set<T>
        get() = selectedOptions.toSet()
    private val headerHeight = 22
    private val optionHeight = ClickGuiStyle.SETTING_ROW_HEIGHT
    private val rowInset = 4

    override val height: Int
        get() = headerHeight + if (expanded) options.size * optionHeight else 0

    override fun render(guiGraphics: GuiGraphics, x: Float, y: Float, mouseX: Float, mouseY: Float) {
        lastX = x
        lastY = y

        val font = Minecraft.getInstance().font
        val left = x.toInt()
        val top = y.toInt()
        val right = left + width
        val headerBottom = top + headerHeight
        val hoveredHeader = isWithin(mouseX, mouseY, top, headerBottom)
        val currentLabel = if (multiSelect) {
            selectedOptions.joinToString(", ") { formatter(it) }.ifBlank { emptyLabel }
        } else {
            selectedValueOrNull?.let(formatter) ?: emptyLabel
        }
        val labelColor = if (hasSelection) ClickGuiStyle.TITLE else ClickGuiStyle.MUTED_TEXT

        guiGraphics.fill(left, top, right, headerBottom, if (hoveredHeader) 0x70334759 else 0x50273444)
        guiGraphics.drawString(font, name, left + 8, top + 7, ClickGuiStyle.SOFT_TEXT, false)
        guiGraphics.drawString(font, currentLabel, right - 22 - font.width(currentLabel), top + 7, labelColor, false)
        guiGraphics.drawString(font, if (expanded) "-" else "+", right - 12, top + 7, ClickGuiStyle.SOFT_TEXT, false)

        if (!expanded) {
            return
        }

        options.forEachIndexed { index, option ->
            val optionTop = headerBottom + index * optionHeight
            val optionBottom = optionTop + optionHeight
            val hoveredOption = isWithin(mouseX, mouseY, optionTop, optionBottom)
            val selected = if (multiSelect) selectedOptions.contains(option) else selectedValueOrNull == option
            val optionLabel = formatter(option)

            guiGraphics.fill(
                left,
                optionTop,
                right,
                optionBottom,
                when {
                    hoveredOption -> 0x70334759
                    selected -> 0x60334759
                    else -> 0x50273444
                }
            )

            guiGraphics.fill(right - 22, optionTop + rowInset, right - 8, optionBottom - rowInset, if (selected) ClickGuiStyle.withAlpha(ClickGuiStyle.SUCCESS, 180) else 0x90404C57.toInt())
            guiGraphics.fill(right - 20, optionTop + rowInset + 1, right - 10, optionBottom - rowInset - 1, if (selected) ClickGuiStyle.TITLE else 0xFF2A3440.toInt())
            guiGraphics.drawString(font, optionLabel, left + 12, optionTop + 5, if (selected) ClickGuiStyle.TITLE else ClickGuiStyle.SOFT_TEXT, false)
        }
    }

    override fun mouseClicked(mouseX: Float, mouseY: Float, click: MouseButtonEvent): Boolean {
        if (click.button() != 0 || !isInside(mouseX, mouseY)) {
            return false
        }

        val relativeY = mouseY - lastY
        if (relativeY <= headerHeight) {
            expanded = !expanded
            return true
        }

        val optionIndex = ((relativeY - headerHeight) / optionHeight).toInt()
        val selected = options.getOrNull(optionIndex) ?: return false
        if (multiSelect) {
            if (selectedOptions.contains(selected)) {
                selectedOptions.remove(selected)
            } else {
                selectedOptions.add(selected)
            }
            value = selectedOptions.firstOrNull() ?: default
        } else {
            if (selectedValueOrNull == selected) {
                selectedOptions.clear()
                value = default
            } else {
                value = selected
                selectedOptions.clear()
                selectedOptions.add(selected)
                expanded = false
            }
        }
        return true
    }

    override fun mouseReleased(click: MouseButtonEvent) = Unit

    private fun isInside(mouseX: Float, mouseY: Float): Boolean {
        return mouseX >= lastX && mouseX <= lastX + width &&
            mouseY >= lastY && mouseY <= lastY + height
    }

    private fun isWithin(mouseX: Float, mouseY: Float, top: Int, bottom: Int): Boolean {
        return mouseX >= lastX && mouseX <= lastX + width &&
            mouseY >= top && mouseY <= bottom
    }
}
