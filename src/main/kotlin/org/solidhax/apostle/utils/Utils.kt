package org.solidhax.apostle.utils

import org.solidhax.apostle.Apostle.Companion.mc

inline val mouseX: Float
    get() =
        mc.mouseHandler.xpos().toFloat()

inline val mouseY: Float
    get() =
        mc.mouseHandler.ypos().toFloat()

fun isAreaHovered(x: Float, y: Float, w: Float, h: Float, scaled: Boolean = false): Boolean =
    mouseX in x..(x + w) && mouseY in y..(y + h)

fun Any?.equalsOneOf(vararg options: Any?): Boolean =
    options.any { this == it }

fun String.startsWithOneOf(vararg options: String, ignoreCase: Boolean = false): Boolean =
    options.any { this.startsWith(it, ignoreCase) }