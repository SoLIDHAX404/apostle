package com.solidhax.apostle.modules.mining

import com.solidhax.apostle.Apostle.mc
import com.solidhax.apostle.modules.internal.Category
import com.solidhax.apostle.modules.internal.Module
import com.solidhax.apostle.ui.ClickGuiStyle
import com.solidhax.apostle.ui.setting.HudSetting
import tech.thatgravyboat.skyblockapi.api.area.mining.Commission
import tech.thatgravyboat.skyblockapi.api.area.mining.CommissionArea
import tech.thatgravyboat.skyblockapi.api.area.mining.CommissionsAPI

object Commissions : Module("Commissions", "Various features for Commissions", Category.MINING) {

    private val exampleCommissions: List<Commission> = listOf(
        Commission("Lava Springs Mithril", CommissionArea.DWARVEN_MINES, 45f),
        Commission("Royal Mines Titanium", CommissionArea.DWARVEN_MINES, 100f),
        Commission("Goblin Slayer", CommissionArea.DWARVEN_MINES, 13f)
    )

    private var activeCommissions: List<Commission> = emptyList()

    val activeCommissionsHUD by HudSetting("Active Commissions", "Display all active commissions and their pogress.", 10, 10) { preview ->
        activeCommissions = if(preview) exampleCommissions else CommissionsAPI.commissions

        var width = 0
        val height = activeCommissions.size * mc.font.lineHeight
        activeCommissions.forEachIndexed { index, commission ->
            val commissionProgressText = if (commission.progress == 1f) "DONE" else "${commission.progress * 100.0}%"
            val line = "${commission.name}: $commissionProgressText"

            drawString(mc.font, line, 0, index * mc.font.lineHeight, ClickGuiStyle.TITLE)
            width = maxOf(width, mc.font.width(line))
        }

        width to height
    }
}
