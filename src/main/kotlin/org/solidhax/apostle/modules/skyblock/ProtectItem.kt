package org.solidhax.apostle.modules.skyblock

import com.google.gson.Gson
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import org.solidhax.apostle.event.AbstractContainerScreenEvent
import org.solidhax.apostle.event.ItemEvent
import org.solidhax.apostle.event.impl.subscribe
import org.solidhax.apostle.modules.impl.Module
import org.solidhax.apostle.utils.chat.modMessage
import org.solidhax.apostle.utils.item.itemUUID

object ProtectItem : Module(name = "Protect Item", description = "Protects items based on uuid.", defaultConfig = "protectitem.json") {
    val protectedItems = ArrayList<String>()

    init {
        subscribe<AbstractContainerScreenEvent.KeyPress> { event ->
            val slot = event.hoveredSlot ?: return@subscribe
            val itemInSlot = slot.item ?: return@subscribe
            val itemName = itemInSlot.styledHoverName ?: return@subscribe
            val itemUUID = itemInSlot.itemUUID ?: return@subscribe

            if(protectedItems.contains(itemUUID) && mc.options.keyDrop.matches(event.keyEvent)) {
                val message = Component.literal("Stopped your from dropping your ").withStyle(ChatFormatting.RED).append(itemName)
                modMessage(message)

                event.cancel()
            }
        }

        subscribe<AbstractContainerScreenEvent.SlotClicked> { event ->
            if(event.slotId != -999) return@subscribe
            if(event.carriedItem == null || event.carriedItem.isEmpty) return@subscribe

            val itemName = event.carriedItem.styledHoverName ?: return@subscribe
            val itemUUID = event.carriedItem.itemUUID ?: return@subscribe

            if(protectedItems.contains(itemUUID)) {
                val message = Component.literal("Stopped your from dropping your ").withStyle(ChatFormatting.RED).append(itemName)
                modMessage(message)

                event.cancel()
            }
        }

        subscribe<ItemEvent.Drop> { event ->
            val heldItem = event.item
            if(heldItem.isEmpty) return@subscribe

            val heldItemName = heldItem.styledHoverName ?: return@subscribe
            val heldItemUUID = heldItem.itemUUID ?: return@subscribe

            if(protectedItems.contains(heldItemUUID)) {
                val message = Component.literal("Stopped your from dropping your ").withStyle(ChatFormatting.RED).append(heldItemName)
                modMessage(message)

                event.cancel()
            }
        }
    }

    fun onProtectItem() {
        val player = mc.player ?: return
        val heldItem = player.mainHandItem
        if(heldItem.isEmpty) return
        val heldItemName = heldItem.customName ?: return
        val heldItemUUID = heldItem.itemUUID

        if(heldItemUUID == null) {
            val message = Component.literal("cannot protect this item as it doesnt have a valid uuid.").withStyle(ChatFormatting.RED)
            modMessage(message)
            return
        }

        if(protectedItems.contains(heldItemUUID)) {
            protectedItems.remove(heldItemUUID)

            val message = heldItemName.copy().append(Component.literal(" will no longer be protected!").withStyle(ChatFormatting.RED))
            modMessage(message)
            return
        }

        protectedItems.add(heldItemUUID)

        val message = heldItemName.copy().append(Component.literal(" will now be protected!").withStyle(ChatFormatting.GREEN))
        modMessage(message)
    }

    override fun loadConfig(gson: Gson, json: Any?) {
        protectedItems.clear()

        if(json is List<*>) {
            json.filterIsInstance<String>().forEach { protectedItems.add(it) }
        }
    }

    override fun saveConfig(gson: Gson): Any {
        return protectedItems
    }
}