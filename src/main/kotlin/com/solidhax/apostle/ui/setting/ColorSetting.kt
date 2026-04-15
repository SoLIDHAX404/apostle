package com.solidhax.apostle.ui.setting

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.solidhax.apostle.ui.ClickGuiStyle
import com.solidhax.apostle.ui.TextInputHandler
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.input.CharacterEvent
import net.minecraft.client.input.KeyEvent
import net.minecraft.client.input.MouseButtonEvent
import java.awt.Color
import kotlin.math.roundToInt

class ColorSetting(
    name: String,
    description: String = "",
    override val default: Color = Color.WHITE
) : RenderableSetting<Color>(name, description), Savable {

    private val headerHeight = 22
    private val pickerSize = 48
    private val hueBarHeight = 8
    private val alphaBarHeight = 8
    private val pickerPadding = 6

    override val height: Int
        get() = if (expanded) headerHeight + pickerPadding + pickerSize + 6 + hueBarHeight + 4 + alphaBarHeight + pickerPadding else headerHeight

    override var value: Color = default
        set(newValue) {
            field = newValue
            syncFromValue()
        }

    private var expanded = false
    private var draggingSquare = false
    private var draggingHue = false
    private var draggingAlpha = false
    private var hue = 0f
    private var saturation = 0f
    private var brightness = 1f
    private var alpha = default.alpha / 255f
    private val hexInput = TextInputHandler(
        initialText = formatHex(default),
        filter = { value ->
            value.filter { it.isDigit() || it.lowercaseChar() in 'a'..'f' }.uppercase()
        },
        maxLength = 8,
        onValueChanged = { applyHexInput(it) },
        onSubmit = { applyHexInput(it, true) },
        onCancel = { syncHexField(moveCaretToEnd = true) }
    )

    init {
        syncFromValue()
    }

    override fun render(guiGraphics: GuiGraphics, x: Float, y: Float, mouseX: Float, mouseY: Float) {
        lastX = x
        lastY = y

        val font = Minecraft.getInstance().font
        val left = x.toInt()
        val top = y.toInt()
        val right = left + width
        val headerBottom = top + headerHeight
        val hoveredHeader = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + headerHeight
        val editingHex = hexInput.focused

        guiGraphics.fill(left, top, right, headerBottom, if (hoveredHeader) 0x70334759 else 0x50273444)
        guiGraphics.drawString(font, name, left + 8, top + 7, ClickGuiStyle.SOFT_TEXT, false)
        guiGraphics.fill(right - 34, top + 4, right - 10, top + 18, value.rgb or (value.alpha shl 24))
        guiGraphics.drawString(font, if (expanded) "-" else "+", right - 44, top + 7, ClickGuiStyle.SOFT_TEXT, false)

        if (!expanded) {
            return
        }

        val squareLeft = left + 8
        val squareTop = headerBottom + pickerPadding
        val squareRight = squareLeft + pickerSize
        val squareBottom = squareTop + pickerSize

        renderColorSquare(guiGraphics, squareLeft, squareTop, squareRight, squareBottom)

        val selectorX = squareLeft + (saturation * pickerSize).roundToInt()
        val selectorY = squareTop + ((1f - brightness) * pickerSize).roundToInt()
        guiGraphics.fill(selectorX - 1, selectorY - 1, selectorX + 2, selectorY + 2, 0xFF000000.toInt())
        guiGraphics.fill(selectorX, selectorY, selectorX + 1, selectorY + 1, 0xFFFFFFFF.toInt())

        val hueLeft = squareLeft
        val hueTop = squareBottom + 6
        val hueRight = right - 10
        val hueBottom = hueTop + hueBarHeight
        renderHueBar(guiGraphics, hueLeft, hueTop, hueRight, hueBottom)

        val hueX = hueLeft + (hue * (hueRight - hueLeft)).roundToInt()
        guiGraphics.fill(hueX - 1, hueTop - 2, hueX + 1, hueBottom + 2, 0xFFFFFFFF.toInt())

        val alphaTop = hueBottom + 4
        val alphaBottom = alphaTop + alphaBarHeight
        renderAlphaBar(guiGraphics, hueLeft, alphaTop, hueRight, alphaBottom)

        val alphaX = hueLeft + (alpha * (hueRight - hueLeft)).roundToInt()
        guiGraphics.fill(alphaX - 1, alphaTop - 2, alphaX + 1, alphaBottom + 2, 0xFFFFFFFF.toInt())

        val previewLeft = squareRight + 8
        val previewTop = squareTop
        val previewRight = right - 10
        val previewBottom = squareTop + 18
        guiGraphics.fill(previewLeft, previewTop, previewRight, previewBottom, if (editingHex) 0x70334759 else 0x50273444)
        guiGraphics.fill(previewLeft + 1, previewTop + 1, previewRight - 1, previewBottom - 1, 0x90202B36.toInt())
        hexInput.x = previewLeft.toFloat()
        hexInput.y = previewTop.toFloat()
        hexInput.width = (previewRight - previewLeft).toFloat()
        hexInput.height = (previewBottom - previewTop).toFloat()
        val displayHex = hexInput.text

        hexInput.selectionPixelRange(font)?.let { (selectionLeft, selectionRight) ->
            guiGraphics.fill(
                previewLeft + 4 + selectionLeft,
                previewTop + 4,
                previewLeft + 4 + selectionRight,
                previewBottom - 3,
                0x805A84C9.toInt()
            )
        }

        guiGraphics.drawString(font, displayHex, previewLeft + 4, previewTop + 5, ClickGuiStyle.TITLE, false)
        if (editingHex && !hexInput.hasSelection) {
            val caretX = previewLeft + 4 + hexInput.caretPixelX(font)
            guiGraphics.fill(caretX, previewTop + 4, caretX + 1, previewBottom - 3, ClickGuiStyle.TITLE)
        }
    }

    override fun mouseClicked(mouseX: Float, mouseY: Float, click: MouseButtonEvent): Boolean {
        if (click.button() != 0 || !isInside(mouseX, mouseY)) {
            return false
        }

        val relativeY = mouseY - lastY
        if (relativeY <= headerHeight) {
            hexInput.blur()
            expanded = !expanded
            return true
        }

        if (!expanded) {
            return false
        }

        val squareLeft = lastX + 8f
        val squareTop = lastY + headerHeight + pickerPadding
        val squareRight = squareLeft + pickerSize
        val squareBottom = squareTop + pickerSize
        if (mouseX in squareLeft..squareRight && mouseY in squareTop..squareBottom) {
            hexInput.blur()
            draggingSquare = true
            updateSquare(mouseX, mouseY)
            return true
        }

        val hueLeft = squareLeft
        val hueTop = squareBottom + 6f
        val hueRight = lastX + width - 10f
        val hueBottom = hueTop + hueBarHeight
        if (mouseX in hueLeft..hueRight && mouseY in hueTop..hueBottom) {
            hexInput.blur()
            draggingHue = true
            updateHue(mouseX)
            return true
        }

        val alphaTop = hueBottom + 4f
        val alphaBottom = alphaTop + alphaBarHeight
        if (mouseX in hueLeft..hueRight && mouseY in alphaTop..alphaBottom) {
            hexInput.blur()
            draggingAlpha = true
            updateAlpha(mouseX)
            return true
        }

        val textLeft = squareRight + 8f
        val textTop = squareTop
        val textRight = lastX + width - 10f
        val textBottom = squareTop + 18f
        hexInput.x = textLeft
        hexInput.y = textTop
        hexInput.width = textRight - textLeft
        hexInput.height = textBottom - textTop
        if (!hexInput.focused) {
            syncHexField(moveCaretToEnd = true)
        }
        if (hexInput.mouseClicked(mouseX, mouseY, click, font = Minecraft.getInstance().font)) {
            return true
        }

        hexInput.blur()
        return true
    }

    override fun mouseDragged(mouseX: Float, mouseY: Float, click: MouseButtonEvent): Boolean {
        if (click.button() != 0) {
            return false
        }

        if (hexInput.mouseDragged(mouseX, mouseY, click, Minecraft.getInstance().font)) {
            return true
        }

        if (draggingSquare) {
            updateSquare(mouseX, mouseY)
            return true
        }

        if (draggingHue) {
            updateHue(mouseX)
            return true
        }

        if (draggingAlpha) {
            updateAlpha(mouseX)
            return true
        }

        return false
    }

    override fun mouseReleased(click: MouseButtonEvent) {
        if (click.button() == 0) {
            draggingSquare = false
            draggingHue = false
            draggingAlpha = false
        }
        hexInput.mouseReleased(click)
    }

    override fun charTyped(characterEvent: CharacterEvent): Boolean {
        return hexInput.charTyped(characterEvent)
    }

    override fun keyPressed(keyEvent: KeyEvent): Boolean {
        return hexInput.keyPressed(keyEvent)
    }

    override fun read(element: JsonElement, gson: Gson) {
        value = Color(element.asInt, true)
    }

    override fun write(gson: Gson): JsonElement = JsonPrimitive(value.rgb)

    private fun isInside(mouseX: Float, mouseY: Float): Boolean {
        return mouseX >= lastX && mouseX <= lastX + width &&
            mouseY >= lastY && mouseY <= lastY + height
    }

    private fun syncFromValue() {
        val hsb = Color.RGBtoHSB(value.red, value.green, value.blue, null)
        hue = hsb[0]
        saturation = hsb[1]
        brightness = hsb[2]
        alpha = value.alpha / 255f
        if (!hexInput.focused) {
            syncHexField()
        }
    }

    private fun updateColorFromHsb() {
        val rgb = Color.HSBtoRGB(hue, saturation, brightness)
        value = Color(
            (rgb shr 16) and 0xFF,
            (rgb shr 8) and 0xFF,
            rgb and 0xFF,
            (alpha * 255f).roundToInt().coerceIn(0, 255)
        )
    }

    private fun updateSquare(mouseX: Float, mouseY: Float) {
        val squareLeft = lastX + 8f
        val squareTop = lastY + headerHeight + pickerPadding
        saturation = ((mouseX - squareLeft) / pickerSize).coerceIn(0f, 1f)
        brightness = (1f - ((mouseY - squareTop) / pickerSize)).coerceIn(0f, 1f)
        updateColorFromHsb()
    }

    private fun updateHue(mouseX: Float) {
        val hueLeft = lastX + 8f
        val hueRight = lastX + width - 10f
        hue = ((mouseX - hueLeft) / (hueRight - hueLeft)).coerceIn(0f, 1f)
        if (saturation == 0f) {
            saturation = 1f
        }
        if (brightness == 0f) {
            brightness = 1f
        }
        updateColorFromHsb()
    }

    private fun updateAlpha(mouseX: Float) {
        val alphaLeft = lastX + 8f
        val alphaRight = lastX + width - 10f
        alpha = ((mouseX - alphaLeft) / (alphaRight - alphaLeft)).coerceIn(0f, 1f)
        updateColorFromHsb()
    }

    private fun renderColorSquare(guiGraphics: GuiGraphics, left: Int, top: Int, right: Int, bottom: Int) {
        val size = right - left
        for (x in 0 until size) {
            for (y in 0 until size) {
                val sat = x / size.toFloat()
                val bri = 1f - (y / size.toFloat())
                val color = Color.HSBtoRGB(hue, sat, bri) or 0xFF000000.toInt()
                guiGraphics.fill(left + x, top + y, left + x + 1, top + y + 1, color)
            }
        }
    }

    private fun renderHueBar(guiGraphics: GuiGraphics, left: Int, top: Int, right: Int, bottom: Int) {
        val width = right - left
        for (x in 0 until width) {
            val color = Color.HSBtoRGB(x / width.toFloat(), 1f, 1f) or 0xFF000000.toInt()
            guiGraphics.fill(left + x, top, left + x + 1, bottom, color)
        }
    }

    private fun renderAlphaBar(guiGraphics: GuiGraphics, left: Int, top: Int, right: Int, bottom: Int) {
        val width = right - left
        val base = Color.HSBtoRGB(hue, saturation, brightness)
        val red = (base shr 16) and 0xFF
        val green = (base shr 8) and 0xFF
        val blue = base and 0xFF

        for (x in 0 until width) {
            val currentAlpha = ((x / width.toFloat()) * 255f).roundToInt().coerceIn(0, 255)
            val color = (currentAlpha shl 24) or (red shl 16) or (green shl 8) or blue
            guiGraphics.fill(left + x, top, left + x + 1, bottom, color)
        }
    }

    private fun applyHexInput(input: String, force: Boolean = false) {
        val normalized = input.take(8)
        if (!force && normalized.length !in setOf(6, 8)) {
            return
        }

        val parsed = normalized.toLongOrNull(16) ?: return
        value = when (normalized.length) {
            6 -> Color(
                ((parsed shr 16) and 0xFF).toInt(),
                ((parsed shr 8) and 0xFF).toInt(),
                (parsed and 0xFF).toInt(),
                255
            )
            8 -> Color(
                ((parsed shr 16) and 0xFF).toInt(),
                ((parsed shr 8) and 0xFF).toInt(),
                (parsed and 0xFF).toInt(),
                ((parsed shr 24) and 0xFF).toInt()
            )
            else -> value
        }
    }

    private fun formatHex(color: Color): String {
        return "%02X%02X%02X%02X".format(color.alpha, color.red, color.green, color.blue)
    }

    private fun syncHexField(moveCaretToEnd: Boolean = false) {
        hexInput.setText(formatHex(value), moveCaretToEnd)
    }
}
