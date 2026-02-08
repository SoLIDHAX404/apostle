package org.solidhax.apostle.utils.chat

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import org.solidhax.apostle.Apostle.Companion.mc

fun modMessage(message: Any?, prefix: String = "§9Apostle §8»§r ", chatStyle: Style? = null) {
    val text = Component.literal("$prefix$message")
    chatStyle?.let { text.setStyle(chatStyle) }
    mc.execute { mc.gui?.chat?.addMessage(text) }
}

fun modMessage(message: Component, prefix: String = "§9Apostle §8»§r ", chatStyle: Style? = null) {
    val text = Component.literal(prefix).append(message)
    chatStyle?.let { text.setStyle(chatStyle) }
    mc.execute { mc.gui?.chat?.addMessage(text) }
}