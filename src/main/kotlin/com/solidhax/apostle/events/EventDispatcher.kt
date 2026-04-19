package com.solidhax.apostle.events

import com.solidhax.apostle.utils.RenderBatchManager
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents

object EventDispatcher {

    init {
        ClientPlayConnectionEvents.JOIN.register { _, _, _ -> WorldEvent.Load.post() }
        ClientPlayConnectionEvents.DISCONNECT.register { _, _ -> WorldEvent.Unload.post() }

        ClientTickEvents.START_WORLD_TICK.register { world -> TickEvent.Start(world).post() }
        ClientTickEvents.END_WORLD_TICK.register { world -> TickEvent.End(world).post() }

        WorldRenderEvents.END_EXTRACTION.register { handler -> RenderEvent.Extract(handler, RenderBatchManager.renderConsumer).post() }
        WorldRenderEvents.END_MAIN.register { context -> RenderEvent.Last(context).post() }
    }


}