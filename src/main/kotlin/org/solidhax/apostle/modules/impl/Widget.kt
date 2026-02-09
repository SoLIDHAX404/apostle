package org.solidhax.apostle.modules.impl

import com.google.gson.Gson
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import org.solidhax.apostle.Apostle.Companion.mc
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

open class Widget(var x: Int = 10, var y: Int = 10, var width: Int = 100, var height: Int = 30) {
    var id: String = ""
        private set

    var moduleId: String = ""
        private set

    fun render(g: GuiGraphics) {
        val (newW, newH) = draw(g)

        if (newW > 0) width = newW
        if (newH > 0) height = newH
    }

    open fun draw(guiGraphics: GuiGraphics): Pair<Int, Int> {
        return width to height
    }

    fun loadConfig(gson: Gson, json: Any?) {
        val map = json as? Map<*, *> ?: return

        (map["x"] as? Number)?.let { x = it.toInt() }
        (map["y"] as? Number)?.let { y = it.toInt() }
    }

    fun saveConfig(): Any = mapOf("x" to x, "y" to y)

    fun font(): Font = mc.font
    fun textWidth(text: String) = font().width(text)

    operator fun provideDelegate(thisRef: Module, property: KProperty<*>): ReadOnlyProperty<Module, Widget> {
        id = property.name
        moduleId = thisRef.name
        thisRef.registerWidget(this)

        return ReadOnlyProperty { _, _ -> this }
    }
}

class HUDWidget(
    val name: String,
    val description: String,
    val enabledByDefault: Boolean,
    private val renderBlock: Widget.(GuiGraphics) -> Pair<Int, Int>,
) : Widget() {
    override fun draw(guiGraphics: GuiGraphics): Pair<Int, Int> = renderBlock(guiGraphics)
}

fun HUD(
    name: String,
    description: String,
    enabledByDefault: Boolean = true,
    renderBlock: Widget.(GuiGraphics) -> Pair<Int, Int>,
): HUDWidget = HUDWidget(name, description, enabledByDefault, renderBlock)

