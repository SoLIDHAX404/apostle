package com.solidhax.apostle.events

import net.minecraft.client.multiplayer.ClientLevel

abstract class TickEvent(val world: ClientLevel) : Event {
    class Start(world: ClientLevel) : TickEvent(world)
    class End(world: ClientLevel) : TickEvent(world)
}