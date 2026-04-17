package com.solidhax.apostle.modules.mining

import com.solidhax.apostle.Apostle.mc
import com.solidhax.apostle.api.MiningAPI
import com.solidhax.apostle.api.MiningAPI.PickaxeAbility
import com.solidhax.apostle.api.MiningAPI.Commission
import com.solidhax.apostle.modules.internal.Category
import com.solidhax.apostle.modules.internal.Module
import com.solidhax.apostle.ui.setting.HudSetting
import com.solidhax.apostle.utils.toMinecraftColor
import java.awt.Color

object Commissions : Module("Commissions", "Various features for Commissions", Category.MINING) {

    private val exampleCommissions: List<Commission> = listOf(
        Commission("Lava Springs Mithril", 45.0),
        Commission("Royal Mines Titanium", 100.0),
        Commission("Goblin Slayer", 13.0)
    )

    private var activeCommissions: List<Commission> = emptyList()

    val activeCommissionsHUD by HudSetting("Active Commissions", "Display all active commissions and their progress.", 10, 10) { preview ->
        activeCommissions = if(preview) exampleCommissions else MiningAPI.commissions

        var width = 0
        val height = activeCommissions.size * mc.font.lineHeight
        activeCommissions.forEachIndexed { index, commission ->
            val percent = commission.progress * 100f
            val commissionProgressText = if (commission.progress == 100.0) "DONE" else "${commission.progress}%"

            val line = "${commission.name}: $commissionProgressText"
            drawString(mc.font, line, 0, index * mc.font.lineHeight, progressToColor(commission.progress).toMinecraftColor())
            width = maxOf(width, mc.font.width(line))
        }

        width to height
    }

    private fun progressToColor(progress: Double): Color {
        return when {
            progress >= 75.0 -> Color.GREEN
            progress >= 50.0 -> Color.YELLOW
            progress >= 25.0 -> Color.ORANGE
            else -> Color.RED
        }
    }
}
