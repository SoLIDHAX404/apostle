package org.solidhax.apostle.event

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.KeyEvent
import net.minecraft.world.inventory.ClickType
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import org.solidhax.apostle.event.impl.CancellableEvent

abstract class AbstractContainerScreenEvent(val screen: Screen) : CancellableEvent() {
    class RenderSlot(screen: Screen, val guiGraphics: GuiGraphics, val slot: Slot) : AbstractContainerScreenEvent(screen)
    class KeyPress(screen: Screen, val keyEvent: KeyEvent, val hoveredSlot: Slot?) : AbstractContainerScreenEvent(screen)
    class SlotClicked(screen: Screen, val slotId: Int, val clickType: ClickType, val carriedItem: ItemStack?) : AbstractContainerScreenEvent(screen)
}