package com.solidhax.apostle.modules.mining

import com.solidhax.apostle.Apostle.mc
import com.solidhax.apostle.modules.internal.Category
import com.solidhax.apostle.modules.internal.Module
import com.solidhax.apostle.ui.setting.HudSetting
import com.solidhax.apostle.utils.toMinecraftColor
import tech.thatgravyboat.skyblockapi.api.area.mining.Commission
import tech.thatgravyboat.skyblockapi.api.area.mining.CommissionArea
import tech.thatgravyboat.skyblockapi.api.area.mining.CommissionsAPI
import java.awt.Color

object Commissions : Module("Commissions", "Various features for Commissions", Category.MINING) {

    private val exampleCommissions: List<Commission> = listOf(
        Commission("Lava Springs Mithril", CommissionArea.DWARVEN_MINES, 0.45f),
        Commission("Royal Mines Titanium", CommissionArea.DWARVEN_MINES, 1f),
        Commission("Goblin Slayer", CommissionArea.DWARVEN_MINES, 0.13f)
    )

    private var activeCommissions: List<Commission> = emptyList()

    val activeCommissionsHUD by HudSetting("Active Commissions", "Display all active commissions and their pogress.", 10, 10) { preview ->
        activeCommissions = if(preview) exampleCommissions else CommissionsAPI.commissions.filter { CommissionArea.currentArea == it.area }

        var width = 0
        val height = activeCommissions.size * mc.font.lineHeight
        activeCommissions.forEachIndexed { index, commission ->
            val percent = commission.progress * 100f
            val commissionProgressText =
                if (commission.progress == 1f) "DONE"
                else if (percent % 1f == 0f) "${percent.toInt()}%"
                else "%.1f%%".format(java.util.Locale.US, percent)

            val line = "${commission.name}: $commissionProgressText"
            drawString(mc.font, line, 0, index * mc.font.lineHeight, progressToColor(commission.progress).toMinecraftColor())
            width = maxOf(width, mc.font.width(line))
        }

        width to height
    }

    private fun progressToColor(progress: Float): Color {
        return when {
            progress >= 0.75f -> Color.GREEN
            progress >= 0.50f -> Color.YELLOW
            progress >= 0.25f -> Color.ORANGE
            else -> Color.RED
        }
    }
}
