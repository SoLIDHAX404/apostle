package org.solidhax.apostle.utils.render

data class Color(val red: Int, val green: Int, val blue: Int, val alpha: Int = 255) {

    fun redB() = red.toByte()
    fun greenB() = green.toByte()
    fun blueB() = blue.toByte()
    fun alphaB() = alpha.toByte()

    init {
        require(red in 0..255)
        require(green in 0..255)
        require(blue in 0..255)
        require(alpha in 0..255)
    }
}

object Colors {
    val RED = Color(255, 0, 0, 255)
    val GREEN = Color(0, 255, 0, 255)
    val BLUE = Color(0, 0, 255, 255)
    val WHITE = Color(255, 255, 255, 255)
    val BLACK = Color(0, 0, 0, 255)
    val MINECRAFT_GREEN = Color(85, 255, 85, 255)
    val MINECRAFT_BLUE = Color(85, 85, 255, 255)
    val MINECRAFT_DARK_PURPLE = Color(170, 0, 170, 255)
    val MINECRAFT_GOLD = Color(255, 170, 0, 255)
    val MINECRAFT_LIGHT_PURPLE = Color(255, 85, 255, 255)
    val MINECRAFT_AQUA = Color(85, 255, 255, 255)
    val MINECRAFT_RED = Color(255, 85, 85, 255)
    val UI_BACKGROUND = Color(14, 14, 15, 255)
    val UI_SURFACE    = Color(26, 26, 27, 255)

}
