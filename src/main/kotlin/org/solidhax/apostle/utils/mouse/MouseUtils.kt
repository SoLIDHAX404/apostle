package org.solidhax.apostle.utils.mouse

import org.solidhax.apostle.Apostle
import org.solidhax.apostle.Apostle.Companion.mc

inline val cursorX: Float get() = mc.mouseHandler.xpos().toFloat()
inline val cursorY: Float get() = mc.mouseHandler.ypos().toFloat()