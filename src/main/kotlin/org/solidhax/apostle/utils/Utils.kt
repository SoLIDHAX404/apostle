package org.solidhax.apostle.utils

fun Any?.equalsOneOf(vararg options: Any?): Boolean =
    options.any { this == it }

fun String.startsWithOneOf(vararg options: String, ignoreCase: Boolean = false): Boolean =
    options.any { this.startsWith(it, ignoreCase) }