package org.solidhax.apostle.commands

import com.github.stivais.commodore.Commodore
import gg.essential.universal.UScreen
import org.solidhax.apostle.Apostle.Companion.mc
import org.solidhax.apostle.config.Config
import org.solidhax.apostle.config.KeybindConfig
import org.solidhax.apostle.modules.impl.WidgetEditor
import org.solidhax.apostle.modules.macros.KeybindManager
import org.solidhax.apostle.utils.chat.modMessage
import org.solidhax.apostle.utils.scheduler.schedule

val mainCommand = Commodore("apostle", "ap") {
    runs {
        schedule(0) { UScreen.displayScreen(Config.gui()) }
    }

    literal("dev").runs {
        schedule(0) { modMessage(KeybindManager.keybinds.size) }
    }

    literal("keybinds").runs {
        schedule(0) { UScreen.displayScreen(KeybindConfig) }
    }

    literal("hud").runs {
        schedule(0) { mc.setScreen(WidgetEditor) }
    }
}