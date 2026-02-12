package org.solidhax.apostle.modules.render

import org.solidhax.apostle.modules.impl.Module
import org.solidhax.apostle.utils.location.LocationUtils

enum class HudType {
    HEARTS,
    ARMOR,
    HUNGER,
    POTION_EFFECTS,
    INVENTORY_POTION_EFFECTS,
    HELD_ITEM_TOOLTIP
}

object HUD : Module()  {

    var removeHearts = false
    var removeArmor = false
    var removeHunger = false
    var removePotionEffects = false
    var removeInventoryPotionEffects = false
    var removeHeldItemTooltip = false

    @JvmStatic
    fun shouldCancelHud(type: HudType): Boolean {
        if(!LocationUtils.isInSkyblock) return false
        return when (type) {
            HudType.HEARTS -> removeHearts
            HudType.ARMOR -> removeArmor
            HudType.HUNGER -> removeHunger
            HudType.POTION_EFFECTS -> removePotionEffects
            HudType.INVENTORY_POTION_EFFECTS -> removeInventoryPotionEffects
            HudType.HELD_ITEM_TOOLTIP -> removeHeldItemTooltip
        }
    }
}