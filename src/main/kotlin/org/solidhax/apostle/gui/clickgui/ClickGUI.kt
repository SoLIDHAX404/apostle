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