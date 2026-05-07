package com.solidhax.apostle.modules.farming

import com.solidhax.apostle.Apostle.mc
import com.solidhax.apostle.api.Island
import com.solidhax.apostle.api.LocationAPI
import com.solidhax.apostle.events.ChatPacketEvent
import com.solidhax.apostle.events.TickEvent
import com.solidhax.apostle.modules.internal.Category
import com.solidhax.apostle.modules.internal.Module
import com.solidhax.apostle.ui.setting.HudSetting
import com.solidhax.apostle.ui.setting.Setting.Companion.withDependency
import com.solidhax.apostle.ui.setting.SliderSetting
import com.solidhax.apostle.utils.toMinecraftColor
import meteordevelopment.orbit.EventHandler
import java.awt.Color
import java.util.regex.Pattern

object PestTimer : Module(name = "Pest Timer", "A timer that shows when you can next spawn pests.", Category.FARMING) {

    private val onePestPattern = Pattern.compile(".*! A ൠ Pest has appeared in Plot - (?<plot>.*)!")
    private val multiplePestsPattern = Pattern.compile(".*! (?<amount>\\d) ൠ Pests? have spawned in Plot - (?<plot>.*)!")
    private val offlinePestsPattern = Pattern.compile(".*! While you were offline, ൠ Pests? spawned in Plots (?<plots>.*)!")

    private val spawnCooldown by SliderSetting("Cooldown", "Pest spawn cooldown", 130f, 0f, 300f)
    private var timeTillNextSpawn = -1L
    private var pestSpawnedThisSession = false

    private val spawnCooldownHud by HudSetting("Pest Spawn Cooldown", "HUD element showing the pest spawn cooldown.", 10, 10) { preview ->
        val remaining = timeTillNextSpawn - System.currentTimeMillis()
        val status = when {
            preview -> "§b1m 3s"
            !pestSpawnedThisSession -> "§7No pests spawned since joining"
            remaining <= 0 -> "§aReady!"
            else -> "§e${formatTime(remaining)}"
        }

        val text = "§6Pest Cooldown: $status"
        drawString(mc.font, text, 0, 0, Color.WHITE.toMinecraftColor())

        mc.font.width(text) to mc.font.lineHeight
    }.withDependency { enabled && LocationAPI.isCurrentArea(Island.Garden) }

    init {
        @EventHandler
        fun onChatPacketEvent(event: ChatPacketEvent) {
            if(!LocationAPI.isCurrentArea(Island.Garden)) return

            if (onePestPattern.matcher(event.value).matches() ||
                multiplePestsPattern.matcher(event.value).matches() ||
                offlinePestsPattern.matcher(event.value).matches()
            ) {
                pestSpawnedThisSession = true
                timeTillNextSpawn = System.currentTimeMillis() + (spawnCooldown * 1000).toLong()
            }
        }
    }

    private fun formatTime(ms: Long): String {
        val totalSeconds = (ms / 1000).toInt()
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return if (minutes > 0) "${minutes}m ${seconds}s" else "${seconds}s"
    }
}