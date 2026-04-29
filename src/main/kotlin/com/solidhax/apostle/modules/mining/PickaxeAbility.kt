package com.solidhax.apostle.modules.mining

import com.solidhax.apostle.Apostle.mc
import com.solidhax.apostle.api.MiningAPI
import com.solidhax.apostle.api.MiningAPI.PickaxeAbility
import com.solidhax.apostle.events.ChatPacketEvent
import com.solidhax.apostle.modules.internal.Category
import com.solidhax.apostle.modules.internal.Module
import com.solidhax.apostle.ui.setting.BooleanSetting
import com.solidhax.apostle.ui.setting.HudSetting
import com.solidhax.apostle.utils.alert
import com.solidhax.apostle.utils.floorTo
import com.solidhax.apostle.utils.toMinecraftColor
import meteordevelopment.orbit.EventHandler
import java.awt.Color
import java.util.regex.Pattern

object PickaxeAbility : Module("Pickaxe Ability", "Various features surrounding pickaxe abilities.", Category.MINING) {

    private val examplePickaxeAbility: PickaxeAbility = PickaxeAbility("Mining Speed Boost", 34.0)
    private var pickaxeAbility: PickaxeAbility? = null
    private val pickaxeAbilityReadyPattern = Pattern.compile("^(?<name>Mining Speed Boost|Pickobulus|Maniac Miner|Tunnel Vision|Sheer Force|Gemstone Infusion) is now available!$")

    val pickaxeAbilityHUD by HudSetting("Pickaxe Ability", "Display for the active pickaxe ability.", 10, 10) { preview ->
        pickaxeAbility = if(preview) examplePickaxeAbility else MiningAPI.pickaxeAbility ?: return@HudSetting 0 to 0

        val pickaxeAbilityCooldownText =
            if (pickaxeAbility?.cooldown == 0.0) "§aREADY!"
            else "§c${"%.2f".format(pickaxeAbility?.cooldown?.floorTo(2))}s"

        val line = "${pickaxeAbility?.name}: $pickaxeAbilityCooldownText"
        drawString(mc.font, line, 0, 0, Color.WHITE.toMinecraftColor())

        mc.font.width(line) to mc.font.lineHeight
    }

    val alertOnReady by BooleanSetting("Alert on Ready")

    init {
        @EventHandler
        fun onChatEvent(event: ChatPacketEvent) {
            if(!alertOnReady) return

            val matcher = pickaxeAbilityReadyPattern.matcher(event.value)
            if (matcher.matches()) {
                val name = matcher.group("name")
                alert("§a$name is Ready!")
            }
        }
    }
}