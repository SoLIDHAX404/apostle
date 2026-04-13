package com.solidhax.apostle.ui.setting

import com.solidhax.apostle.ui.ClickGuiStyle
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.input.MouseButtonEvent

class BooleanSetting(
    name: String,
    description: String = "",
    override val default: Boolean = false
) : RenderableSetting<Boolean>(name, description) {
    override var value: Boolean = default

    override fun render(guiGraphics: GuiGraphics, x: Float, y: Float, mouseX: Float, mouseY: Float) {
        lastX = x
        lastY = y

        val left = x.toInt()
        val top = y.toInt()
        val right = left + width
        val bottom = top + ClickGuiStyle.SETTING_ROW_HEIGHT
        val hovered = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + ClickGuiStyle.SETTING_ROW_HEIGHT
        val font = Minecraft.getInstance().font
        val trackLeft = right - 34
        val trackRight = right - 10
        val trackTop = top + 4
        val trackBottom = bottom - 4
        val knobLeft = if (value) trackRight - 10 else trackLeft + 1

        guiGraphics.fill(left, top, right, bottom, if (hovered) 0x70334759 else 0x50273444)
        guiGraphics.drawString(font, name, left + 8, top + 5, ClickGuiStyle.SOFT_TEXT, false)
        guiGraphics.fill(trackLeft, trackTop, trackRight, trackBottom, if (value) ClickGuiStyle.withAlpha(ClickGuiStyle.SUCCESS, 180) else 0x90404C57.toInt())
        guiGraphics.fill(knobLeft, trackTop + 1, knobLeft + 9, trackBottom - 1, ClickGuiStyle.TITLE)
    }

    override fun mouseClicked(mouseX: Float, mouseY: Float, click: MouseButtonEvent): Boolean {
        if (click.button() != 0) {
            return false
        }

        val inside = mouseX >= lastX && mouseX <= lastX + width && mouseY >= lastY && mouseY <= lastY + ClickGuiStyle.SETTING_ROW_HEIGHT
        if (!inside) {
            return false
        }

        value = !value
        return true
    }
}
