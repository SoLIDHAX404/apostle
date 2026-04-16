package com.solidhax.apostle.api

import com.solidhax.apostle.events.TabListWidgetEvent
import meteordevelopment.orbit.EventHandler
import java.util.regex.Pattern

object MiningAPI {
    var commissions: List<Commission> = emptyList()

    private val commissionRegex = Pattern.compile("^\\s*(?<name>[\\w\\s]+?):\\s*(?<progress>[\\d.]+%|\\w+)$")

    init {
        @EventHandler
        fun onWidgetAdded(event: TabListWidgetEvent.Add) {
            if (event.widget != TabListAPI.TabWidget.COMMISSIONS) return
            commissions = parseCommissions(event.newContent)
        }

        @EventHandler
        fun onWidgetUpdated(event: TabListWidgetEvent.Update) {
            if (event.widget != TabListAPI.TabWidget.COMMISSIONS) return
            commissions = parseCommissions(event.newContent)
        }

        @EventHandler
        fun onWidgetRemoved(event: TabListWidgetEvent.Remove) {
            if (event.widget != TabListAPI.TabWidget.COMMISSIONS) return
            commissions = emptyList()
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

    data class Commission(val name: String, val progress: Double)
}
