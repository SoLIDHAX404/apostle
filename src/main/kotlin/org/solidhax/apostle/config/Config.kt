package org.solidhax.apostle.config

import gg.essential.vigilance.Vigilant
import org.solidhax.apostle.modules.mining.Commissions
import org.solidhax.apostle.modules.mining.CorpseFinder
import org.solidhax.apostle.modules.render.HUD
import org.solidhax.apostle.modules.skyblock.DamageSplash
import org.solidhax.apostle.modules.skyblock.MobNametagHider
import org.solidhax.apostle.modules.skyblock.RarityDisplay
import java.io.File

object Config : Vigilant(File("config/apostle/apostle.toml"), "Apostle") {

    init {

        category("General") {
            subcategory("Item Rarity") {
                switch(RarityDisplay::enabled, "Display Item Rarity", "Displays a colored background behind an item based on its rarity.")
                selector(RarityDisplay::type, "Item Rarity Background Type", "The type of background to display behind an item.", listOf("Circle", "Square", "Square Outline", "Outline"))
                percentSlider(RarityDisplay::opacity, "Item Rarity Background Opacity", "The opacity of the background to display.")
            }

            subcategory("Other") {
                switch(DamageSplash::enabled, "Truncate Damage Numbers", "Truncates damage numbers in damage tags. ex: 120,000 -> 120k")
                switch(MobNametagHider::enabled, "Hide Full HP Mob Nametags", "Hide nametags of full hp mobs.")
            }
        }

        category("Mining") {
            subcategory("Commissions") {
                switch(Commissions::highlightCompletedCommissions, "Highlight Completed Commissions", "Highlight commissions in the claim menu.")
            }

            subcategory("Mineshafts") {
                switch(CorpseFinder::enabled, "Corpse Finder", "USE AT YOUR OWN RISK! Adds a ESP style box at every corpses position in a mineshaft.")
            }
        }

        category("Miscellaneous") {
            subcategory("Removals") {
                switch(HUD::removeHearts, "Remove Hearts", "Removes the vanilla hearts.")
                switch(HUD::removeArmor, "Remove Armor", "Removes the vanilla armor.")
                switch(HUD::removeHunger, "Remove Hunger", "Removes the vanilla hunger.")
                switch(HUD::removePotionEffects, "Remove Potion Effect", "Removes the vanilla potion effects hud.")
                switch(HUD::removeInventoryPotionEffects, "Remove Inventory Potion Effect", "Removes the vanilla potion effects hud in the inventory.")
                switch(HUD::removeHeldItemTooltip, "Remove Held Item Tooltip", "Removes the held item tooltip when switching to a new item.")
            }
        }

        initialize()
    }
}