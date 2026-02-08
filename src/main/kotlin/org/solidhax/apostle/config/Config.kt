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

    init {
        initialize()

        addDependency("rarityBackgroundType", "showRarityBackground") { enabled: Boolean -> enabled }
        addDependency("rarityBackgroundOpacity", "showRarityBackground") { enabled: Boolean -> enabled }
    }
}