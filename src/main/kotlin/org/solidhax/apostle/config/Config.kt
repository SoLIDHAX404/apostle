package org.solidhax.apostle.config

import gg.essential.vigilance.Vigilant
import gg.essential.vigilance.data.Property
import gg.essential.vigilance.data.PropertyType
import java.io.File

object Config : Vigilant(File("config/apostle.toml"), "Apostle") {

    @Property(
        type = PropertyType.SWITCH,
        name = "Show Rarity Background",
        description = "Show a colored background behind items based on their rarity.",
        category = "General"
    )
    var showRarityBackground: Boolean = false

    @Property(
        type = PropertyType.SELECTOR,
        name = "Rarity Background Type",
        description = "The typ of rarity background displayed behind an item.",
        category = "General",
        options = ["Circle", "Square", "Square Outline", "Outline"]
    )
    var rarityBackgroundType: Int = 0

    @Property(
        type = PropertyType.PERCENT_SLIDER,
        name = "Rarity Background Opacity",
        description = "The opacity of the rarity background displayed.",
        category = "General",
    )
    var rarityBackgroundOpacity: Float = 1f

    @Property(
        type = PropertyType.SWITCH,
        name = "Truncate Damage Numbers",
        description = "Truncates damage numbers in damage tags. ex: 120,000 -> 120k",
        category = "General"
    )
    var truncateDamageNumbers: Boolean = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Remove Hearts",
        description = "Removes the vanilla hearts.",
        category = "HUD"
    )
    var removeHearts: Boolean = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Remove Armor",
        description = "Removes the vanilla armor.",
        category = "HUD"
    )
    var removeArmor: Boolean = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Remove Hunger",
        description = "Removes the vanilla hunger.",
        category = "HUD"
    )
    var removeHunger: Boolean = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Remove Potion Effects",
        description = "Removes the vanilla potion effects widget.",
        category = "HUD"
    )
    var removePotionEffects: Boolean = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Remove Inventory Potion Effects",
        description = "Removes the vanilla potion effects widget in the inventory.",
        category = "HUD"
    )
    var removeInventoryPotionEffects: Boolean = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Remove Held Item Tooltip",
        description = "Removes the vanilla held item tooltip.",
        category = "HUD"
    )
    var removeHeldItemTooltip: Boolean = false

    init {
        initialize()

        addDependency("rarityBackgroundType", "showRarityBackground") { enabled: Boolean -> enabled }
        addDependency("rarityBackgroundOpacity", "showRarityBackground") { enabled: Boolean -> enabled }
    }
}