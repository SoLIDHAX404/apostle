package com.solidhax.apostle.ui.setting

import com.solidhax.apostle.ui.ClickGuiStyle
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.input.CharacterEvent
import net.minecraft.client.input.KeyEvent
import net.minecraft.client.input.MouseButtonEvent

abstract class RenderableSetting<T>(
    name: String,
    description: String
) : Setting<T>(name, description) {
    protected val width = ClickGuiStyle.PANEL_WIDTH - 12
    protected var lastX = 0f
    protected var lastY = 0f
    var listening = false
    open val height: Int = ClickGuiStyle.SETTING_ROW_HEIGHT

    open fun render(guiGraphics: GuiGraphics, x: Float, y: Float, mouseX: Float, mouseY: Float) {}
    open fun mouseClicked(mouseX: Float, mouseY: Float, click: MouseButtonEvent): Boolean = false
    open fun mouseDragged(mouseX: Float, mouseY: Float, click: MouseButtonEvent): Boolean = false
    open fun mouseReleased(click: MouseButtonEvent) {}
    open fun keyTyped(input: CharacterEvent): Boolean = false
    open fun keyPressed(input: KeyEvent): Boolean = false
}
