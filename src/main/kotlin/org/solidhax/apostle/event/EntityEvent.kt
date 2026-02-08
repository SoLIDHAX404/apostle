package org.solidhax.apostle.event

import net.minecraft.network.protocol.Packet
import net.minecraft.world.entity.Entity
import org.solidhax.apostle.event.impl.CancellableEvent

abstract class EntityEvent(val entity: Entity) : CancellableEvent() {
    class Add(entity: Entity) : EntityEvent(entity)
    class Metadata(entity: Entity, packet: Packet<*>) : EntityEvent(entity)
}