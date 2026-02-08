package org.solidhax.apostle.gui.clickgui

import gg.essential.vigilance.Vigilant
import gg.essential.vigilance.data.Property
import gg.essential.vigilance.data.PropertyType
import java.io.File

object ClickGUI : Vigilant(File("config/apostle.toml"), "Apostle") {
    @Property(
        type = PropertyType.SWITCH,
        name = "Enable feature",
        description = "Turns the feature on/off",
        category = "General"
    )
    var enableFeature: Boolean = true

    init {
        initialize()
    }
}

//object ClickGUI : Screen(Component.literal("Click GUI")) {
//
//    val mainWindow = UIWindow(0f, 0f, 600f, 400f, "Click GUI Window")
//
//    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, deltaTicks: Float) {
//
//        NVGSpecialRenderer.draw(guiGraphics, 0, 0, guiGraphics.guiWidth(), guiGraphics.guiHeight()) {
//            mainWindow.render(guiGraphics, mouseX, mouseY, deltaTicks)
//        }
//
//        super.render(guiGraphics, mouseX, mouseY, deltaTicks)
//    }
//
//    override fun mouseClicked(mouseButtonEvent: MouseButtonEvent, bl: Boolean): Boolean {
//        mainWindow.mouseClicked(mouseButtonEvent, bl)
//        return super.mouseClicked(mouseButtonEvent, bl)
//    }
//
//    override fun mouseReleased(mouseButtonEvent: MouseButtonEvent): Boolean {
//        mainWindow.mouseReleased(mouseButtonEvent)
//        return super.mouseReleased(mouseButtonEvent)
//    }
//
//    override fun renderBackground(guiGraphics: GuiGraphics?, i: Int, j: Int, f: Float) { return }
//
//    override fun isPauseScreen(): Boolean { return false}
//}