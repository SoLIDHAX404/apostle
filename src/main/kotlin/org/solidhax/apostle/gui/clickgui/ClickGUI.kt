package org.solidhax.apostle.gui.clickgui

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.network.chat.Component
import org.solidhax.apostle.gui.clickgui.components.UIWindow
import org.solidhax.apostle.gui.clickgui.pages.ClickGuiPage
import org.solidhax.apostle.gui.clickgui.pages.ModuleDetailPage
import org.solidhax.apostle.gui.clickgui.pages.ModuleGridPage
import org.solidhax.apostle.gui.clickgui.pages.UIBounds
import org.solidhax.apostle.utils.render.NVGSpecialRenderer

object ClickGUI : Screen(Component.literal("Click GUI")) {

    val mainWindow = UIWindow(0f, 0f, 600f, 400f, "Click GUI Window")
    private val contentPadding = 12f
    private var currentPage: ClickGuiPage = ModuleGridPage { module ->
        currentPage = ModuleDetailPage(module)
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, deltaTicks: Float) {

        NVGSpecialRenderer.draw(guiGraphics, 0, 0, guiGraphics.guiWidth(), guiGraphics.guiHeight()) {
            mainWindow.render(guiGraphics, mouseX, mouseY, deltaTicks)
            currentPage.render(guiGraphics, contentBounds(), mouseX, mouseY, deltaTicks)
        }

        super.render(guiGraphics, mouseX, mouseY, deltaTicks)
    }

    override fun mouseClicked(mouseButtonEvent: MouseButtonEvent, bl: Boolean): Boolean {
        mainWindow.mouseClicked(mouseButtonEvent, bl)
        currentPage.mouseClicked(mouseButtonEvent, contentBounds())
        return super.mouseClicked(mouseButtonEvent, bl)
    }

    override fun mouseReleased(mouseButtonEvent: MouseButtonEvent): Boolean {
        mainWindow.mouseReleased(mouseButtonEvent)
        currentPage.mouseReleased(mouseButtonEvent, contentBounds())
        return super.mouseReleased(mouseButtonEvent)
    }

    override fun renderTransparentBackground(guiGraphics: GuiGraphics?) { return }
    override fun renderBlurredBackground(guiGraphics: GuiGraphics?) { return }
    override fun renderBackground(guiGraphics: GuiGraphics?, i: Int, j: Int, f: Float) { return }

    override fun isPauseScreen(): Boolean { return false}

    private fun contentBounds(): UIBounds {
        return UIBounds(
            mainWindow.x + contentPadding,
            mainWindow.y + contentPadding,
            mainWindow.width - (contentPadding * 2),
            mainWindow.height - (contentPadding * 2),
        )
    }
}
