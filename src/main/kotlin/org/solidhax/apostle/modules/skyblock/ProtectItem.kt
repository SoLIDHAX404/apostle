package org.solidhax.apostle.modules.skyblock

import com.google.gson.Gson
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import org.solidhax.apostle.event.AbstractContainerScreenEvent
import org.solidhax.apostle.event.ItemEvent
import org.solidhax.apostle.event.impl.subscribe
import org.solidhax.apostle.modules.impl.Module
import org.solidhax.apostle.utils.chat.modMessage
import org.solidhax.apostle.utils.item.itemUUID
import org.solidhax.apostle.utils.location.Area
import org.solidhax.apostle.utils.location.LocationUtils

object ProtectItem : Module(name = "Protect Item", description = "Protects items based on uuid.", defaultConfig = "protectitem.json") {
    private val protectedItems = hashSetOf<String>()

    init {
        subscribe<AbstractContainerScreenEvent.KeyPress> { event ->
            if(!LocationUtils.isInSkyblock) return@subscribe
            if (!mc.options.keyDrop.matches(event.keyEvent)) return@subscribe

            val stack = event.hoveredSlot?.item ?: return@subscribe
            shouldCancelDrop(stack) { event.cancel() }
        }

        subscribe<AbstractContainerScreenEvent.SlotClicked> { event ->
            if(!LocationUtils.isInSkyblock) return@subscribe
            if (event.slotId != -999) return@subscribe

            val stack = event.carriedItem ?: return@subscribe
            shouldCancelDrop(stack) { event.cancel() }
        }

        subscribe<ItemEvent.Drop> { event ->
            if(!LocationUtils.isInSkyblock || LocationUtils.currentArea == Area.Dungeon) return@subscribe

            shouldCancelDrop(event.item) { event.cancel() }
        }
    }

    fun onProtectItem() {
        val player = mc.player ?: return
        val stack = player.mainHandItem
        if (stack.isEmpty) return

        val uuid = stack.itemUUID
        if (uuid == null) {
            modMessage(Component.literal("Cannot protect this item because it doesn't have a valid uuid.").withStyle(ChatFormatting.RED))
            return
        }

        val nowProtected = protectedItems.add(uuid)
        if (!nowProtected) protectedItems.remove(uuid)

        val msg = stack.hoverName.copy().append(
            Component.literal(if (nowProtected) " will now be protected!" else " will no longer be protected!").withStyle(if (nowProtected) ChatFormatting.GREEN else ChatFormatting.RED)
        )

        modMessage(msg)
    }

    private inline fun shouldCancelDrop(stack: ItemStack, cancel: () -> Unit) {
        if (stack.isEmpty) return

        val uuid = stack.itemUUID ?: return
        if (!protectedItems.contains(uuid)) return

        val message = Component.literal("Stopped you from dropping your ").withStyle(ChatFormatting.RED).append(stack.hoverName)

        modMessage(message)
        cancel()
    }

    override fun loadConfig(gson: Gson, json: Any?) {
        protectedItems.clear()
        if (json is List<*>) {
            json.filterIsInstance<String>().forEach(protectedItems::add)
        }
    }

    override fun saveConfig(gson: Gson): Any = protectedItems.toList()
}
