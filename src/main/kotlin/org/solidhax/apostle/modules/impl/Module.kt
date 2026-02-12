package org.solidhax.apostle.modules.impl

import com.google.gson.Gson
import net.minecraft.client.gui.GuiGraphics
import org.solidhax.apostle.Apostle
import org.solidhax.apostle.event.impl.EventBus

abstract class Module(val defaultConfig: String? = null) {
    private val widgetsInternal: MutableList<HudElement> = mutableListOf()

    protected inline val mc get() = Apostle.mc

    val widgets: List<HudElement> get() = widgetsInternal.toList()

    internal fun registerWidget(widget: HudElement) {
        widgetsInternal.add(widget)
    }

    init {
        EventBus.subscribe(this)
    }

    @Suppress("FunctionName")
    fun HUD(
        name: String,
        x: Int = 10,
        y: Int = 10,
        scale: Float = 2f,
        anchorPoint: AnchorPoint = AnchorPoint.TOP_LEFT,
        block: GuiGraphics.(example: Boolean) -> Pair<Int, Int>
    ): HudElement = HudElement(name, x, y, scale, false, anchorPoint, this, block).also { registerWidget(it) }

    open fun loadConfig(gson: Gson, json: Any?) {}
    open fun saveConfig(gson: Gson): Any? = null

    fun loadDefaultConfig(): String? {
        val res = defaultConfig ?: return null
        val stream = javaClass.classLoader.getResourceAsStream(res) ?: return null
        return stream.bufferedReader().use { it.readText() }
    }
}