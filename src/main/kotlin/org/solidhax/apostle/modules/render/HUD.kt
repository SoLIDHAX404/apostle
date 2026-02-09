package org.solidhax.apostle.modules.render

import org.solidhax.apostle.config.Config
import org.solidhax.apostle.modules.impl.HUD
import org.solidhax.apostle.modules.impl.Module
import org.solidhax.apostle.utils.location.LocationUtils
import org.solidhax.apostle.utils.render.Colors

enum class HudType {
    HEARTS,
    ARMOR,
    HUNGER,
    POTION_EFFECTS,
    INVENTORY_POTION_EFFECTS,
    HELD_ITEM_TOOLTIP
}

object HUD : Module(name = "HUD", description = "HUD")  {

    val test by HUD("Test", "Test") { g ->
        val label = LocationUtils.currentArea.displayName
        val labelWidth = textWidth(label)
        val labelHeight = font().lineHeight

        g.drawString(font(), label, x, y, 0xFFFFFFFF.toInt(), true)

        labelWidth to 9
    }

    @JvmStatic
    fun shouldCancelHud(type: HudType): Boolean {
        if(!LocationUtils.isInSkyblock) return false
        return when (type) {
            HudType.HEARTS -> Config.removeHearts
            HudType.ARMOR -> Config.removeArmor
            HudType.HUNGER -> Config.removeHunger
            HudType.POTION_EFFECTS -> Config.removePotionEffects
            HudType.INVENTORY_POTION_EFFECTS -> Config.removeInventoryPotionEffects
            HudType.HELD_ITEM_TOOLTIP -> Config.removeHeldItemTooltip
        }
    }
}