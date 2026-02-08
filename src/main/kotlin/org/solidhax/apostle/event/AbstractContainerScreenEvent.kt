package org.solidhax.apostle.event

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.world.inventory.Slot
import org.solidhax.apostle.event.impl.CancellableEvent

abstract class AbstractContainerScreenEvent(val screen: Screen) : CancellableEvent() {
    class RenderSlot(screen: Screen, val guiGraphics: GuiGraphics, val slot: Slot) : AbstractContainerScreenEvent(screen)
}