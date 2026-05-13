package com.solidhax.apostle

import com.solidhax.apostle.api.LocationAPI
import com.solidhax.apostle.api.MiningAPI
import com.solidhax.apostle.api.TabListAPI
import com.solidhax.apostle.commands.mainCommand
import com.solidhax.apostle.commands.pathfindingCommand
import com.solidhax.apostle.events.EventDispatcher
import com.solidhax.apostle.modules.internal.ModuleManager
import com.solidhax.apostle.utils.RenderBatchManager
import com.solidhax.apostle.utils.TickTasks
import meteordevelopment.orbit.EventBus
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.minecraft.client.Minecraft
import java.io.File
import java.lang.invoke.MethodHandles

object Apostle : ClientModInitializer {

    @JvmStatic val mc: Minecraft = Minecraft.getInstance()
    @JvmStatic val bus: EventBus = EventBus().apply {
        registerLambdaFactory("com.solidhax.apostle") { lookupInMethod, klass ->
            lookupInMethod.invoke(null, klass, MethodHandles.lookup()) as MethodHandles.Lookup
        }
    }

    val configFile: File = File(mc.gameDirectory, "config/apostle/").apply {
        try {
            if (isFile) delete()
            if (!exists()) mkdirs()
        } catch (e: Exception) {
            println("Error initializing module config\n${e.message}")
        }
    }

    const val MOD_ID = "apostle"

    override fun onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            arrayOf(mainCommand, pathfindingCommand).forEach { commodore -> commodore.register(dispatcher) }
        }

        listOf(EventDispatcher, ModuleManager, TickTasks, RenderBatchManager, TabListAPI, LocationAPI, MiningAPI).forEach { bus.subscribe(it) }
    }
}
