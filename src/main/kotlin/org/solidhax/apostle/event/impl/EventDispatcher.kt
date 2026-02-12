package org.solidhax.apostle.event.impl

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket
import org.solidhax.apostle.Apostle.Companion.mc
import org.solidhax.apostle.event.ChatPacketEvent
import org.solidhax.apostle.event.ClientEvent
import org.solidhax.apostle.event.PacketEvent
import org.solidhax.apostle.event.RenderEvent
import org.solidhax.apostle.event.TickEvent
import org.solidhax.apostle.event.WorldEvent
import org.solidhax.apostle.utils.noControlCodes

object EventDispatcher {
    init {
        ClientLifecycleEvents.CLIENT_STARTED.register { ClientEvent.Start().postAndCatch() }
        ClientLifecycleEvents.CLIENT_STOPPING.register { ClientEvent.Stop().postAndCatch() }

        ClientPlayConnectionEvents.JOIN.register { _, _, _ -> WorldEvent.Load().postAndCatch() }
        ClientPlayConnectionEvents.DISCONNECT.register { _, _ -> WorldEvent.Unload().postAndCatch() }

        ClientTickEvents.START_WORLD_TICK.register { world -> TickEvent.Start(world).postAndCatch() }
        ClientTickEvents.END_WORLD_TICK.register { world -> TickEvent.End(world).postAndCatch() }

        WorldRenderEvents.END_EXTRACTION.register { handler -> mc.level.let { RenderEvent.Extract(handler).postAndCatch() } }
        WorldRenderEvents.END_MAIN.register { context -> mc.level.let { RenderEvent.Last(context).postAndCatch() } }

        on<PacketEvent.Receive> {
            if(packet !is ClientboundSystemChatPacket) return@on

            if(!packet.overlay) packet.content.string?.noControlCodes?.let { ChatPacketEvent(it, packet.content).postAndCatch() }
        }
    }
}