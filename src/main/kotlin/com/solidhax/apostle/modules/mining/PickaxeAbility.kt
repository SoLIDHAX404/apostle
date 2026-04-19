package com.solidhax.apostle.modules.mining

import com.solidhax.apostle.Apostle.mc
import com.solidhax.apostle.api.MiningAPI
import com.solidhax.apostle.api.MiningAPI.PickaxeAbility
import com.solidhax.apostle.events.TickEvent
import com.solidhax.apostle.modules.internal.Category
import com.solidhax.apostle.modules.internal.Module
import com.solidhax.apostle.ui.setting.HudSetting
import com.solidhax.apostle.utils.floorTo
import com.solidhax.apostle.utils.toMinecraftColor
import meteordevelopment.orbit.EventHandler
import java.awt.Color

object PickaxeAbility : Module("Pickaxe Ability", "Various features surrounding pickaxe abilities.", Category.MINING) {

    private val examplePickaxeAbility: PickaxeAbility = PickaxeAbility("Mining Speed Boost", 34.0)
    private var pickaxeAbility: PickaxeAbility? = null

    val pickaxeAbilityHUD by HudSetting("Pickaxe Ability", "Display for the active pickaxe ability.", 10, 10) { preview ->
        pickaxeAbility = if(preview) examplePickaxeAbility else MiningAPI.pickaxeAbility ?: return@HudSetting 0 to 0

        val pickaxeAbilityCooldownText =
            if (pickaxeAbility?.cooldown == 0.0) "§aREADY!"
            else "§c${"%.2f".format(pickaxeAbility?.cooldown?.floorTo(2))}s"

        val line = "${pickaxeAbility?.name}: $pickaxeAbilityCooldownText"
        drawString(mc.font, line, 0, 0, Color.WHITE.toMinecraftColor())

        mc.font.width(line) to mc.font.lineHeight
    }
}