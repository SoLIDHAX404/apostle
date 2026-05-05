package com.solidhax.apostle.utils

import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.ItemStack

fun getSkullTexture(stack: ItemStack): String? {
    if (stack.isEmpty) return null
    val profile = stack.get(DataComponents.PROFILE) ?: return null
    val properties = profile.partialProfile().properties
    return properties["textures"].firstOrNull()?.value
}