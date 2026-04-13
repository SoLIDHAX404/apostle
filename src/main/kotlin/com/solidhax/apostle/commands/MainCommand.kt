package com.solidhax.apostle.commands

import com.github.stivais.commodore.Commodore
import com.solidhax.apostle.Apostle.mc
import com.solidhax.apostle.ui.ConfigScreen
import com.solidhax.apostle.utils.schedule

val mainCommand = Commodore("apostle", "ap") {
    runs {
        schedule(0) { mc.setScreen(ConfigScreen) }
    }
}