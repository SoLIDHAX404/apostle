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
}