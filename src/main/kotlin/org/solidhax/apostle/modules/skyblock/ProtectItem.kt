package org.solidhax.apostle.modules.skyblock

import com.google.gson.Gson
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import org.solidhax.apostle.event.ItemEvent
import org.solidhax.apostle.event.impl.subscribe
import org.solidhax.apostle.modules.impl.Module
import org.solidhax.apostle.utils.chat.modMessage
import org.solidhax.apostle.utils.item.itemUUID

object ProtectItem : Module(name = "Protect Item", description = "Protects items based on uuid.", defaultConfig = "protectitem.json") {
    val protectedItems = ArrayList<String>()

    init {
        subscribe<ItemEvent.Drop> { event ->
            val heldItem = event.item
            if(heldItem.isEmpty) return@subscribe

            val heldItemName = heldItem.customName ?: return@subscribe

            if(protectedItems.contains(event.item.itemUUID)) {
                val message = Component.literal("Stopped your from dropping your ").withStyle(ChatFormatting.RED).append(heldItemName)
                modMessage(message)

                event.cancel()
            }
        }
    }

    fun onProtectItem() {
        val player = mc.player ?: return
        val heldItem = player.mainHandItem ?: return
        val heldItemName = heldItem.customName ?: return
        val heldItemUUID = heldItem.itemUUID

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