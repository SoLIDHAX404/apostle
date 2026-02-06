package org.solidhax.apostle.commands

import com.github.stivais.commodore.Commodore
import org.solidhax.apostle.Apostle
import org.solidhax.apostle.gui.clickgui.ClickGUI
import org.solidhax.apostle.utils.scheduler.schedule

val mainCommand = Commodore("apostle", "ap") {
    runs {
        schedule(0) { Apostle.mc.setScreen(ClickGUI) }
    }
}