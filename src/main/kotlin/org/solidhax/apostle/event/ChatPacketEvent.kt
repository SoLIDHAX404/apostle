package org.solidhax.apostle.event

import net.minecraft.network.chat.Component
import org.solidhax.apostle.event.impl.Event

class ChatPacketEvent(val value: String, val component: Component) : Event()