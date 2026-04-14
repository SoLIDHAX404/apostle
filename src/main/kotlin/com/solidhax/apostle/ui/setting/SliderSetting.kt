package com.solidhax.apostle.ui.setting

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.solidhax.apostle.ui.ClickGuiStyle
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.input.MouseButtonEvent
import kotlin.math.round

class SliderSetting(
    name: String,
    description: String = "",
    override val default: Float,
    private val min: Float,
    private val max: Float,
    private val step: Float = 0.1f
) : RenderableSetting<Float>(name, description), Savable {
    override val height: Int = 30

    init {
        require(max > min) { "max must be greater than min" }
        require(step > 0f) { "step must be greater than 0" }
    }

    override var value: Float = default.coerceIn(min, max)
        set(newValue) {
            field = snap(newValue.coerceIn(min, max))
        }

    private var sliding = false

    override fun render(guiGraphics: GuiGraphics, x: Float, y: Float, mouseX: Float, mouseY: Float) {
        lastX = x
        lastY = y

        val left = x.toInt()
        val top = y.toInt()
        val right = left + width
        val bottom = top + height
        val hovered = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height
        val font = Minecraft.getInstance().font
        val barLeft = left + 8
        val barRight = right - 8
        val barTop = bottom - 8
        val barBottom = barTop + 3
        val progress = ((value - min) / (max - min)).coerceIn(0f, 1f)
        val fillRight = (barLeft + ((barRight - barLeft) * progress)).toInt()
        val knobCenter = fillRight.coerceIn(barLeft, barRight)
        val valueLabel = formatValue(value)

        guiGraphics.fill(left, top, right, bottom, if (hovered || sliding) 0x70334759 else 0x50273444)
        guiGraphics.drawString(font, name, left + 8, top + 5, ClickGuiStyle.SOFT_TEXT, false)
        guiGraphics.drawString(font, valueLabel, right - 8 - font.width(valueLabel), top + 5, ClickGuiStyle.TITLE, false)

        guiGraphics.fill(barLeft, barTop, barRight, barBottom, 0x90404C57.toInt())
        guiGraphics.fill(barLeft, barTop, fillRight, barBottom, ClickGuiStyle.withAlpha(ClickGuiStyle.SUCCESS, 180))
        guiGraphics.fill(knobCenter - 2, barTop - 2, knobCenter + 2, barBottom + 2, ClickGuiStyle.TITLE)
    }

    override fun mouseClicked(mouseX: Float, mouseY: Float, click: MouseButtonEvent): Boolean {
        if (click.button() != 0 || !isInside(mouseX, mouseY)) {
            return false
        }

        sliding = true
        updateFromMouse(mouseX)
        return true
    }

    override fun mouseDragged(mouseX: Float, mouseY: Float, click: MouseButtonEvent): Boolean {
        if (click.button() != 0 || !sliding) {
            return false
        }

        updateFromMouse(mouseX)
        return true
    }

    override fun mouseReleased(click: MouseButtonEvent) {
        if (click.button() == 0) {
            sliding = false
        }
    }

    private fun isInside(mouseX: Float, mouseY: Float): Boolean {
        return mouseX >= lastX && mouseX <= lastX + width &&
            mouseY >= lastY && mouseY <= lastY + height
    }

    private fun updateFromMouse(mouseX: Float) {
        val barLeft = lastX + 8f
        val barRight = lastX + width - 8f
        val progress = ((mouseX - barLeft) / (barRight - barLeft)).coerceIn(0f, 1f)
        value = min + (max - min) * progress
    }

    private fun snap(raw: Float): Float {
        val steps = round((raw - min) / step)
        return min + steps * step
    }

    private fun formatValue(current: Float): String {
        val rounded = snap(current)
        return if (step >= 1f) {
            rounded.toInt().toString()
        } else {
            String.format(java.util.Locale.US, "%.2f", rounded).trimEnd('0').trimEnd('.')
        }
    }

    override fun read(element: JsonElement, gson: Gson) {
        value = element.asFloat
    }

    override fun write(gson: Gson): JsonElement = JsonPrimitive(value)
}
