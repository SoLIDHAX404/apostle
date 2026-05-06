package com.solidhax.apostle.modules.farming

import com.solidhax.apostle.Apostle.mc
import com.solidhax.apostle.api.Island
import com.solidhax.apostle.api.LocationAPI
import com.solidhax.apostle.mixin.accessor.KeyMappingAccessor
import com.solidhax.apostle.modules.internal.Category
import com.solidhax.apostle.modules.internal.Module
import net.minecraft.client.KeyMapping

object FarmKeys : Module("Farm Keys", "Switches keybinds while farming.", Category.FARMING) {

    private val farmingTools = setOf(
        "Euclid’s Wheat Hoe",
        "Gauss Carrot Hoe",
        "Pythagorean Potato Hoe",
        "Pumpkin Dicer",
        "Turing Sugar Cane Hoe",
        "Melon Dicer",
        "Cactus Knife",
        "Cocoa Chopper",
        "Fungi Cutter",
        "Newton Nether Wart Hoe",
        "Eclipse Hoe",
        "Wild Rose Hoe"
    )

    @JvmStatic
    fun getSwappedIsDown(self: KeyMapping): Boolean? {
        if (!shouldSwitchAttackAndJump()) return null
        return when (self) {
            mc.options.keyAttack -> (mc.options.keyJump as KeyMappingAccessor).isDown
            mc.options.keyJump -> (mc.options.keyAttack as KeyMappingAccessor).isDown
            else -> null
        }
    }

    @JvmStatic
    fun getSwappedConsumeClick(self: KeyMapping): Boolean? {
        if (!shouldSwitchAttackAndJump()) return null
        return when (self) {
            mc.options.keyAttack -> consumeClickDirect(mc.options.keyJump as KeyMappingAccessor)
            mc.options.keyJump -> consumeClickDirect(mc.options.keyAttack as KeyMappingAccessor)
            else -> null
        }
    }

    private fun consumeClickDirect(key: KeyMappingAccessor): Boolean {
        if (key.clickCount <= 0) return false
        key.clickCount -= 1
        return true
    }

    fun shouldSwitchAttackAndJump(): Boolean {
        if (!enabled || !LocationAPI.isCurrentArea(Island.Garden)) return false
        val player = mc.player ?: return false

        val item = player.mainHandItem
        if (item.isEmpty) return false

        val name = item.displayName.string
        if (farmingTools.none { name.contains(it, ignoreCase = true) }) return false

        return true
    }
}