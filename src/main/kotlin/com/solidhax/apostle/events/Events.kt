package com.solidhax.apostle.events

import com.solidhax.apostle.api.TabListAPI
import com.solidhax.apostle.utils.RenderConsumer
import net.fabricmc.fabric.api.client.rendering.v1.world.AbstractWorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldExtractionContext
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.Packet
import net.minecraft.world.inventory.ClickType
import net.minecraft.world.inventory.Slot

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

abstract class GuiEvent(val screen: Screen) : CancellableEvent() {
    class RenderSlot(screen: Screen, val guiGraphics: GuiGraphics, val slot: Slot) : GuiEvent(screen)
    class SlotClicked(screen: Screen, val slot: Slot, val clickType: ClickType) : GuiEvent(screen)
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

abstract class VisitorEvent(val visitor: String) : Event {
    class VisitorAccepted(visitor: String) : VisitorEvent(visitor)
    class VisitorRefused(visitor: String) : VisitorEvent(visitor)
}