package org.solidhax.apostle.event

import net.minecraft.world.item.ItemStack
import org.solidhax.apostle.event.impl.CancellableEvent

abstract class ItemEvent(val item: ItemStack) : CancellableEvent() {
    class Drop(item: ItemStack) : ItemEvent(item)
}