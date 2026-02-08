package org.solidhax.apostle.modules.skyblock

import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.resources.ResourceLocation
import org.solidhax.apostle.config.Config
import org.solidhax.apostle.event.AbstractContainerScreenEvent
import org.solidhax.apostle.event.GuiEvent
import org.solidhax.apostle.event.impl.subscribe
import org.solidhax.apostle.modules.impl.Module
import org.solidhax.apostle.utils.item.ItemUtils

object RarityDisplay : Module(id = "rarity_display", displayName = "Rarity Display", description = "Rarity display") {
    val RARITY_TEXTURES: Array<ResourceLocation?> = arrayOf<ResourceLocation?>(
        ResourceLocation.fromNamespaceAndPath("apostle", "rarity"),
        ResourceLocation.fromNamespaceAndPath("apostle", "rarity2"),
        ResourceLocation.fromNamespaceAndPath("apostle", "rarity3"),
        ResourceLocation.fromNamespaceAndPath("apostle", "rarity4")
    )

    init {
        subscribe<AbstractContainerScreenEvent.RenderSlot> { event ->
            if(!Config.showRarityBackground) return@subscribe

            val itemInSlot = event.slot.item ?: return@subscribe
            if(itemInSlot.isEmpty) return@subscribe

            val itemRarity = ItemUtils.getItemRarity(itemInSlot) ?: return@subscribe
            val itemRarityBackground = RARITY_TEXTURES[Config.rarityBackgroundType] ?: return@subscribe
            val itemRarityColor = itemRarity.color.withAlpha(Config.rarityBackgroundOpacity)

            event.guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, itemRarityBackground, event.slot.x, event.slot.y, 16, 16, itemRarityColor.rgba)
        }

        subscribe<GuiEvent.RenderSlot> { event ->
            if(!Config.showRarityBackground) return@subscribe

            val itemRarity = ItemUtils.getItemRarity(event.item) ?: return@subscribe
            val itemRarityBackground = RARITY_TEXTURES[Config.rarityBackgroundType] ?: return@subscribe
            val itemRarityColor = itemRarity.color.withAlpha(Config.rarityBackgroundOpacity)

            event.guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, itemRarityBackground, event.x, event.y, 16, 16, itemRarityColor.rgba)
        }
    }
}