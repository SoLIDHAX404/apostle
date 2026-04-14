package com.solidhax.apostle

import com.solidhax.apostle.commands.mainCommand
import com.solidhax.apostle.events.EventDispatcher
import com.solidhax.apostle.modules.internal.ModuleManager
import com.solidhax.apostle.utils.TickTasks
import meteordevelopment.orbit.EventBus
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.minecraft.client.Minecraft
import java.lang.invoke.MethodHandles

object Apostle : ClientModInitializer {

    @JvmStatic val mc: Minecraft = Minecraft.getInstance()
    @JvmStatic val bus: EventBus = EventBus().apply {
        registerLambdaFactory("com.solidhax.apostle") { lookupInMethod, klass ->
            lookupInMethod.invoke(null, klass, MethodHandles.lookup()) as MethodHandles.Lookup
        }
    }

    const val MOD_ID = "apostle"

    override fun onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            arrayOf(mainCommand).forEach { commodore -> commodore.register(dispatcher) }
        }

        listOf(EventDispatcher, ModuleManager, TickTasks).forEach { bus.subscribe(it) }
    }
}
