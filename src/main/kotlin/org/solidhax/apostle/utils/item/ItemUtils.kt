package org.solidhax.apostle.utils.item

import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.ItemLore

inline val ItemStack.lore: List<Component> get() = getOrDefault(DataComponents.LORE, ItemLore.EMPTY).styledLines()
inline val ItemStack.loreString: List<String> get() = lore.map { it.string }

object ItemUtils {

    private val rarityRegex = Regex("(${ItemRarity.entries.joinToString("|") { it.identifier }}) ?([A-Z ]+)?")

    fun getItemRarity(lore: List<String>): ItemRarity? {
        for(i in lore.indices.reversed()) {
            val rarity = rarityRegex.matchEntire(lore[i])?.groups?.get(1)?.value ?: continue
            return ItemRarity.entries.find { it.identifier == rarity }
        }

        return null
    }

    fun getItemRarity(item: ItemStack): ItemRarity? {
        return getItemRarity(item.loreString)
    }
}