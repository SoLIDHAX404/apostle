package com.solidhax.apostle.events

import com.solidhax.apostle.api.TabListAPI
import com.solidhax.apostle.utils.RenderConsumer
import net.fabricmc.fabric.api.client.rendering.v1.world.AbstractWorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldExtractionContext
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.Packet

interface TickEvent : Event {
    class Start(val world: ClientLevel) : TickEvent
    class End(val world: ClientLevel) : TickEvent
    object Server : TickEvent
}

interface TabListWidgetEvent : Event {
    val widget: TabListAPI.TabWidget

    class Add(
        override val widget: TabListAPI.TabWidget,
        val newContent: TabListAPI.WidgetContent
    ) : TabListWidgetEvent

    class Update(
        override val widget: TabListAPI.TabWidget,
        val oldContent: TabListAPI.WidgetContent,
        val newContent: TabListAPI.WidgetContent
    ) : TabListWidgetEvent

    class Remove(
        override val widget: TabListAPI.TabWidget,
        val oldContent: TabListAPI.WidgetContent
    ) : TabListWidgetEvent
}

abstract class RenderEvent(open val context: AbstractWorldRenderContext) : Event {
    class Extract(override val context: WorldExtractionContext, val consumer: RenderConsumer) : RenderEvent(context)
    class Last(override val context: WorldRenderContext) : RenderEvent(context)
}

interface WorldEvent : Event {
    object Load : WorldEvent
    object Unload : WorldEvent
}

abstract class PacketEvent(val packet: Packet<*>) : CancellableEvent() {
    class Receive(packet: Packet<*>) : PacketEvent(packet)
    class Send(packet: Packet<*>) : PacketEvent(packet)
}

class ChatPacketEvent(val value: String, val component: Component) : Event