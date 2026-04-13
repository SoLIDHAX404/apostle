package com.solidhax.apostle.events

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents

object EventDispatcher {

    init {
        ClientTickEvents.START_WORLD_TICK.register { world -> TickEvent.Start(world).post() }
        ClientTickEvents.END_WORLD_TICK.register { world -> TickEvent.End(world).post() }
    }

}