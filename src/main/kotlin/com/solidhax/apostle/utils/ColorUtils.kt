package com.solidhax.apostle.utils

import java.awt.Color

fun Color.toMinecraftColor(): Int {
    return ((alpha and 0xFF) shl 24) or
        ((red and 0xFF) shl 16) or
        ((green and 0xFF) shl 8) or
        (blue and 0xFF)
}
