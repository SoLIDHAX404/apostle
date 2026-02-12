package org.solidhax.apostle.modules.skyblock

import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.resources.ResourceLocation
import org.solidhax.apostle.event.AbstractContainerScreenEvent
import org.solidhax.apostle.event.GuiEvent
import org.solidhax.apostle.event.impl.on
import org.solidhax.apostle.modules.impl.Module
import org.solidhax.apostle.utils.item.ItemUtils
import org.solidhax.apostle.utils.location.LocationUtils

object RarityDisplay : Module() {

    var enabled = false
    var opacity = 1f
    var type = 0

    val RARITY_TEXTURES: Array<ResourceLocation?> = arrayOf<ResourceLocation?>(
        ResourceLocation.fromNamespaceAndPath("apostle", "rarity"),
        ResourceLocation.fromNamespaceAndPath("apostle", "rarity2"),
        ResourceLocation.fromNamespaceAndPath("apostle", "rarity3"),
        ResourceLocation.fromNamespaceAndPath("apostle", "rarity4")
    )

    init {
        on<AbstractContainerScreenEvent.RenderSlot> {
            if(!enabled || !LocationUtils.isInSkyblock) return@on

            val itemInSlot = slot.item ?: return@on
            if(itemInSlot.isEmpty) return@on

            val itemRarity = ItemUtils.getItemRarity(itemInSlot) ?: return@on
            val itemRarityBackground = RARITY_TEXTURES[type] ?: return@on
            val itemRarityColor = itemRarity.color.withAlpha(opacity)

            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, itemRarityBackground, slot.x, slot.y, 16, 16, itemRarityColor.rgba)
        }

        on<GuiEvent.RenderSlot> {
            if(!enabled || !LocationUtils.isInSkyblock) return@on

            val itemRarity = ItemUtils.getItemRarity(item) ?: return@on
            val itemRarityBackground = RARITY_TEXTURES[type] ?: return@on
            val itemRarityColor = itemRarity.color.withAlpha(opacity)

            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, itemRarityBackground, x, y, 16, 16, itemRarityColor.rgba)
        }
    }
}