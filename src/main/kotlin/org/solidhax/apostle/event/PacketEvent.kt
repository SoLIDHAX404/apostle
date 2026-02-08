package org.solidhax.apostle.event

import net.minecraft.network.protocol.Packet
import org.solidhax.apostle.event.impl.CancellableEvent

abstract class PacketEvent(val packet: Packet<*>) : CancellableEvent() {
    class Receive(packet: Packet<*>) : PacketEvent(packet)
    class Send(packet: Packet<*>) : PacketEvent(packet)
}