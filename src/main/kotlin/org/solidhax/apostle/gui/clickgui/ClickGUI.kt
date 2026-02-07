package org.solidhax.apostle.gui.clickgui

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.network.chat.Component
import org.solidhax.apostle.gui.clickgui.components.UIWindow
import org.solidhax.apostle.modules.impl.ModuleManager
import org.solidhax.apostle.utils.render.NVGRenderer
import org.solidhax.apostle.utils.render.NVGSpecialRenderer
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max

object ClickGUI : Screen(Component.literal("Click GUI")) {

    val mainWindow = UIWindow(0f, 0f, 875f, 540f, "Apostle")

    init {
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, deltaTicks: Float) {

        NVGSpecialRenderer.draw(guiGraphics, 0, 0, guiGraphics.guiWidth(), guiGraphics.guiHeight()) {
            mainWindow.render(guiGraphics, mouseX, mouseY, deltaTicks)
        }

        super.render(guiGraphics, mouseX, mouseY, deltaTicks)
    }

    override fun mouseClicked(mouseButtonEvent: MouseButtonEvent, bl: Boolean): Boolean {
        mainWindow.mouseClicked(mouseButtonEvent, bl)
        return super.mouseClicked(mouseButtonEvent, bl)
    }

    override fun mouseReleased(mouseButtonEvent: MouseButtonEvent): Boolean {
        mainWindow.mouseReleased(mouseButtonEvent)
        return super.mouseReleased(mouseButtonEvent)
    }

    override fun renderBackground(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {}
    override fun isPauseScreen(): Boolean { return false}
}