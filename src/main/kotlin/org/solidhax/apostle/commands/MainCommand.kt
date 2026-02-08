package org.solidhax.apostle.commands

import com.github.stivais.commodore.Commodore
import gg.essential.universal.UScreen
import org.solidhax.apostle.config.Config
import org.solidhax.apostle.utils.scheduler.schedule

val mainCommand = Commodore("apostle", "ap") {
    runs {
        schedule(0) {
            UScreen.displayScreen(Config.gui())
        }
    }
}