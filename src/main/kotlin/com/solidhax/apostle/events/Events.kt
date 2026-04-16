package com.solidhax.apostle.events

import com.solidhax.apostle.api.TabListAPI
import net.minecraft.client.multiplayer.ClientLevel

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
