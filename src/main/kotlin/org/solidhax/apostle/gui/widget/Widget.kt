package org.solidhax.apostle.gui.widget

import net.minecraft.client.gui.GuiGraphics
import org.solidhax.apostle.utils.render.NVGSpecialRenderer
import org.solidhax.apostle.modules.impl.Module
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

open class Widget(
    var x: Float = 500f,
    var y: Float = 500f,
    var width: Float = 100f,
    var height: Float = 24f,
) {
    var id: String = ""
        private set

    var moduleId: String = ""
        private set

    fun render(context: GuiGraphics) {
        NVGSpecialRenderer.draw(context, x.toInt(), y.toInt(), width.toInt(), height.toInt()) {
            val (newWidth, newHeight) = draw()

            if(newWidth > 0 && newHeight > 0) {
                width = newWidth.toFloat()
                height = newHeight.toFloat()
            }
        }
    }

    open fun draw(): Pair<Int, Int> {
        return width.toInt() to height.toInt()
    }

    operator fun provideDelegate(thisRef: Module, property: KProperty<*>): ReadOnlyProperty<Module, Widget> {
        id = property.name
        moduleId = thisRef.id
        thisRef.registerWidget(this)

        return object : ReadOnlyProperty<Module, Widget> {
            override fun getValue(thisRef: Module, property: KProperty<*>): Widget = this@Widget
        }
    }
}

class HUDWidget(
    val name: String,
    val description: String,
    val enabledByDefault: Boolean,
    private val renderBlock: Widget.() -> Pair<Int, Int>,
) : Widget() {
    override fun draw(): Pair<Int, Int> = renderBlock()
}

fun HUD(
    name: String,
    description: String,
    enabledByDefault: Boolean = true,
    renderBlock: Widget.() -> Pair<Int, Int>,
): HUDWidget = HUDWidget(name, description, enabledByDefault, renderBlock)

