package org.solidhax.apostle

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.fabric.api.client.rendering.v1.SpecialGuiElementRegistry
import net.minecraft.client.Minecraft
import org.solidhax.apostle.commands.mainCommand
import org.solidhax.apostle.modules.impl.ModuleManager
import org.solidhax.apostle.utils.render.NVGSpecialRenderer
import org.solidhax.apostle.utils.scheduler.TickScheduler

class Apostle : ClientModInitializer {

    override fun onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register{ dispatcher, _ -> arrayOf(mainCommand).forEach { command -> command.register(dispatcher) } }
        ClientLifecycleEvents.CLIENT_STOPPING.register{ ModuleManager.shutdown() }
        SpecialGuiElementRegistry.register { context -> NVGSpecialRenderer(context.vertexConsumers()) }

        ModuleManager.init()
        TickScheduler.init()
    }

    companion object {
        const val MOD_ID = "apostle"

        @JvmStatic
        val mc = Minecraft.getInstance()
    }
}
