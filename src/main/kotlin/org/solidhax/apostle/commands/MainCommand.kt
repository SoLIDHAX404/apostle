package org.solidhax.apostle.commands

import com.github.stivais.commodore.Commodore
import gg.essential.universal.UScreen
import org.solidhax.apostle.Apostle.Companion.mc
import org.solidhax.apostle.config.Config
import org.solidhax.apostle.modules.impl.WidgetEditor
import org.solidhax.apostle.utils.scheduler.schedule

val mainCommand = Commodore("apostle", "ap") {
    runs {
        schedule(0) { UScreen.displayScreen(Config.gui()) }
    }

    literal("hud").runs {
        schedule(0) { mc.setScreen(WidgetEditor) }
    }
}