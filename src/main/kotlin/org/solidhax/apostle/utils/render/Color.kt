package org.solidhax.apostle.utils.render

data class Color(val red: Int, val green: Int, val blue: Int, val alpha: Float = 1.0f) {

    val rgba: Int
        get() {
            val a = (alpha.coerceIn(0f, 1f) * 255).toInt() and 0xFF
            return (a shl 24) or
                    ((red   and 0xFF) shl 16) or
                    ((green and 0xFF) shl 8) or
                    ((blue  and 0xFF))
        }

    fun redB() = red.toByte()
    fun greenB() = green.toByte()
    fun blueB() = blue.toByte()
    fun alphaB() = (alpha.coerceIn(0f, 1f) * 255).toInt().toByte()

    fun withAlpha(alpha: Float, newInstance: Boolean = true): Color {
        return if (newInstance) {
            Color(red, green, blue, alpha)
        } else {
            this.copy(alpha = alpha)
        }
    }

    init {
        require(red in 0..255)
        require(green in 0..255)
        require(blue in 0..255)
        require(alpha in 0f..1f)
    }
}


object Colors {
    val RED = Color(255, 0, 0, 1f)
    val GREEN = Color(0, 255, 0, 1f)
    val BLUE = Color(0, 0, 255, 1f)
    val WHITE = Color(255, 255, 255, 1f)
    val BLACK = Color(0, 0, 0, 1f)
    val MINECRAFT_GREEN = Color(85, 255, 85, 1f)
    val MINECRAFT_BLUE = Color(85, 85, 255, 1f)
    val MINECRAFT_DARK_PURPLE = Color(170, 0, 170, 1f)
    val MINECRAFT_GOLD = Color(255, 170, 0, 1f)
    val MINECRAFT_LIGHT_PURPLE = Color(255, 85, 255, 1f)
    val MINECRAFT_AQUA = Color(85, 255, 255, 1f)
    val MINECRAFT_RED = Color(255, 85, 85, 1f)
}
