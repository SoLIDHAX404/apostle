package org.solidhax.apostle.event

import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.world.item.ItemStack
import org.solidhax.apostle.event.impl.CancellableEvent

abstract class GuiEvent(val gui: Gui) : CancellableEvent() {
    class RenderSlot(gui: Gui, val guiGraphics: GuiGraphics, val x: Int, val y: Int, val item: ItemStack) : GuiEvent(gui)
}