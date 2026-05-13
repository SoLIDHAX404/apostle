package com.solidhax.apostle.commands

import com.github.stivais.commodore.Commodore
import com.solidhax.apostle.Apostle.mc
import com.solidhax.apostle.modules.dev.PathfindingDebug
import com.solidhax.apostle.ui.ConfigScreen
import com.solidhax.apostle.ui.HudManager
import com.solidhax.apostle.utils.schedule
import net.minecraft.core.BlockPos
import kotlin.math.floor

val mainCommand = Commodore("apostle", "ap") {
    runs {
        schedule(0) { mc.setScreen(ConfigScreen) }
    }

    literal("hud").runs {
        schedule(0) { mc.setScreen(HudManager) }
    }
}