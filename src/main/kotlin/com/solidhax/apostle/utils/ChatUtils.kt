package com.solidhax.apostle.utils

import com.solidhax.apostle.Apostle.mc
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style

fun playerMessage(message: Any?, prefix: String = "§7[§5Apostle§7] ", chatStyle: Style? = null) {
    val text = Component.literal("$prefix$message")
    chatStyle?.let { text.setStyle(chatStyle) }
    mc.execute { mc.gui.chat.addMessage(text) }
}

fun errorMessage(throwable: Throwable, context: Any) {
    val message = "§cCaught an ${throwable.javaClass.simpleName} at ${context.javaClass.simpleName}"
    playerMessage(message)
}