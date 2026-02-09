package org.solidhax.apostle.modules.mining

import org.solidhax.apostle.config.Config
import org.solidhax.apostle.event.AbstractContainerScreenEvent
import org.solidhax.apostle.event.impl.subscribe
import org.solidhax.apostle.modules.impl.Module
import org.solidhax.apostle.utils.item.loreString
import org.solidhax.apostle.utils.render.Colors

object Commissions : Module(name = "Commissions", "Various qol surrounding commissions") {

    init {
        subscribe<AbstractContainerScreenEvent.RenderSlot> { event ->
            if(!Config.highlightCompletedCommissions)
            if(!event.screen.title.string.contains("Commissions")) return@subscribe

            val slot = event.slot
            val itemInSlot = slot.item?.takeUnless { it.isEmpty } ?: return@subscribe
            val itemLore = itemInSlot.loreString

            if(!itemLore.contains("COMPLETED")) return@subscribe

            event.guiGraphics.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, Colors.MINECRAFT_GREEN.rgba)
        }
    }
}