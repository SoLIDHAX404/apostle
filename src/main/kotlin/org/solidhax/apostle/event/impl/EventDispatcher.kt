package org.solidhax.apostle.event.impl

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import org.solidhax.apostle.event.ClientEvent
import org.solidhax.apostle.event.TickEvent
import org.solidhax.apostle.event.WorldEvent

object EventDispatcher {
    init {
        ClientLifecycleEvents.CLIENT_STARTED.register { ClientEvent.Start().postAndCatch() }
        ClientLifecycleEvents.CLIENT_STOPPING.register { ClientEvent.Stop().postAndCatch() }

        ClientPlayConnectionEvents.JOIN.register { _, _, _ -> WorldEvent.Load().postAndCatch() }
        ClientPlayConnectionEvents.DISCONNECT.register { _, _ -> WorldEvent.Unload().postAndCatch() }

        ClientTickEvents.START_WORLD_TICK.register { world -> TickEvent.Start(world).postAndCatch() }
        ClientTickEvents.END_WORLD_TICK.register { world -> TickEvent.End(world).postAndCatch() }
    }
}