package org.solidhax.apostle.modules.impl

import com.google.gson.Gson
import net.minecraft.client.gui.GuiGraphics
import org.solidhax.apostle.Apostle.Companion.mc
import org.solidhax.apostle.utils.isAreaHovered
import org.solidhax.apostle.utils.render.Colors
import kotlin.reflect.KProperty


enum class AnchorPoint {
    TOP_LEFT,
    TOP,
    TOP_RIGHT,
    LEFT,
    CENTER,
    RIGHT,
    BOTTOM_LEFT,
    BOTTOM,
    BOTTOM_RIGHT;

    fun resolveX(screenWidth: Int, scaledWidth: Int): Int =
        when (this) {
            TOP_LEFT, LEFT, BOTTOM_LEFT -> 0
            TOP, CENTER, BOTTOM -> (screenWidth - scaledWidth) / 2
            TOP_RIGHT, RIGHT, BOTTOM_RIGHT -> screenWidth - scaledWidth
        }

    fun resolveY(screenHeight: Int, scaledHeight: Int): Int =
        when (this) {
            TOP_LEFT, TOP, TOP_RIGHT -> 0
            LEFT, CENTER, RIGHT -> (screenHeight - scaledHeight) / 2
            BOTTOM_LEFT, BOTTOM, BOTTOM_RIGHT -> screenHeight - scaledHeight
        }
}


open class HudElement(
    var x: Int,
    var y: Int,
    var scale: Float,
    var enabled: Boolean = true,
    var anchorPoint: AnchorPoint = AnchorPoint.TOP_LEFT,
    val owner: Module? = null,
    val render: GuiGraphics.(Boolean) -> Pair<Int, Int> = { _ -> 0 to 0 }
) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): HudElement = this

    var width: Int = 0
        private set
    var height: Int = 0
        private set


    private val scaledWidth: Int get() = (width * scale).toInt()
    private val scaledHeight: Int get() = (height * scale).toInt()

    fun getScreenPosition(): Pair<Int, Int> {
        val screenX = anchorPoint.resolveX(mc.window.width, scaledWidth) + x
        val screenY = anchorPoint.resolveY(mc.window.height, scaledHeight) + y
        return screenX to screenY
    }

    fun setScreenPosition(screenX: Int, screenY: Int) {
        val maxX = (mc.window.width - scaledWidth).coerceAtLeast(0)
        val maxY = (mc.window.height - scaledHeight).coerceAtLeast(0)
        val clampedX = screenX.coerceIn(0, maxX)
        val clampedY = screenY.coerceIn(0, maxY)

        val originX = anchorPoint.resolveX(mc.window.width, scaledWidth)
        val originY = anchorPoint.resolveY(mc.window.height, scaledHeight)
        x = clampedX - originX
        y = clampedY - originY
    }


    fun draw(context: GuiGraphics, example: Boolean) {

        val (screenX, screenY) = getScreenPosition()

        if(example) context.fill(
            screenX - 5,
            screenY - 5,
            screenX + scaledWidth + 5,
            screenY + scaledHeight + 5,
            Colors.BLACK.withAlpha(0.55f).rgba
        )

        context.pose().pushMatrix()
        context.pose().translate(screenX.toFloat(), screenY.toFloat())

        context.pose().scale(scale, scale)

        val (width, height) = context.render(example).let { (w, h) -> w to h }

        context.pose().popMatrix()

        this.width = width
        this.height = height
    }

    fun loadConfig(json: Any?) {
        val map = json as? Map<*, *> ?: return

        (map["x"] as? Number)?.let { x = it.toInt() }
        (map["y"] as? Number)?.let { y = it.toInt() }
        (map["scale"] as? Number)?.let { scale = it.toFloat() }
        (map["anchorPoint"] as? String)?.let { value ->
            anchorPoint = AnchorPoint.entries.firstOrNull { point -> point.name == value } ?: AnchorPoint.TOP_LEFT
        }

    }

    fun saveConfig(): Any = mapOf("x" to x, "y" to y, "scale" to scale, "anchorPoint" to anchorPoint.name)

    fun isHovered(): Boolean {
        val (screenX, screenY) = getScreenPosition()
        return isAreaHovered(screenX.toFloat(), screenY.toFloat(), width * scale, height * scale)
    }
}