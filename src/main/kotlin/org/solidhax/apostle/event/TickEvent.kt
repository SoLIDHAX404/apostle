package org.solidhax.apostle.event

import net.minecraft.client.multiplayer.ClientLevel
import org.solidhax.apostle.event.impl.Event

abstract class TickEvent : Event() {
    class Start(val world: ClientLevel) : TickEvent()
    class End(val world: ClientLevel) : TickEvent()
}