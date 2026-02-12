package org.solidhax.apostle.modules.mining

import org.solidhax.apostle.event.AbstractContainerScreenEvent
import org.solidhax.apostle.event.impl.on
import org.solidhax.apostle.modules.impl.Module
import org.solidhax.apostle.utils.item.loreString
import org.solidhax.apostle.utils.render.Colors

object Commissions : Module() {

    var highlightCompletedCommissions = false

    init {
        on<AbstractContainerScreenEvent.RenderSlot> {
            if(!highlightCompletedCommissions || !screen.title.string.contains("Commissions")) return@on

            val itemInSlot = slot.item?.takeUnless { it.isEmpty } ?: return@on
            val itemLore = itemInSlot.loreString

            if(!itemLore.contains("COMPLETED")) return@on

            guiGraphics.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, Colors.MINECRAFT_GREEN.rgba)
        }
    }
}