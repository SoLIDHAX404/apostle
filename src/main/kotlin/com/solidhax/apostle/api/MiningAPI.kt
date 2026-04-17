package com.solidhax.apostle.api

import com.solidhax.apostle.events.TabListWidgetEvent
import com.solidhax.apostle.events.TickEvent
import meteordevelopment.orbit.EventHandler
import kotlin.math.abs
import kotlin.math.max
import java.util.regex.Pattern

object MiningAPI {
    var commissions: List<Commission> = emptyList()
    var pickaxeAbility: PickaxeAbility? = null

    private val commissionRegex = Pattern.compile("^\\s*(?<name>[\\w\\s]+?):\\s*(?<progress>[\\d.]+%|\\w+)$")
    private val pickaxeAbilityRegex = Pattern.compile("(?<name>[^:]+): (?<cooldown>.+)")

    init {
        @EventHandler
        fun onWidgetAdded(event: TabListWidgetEvent.Add) {
            when (event.widget) {
                TabListAPI.TabWidget.COMMISSIONS -> commissions = parseCommissions(event.newContent)
                TabListAPI.TabWidget.PICKAXE_ABILITY -> syncPickaxeAbility(parsePickaxeAbility(event.newContent))
                else -> return
            }
        }

        @EventHandler
        fun onWidgetUpdated(event: TabListWidgetEvent.Update) {
            when (event.widget) {
                TabListAPI.TabWidget.COMMISSIONS -> commissions = parseCommissions(event.newContent)
                TabListAPI.TabWidget.PICKAXE_ABILITY -> syncPickaxeAbility(parsePickaxeAbility(event.newContent))
                else -> return
            }
        }

        @EventHandler
        fun onWidgetRemoved(event: TabListWidgetEvent.Remove) {
            when (event.widget) {
                TabListAPI.TabWidget.COMMISSIONS -> commissions = emptyList()
                TabListAPI.TabWidget.PICKAXE_ABILITY -> pickaxeAbility = null
                else -> return
            }
        }

        @EventHandler
        fun onTick(event: TickEvent.End) {
            val current = pickaxeAbility ?: return
            if (current.cooldown <= 0.0) return

            pickaxeAbility = current.copy(cooldown = max(0.0, current.cooldown - COOLDOWN_SECONDS_PER_TICK))
        }
    }

    private fun parseCommissions(content: TabListAPI.WidgetContent): List<Commission> =
        content.data.mapNotNull { line ->
            commissionRegex.matcher(line).takeIf { it.find() }?.let {
                Commission(it.group("name"), parseCommissionProgress(it.group("progress")))
            }
        }

    private fun parseCommissionProgress(progressStr: String): Double = when {
        progressStr == "DONE" -> 100.0
        progressStr.endsWith("%") -> progressStr.dropLast(1).toDouble()
        else -> 0.0
    }

    private fun parsePickaxeAbility(content: TabListAPI.WidgetContent): PickaxeAbility? {
        val match = content.data.firstNotNullOfOrNull { line -> pickaxeAbilityRegex.matcher(line).takeIf { it.find() } } ?: return null

        return PickaxeAbility(
            match.group("name"),
            parsePickaxeAbilityCooldown(match.group("cooldown"))
        )
    }

    private fun parsePickaxeAbilityCooldown(cooldownStr: String): Double = when {
        cooldownStr == "Available" -> 0.0
        cooldownStr.endsWith("s") -> cooldownStr.dropLast(1).toDouble()
        else -> 0.0
    }

    private fun syncPickaxeAbility(parsed: PickaxeAbility?) {
        if (parsed == null) {
            pickaxeAbility = null
            return
        }

        val current = pickaxeAbility
        pickaxeAbility = when {
            current == null -> parsed
            current.name != parsed.name -> parsed
            parsed.cooldown == 0.0 -> parsed
            current.cooldown == 0.0 -> parsed
            parsed.cooldown > current.cooldown + COOLDOWN_RESYNC_THRESHOLD -> parsed
            abs(parsed.cooldown - current.cooldown) <= COOLDOWN_RESYNC_THRESHOLD -> parsed
            else -> current
        }
    }

    data class Commission(val name: String, val progress: Double)
    data class PickaxeAbility(val name: String, val cooldown: Double)

    private const val COOLDOWN_SECONDS_PER_TICK = 0.05
    private const val COOLDOWN_RESYNC_THRESHOLD = 0.25
}
