package org.solidhax.apostle

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.minecraft.client.Minecraft
import org.solidhax.apostle.commands.mainCommand
import org.solidhax.apostle.commands.protectItemCommand
import org.solidhax.apostle.config.Config
import org.solidhax.apostle.event.impl.EventBus
import org.solidhax.apostle.event.impl.EventDispatcher
import org.solidhax.apostle.modules.impl.ModuleManager
import org.solidhax.apostle.utils.location.LocationUtils
import org.solidhax.apostle.utils.render.RenderUtils
import org.solidhax.apostle.utils.scheduler.TickScheduler

class Apostle : ClientModInitializer {

    override fun onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register{ dispatcher, _ -> arrayOf(
            mainCommand, protectItemCommand
        ).forEach { command -> command.register(dispatcher) } }

        listOf(this, Config, EventDispatcher, RenderUtils, TickScheduler, LocationUtils, ModuleManager).forEach { EventBus.subscribe(it) }
    }

    companion object {
        const val MOD_ID = "apostle"

        @JvmStatic
        val mc = Minecraft.getInstance()
    }
}
