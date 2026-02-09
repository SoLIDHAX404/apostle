package org.solidhax.apostle.modules.impl

import com.google.gson.Gson
import net.minecraft.client.gui.GuiGraphics
import org.solidhax.apostle.utils.isAreaHovered
import org.solidhax.apostle.utils.render.Colors
import kotlin.reflect.KProperty

open class HudElement(
    var x: Int,
    var y: Int,
    var scale: Float,
    var enabled: Boolean = true,
    val owner: Module? = null,
    val render: GuiGraphics.(Boolean) -> Pair<Int, Int> = { _ -> 0 to 0 }
) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): HudElement = this

    var width: Int = 0
        private set
    var height: Int = 0
        private set

    fun draw(context: GuiGraphics, example: Boolean) {

        if(example) context.fill(
            x - 5,
            y - 5,
            x + (width * scale).toInt() + 5,
            y + (height * scale).toInt() + 5,
            Colors.BLACK.withAlpha(0.55f).rgba
        )

        context.pose().pushMatrix()
        context.pose().translate(x.toFloat(), y.toFloat())

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
    }

    fun saveConfig(): Any = mapOf("x" to x, "y" to y, "scale" to scale)

    fun isHovered(): Boolean = isAreaHovered(x.toFloat(), y.toFloat(), width * scale, height * scale)
}