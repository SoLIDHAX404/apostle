package org.solidhax.apostle.utils.render

import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

class Font {
    val name: String
    private val fontBytes: ByteArray?

    constructor(name: String, inputStream: InputStream) {
        this.name = name
        this.fontBytes = inputStream.use { it.readBytes() }
    }

    fun buffer(): ByteBuffer {
        val bytes = fontBytes ?: throw Exception("Failed to load font")

        return ByteBuffer.allocateDirect(bytes.size).order(ByteOrder.nativeOrder()).put(bytes).flip() as ByteBuffer
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return other is Font && name == other.name
    }
}