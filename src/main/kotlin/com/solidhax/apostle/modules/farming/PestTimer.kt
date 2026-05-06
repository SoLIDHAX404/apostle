package com.solidhax.apostle.modules.farming

import com.solidhax.apostle.Apostle.mc
import com.solidhax.apostle.events.ChatPacketEvent
import com.solidhax.apostle.events.TickEvent
import com.solidhax.apostle.modules.internal.Category
import com.solidhax.apostle.modules.internal.Module
import com.solidhax.apostle.ui.setting.HudSetting
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
    private var timeTillNextSpawn = -1f
    private var pestSpawnedThisSession = false

    private fun formatTicks(ticks: Float): String {
        val totalSeconds = (ticks / 20).toInt()
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return if (minutes > 0) "${minutes}m ${seconds}s" else "${seconds}s"
    }

    private val spawnCooldownHud by HudSetting("Pest Spawn Cooldown", "HUD element showing the pest spawn cooldown.", 10, 10) { preview ->
        val status = when {
            preview -> "§b1m 3s"
            !pestSpawnedThisSession -> "§7No pests spawned since joining"
            timeTillNextSpawn <= 0 -> "§aReady!"
            else -> "§e${formatTicks(timeTillNextSpawn)}"
        }
        val text = "§6Pest Cooldown: $status"
        drawString(mc.font, text, 0, 0, Color.WHITE.toMinecraftColor())

        mc.font.width(text) to mc.font.lineHeight
    }

    init {
        @EventHandler
        fun onTickEnd(event: TickEvent.End) {
            if (timeTillNextSpawn > 0) timeTillNextSpawn--
        }

        @EventHandler
        fun onChatPacketEvent(event: ChatPacketEvent) {
            if (onePestPattern.matcher(event.value).matches() ||
                multiplePestsPattern.matcher(event.value).matches() ||
                offlinePestsPattern.matcher(event.value).matches()
            ) {
                pestSpawnedThisSession = true
                timeTillNextSpawn = spawnCooldown * 20f
            }
        }
    }
}