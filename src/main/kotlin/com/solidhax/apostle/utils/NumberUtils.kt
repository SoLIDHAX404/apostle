package com.solidhax.apostle.utils

import kotlin.math.floor
import kotlin.math.pow

fun Double.floorTo(decimals: Int): Double {
    val factor = 10.0.pow(decimals)
    return floor(this * factor) / factor
}