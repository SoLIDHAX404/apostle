package org.solidhax.apostle.modules.impl

import com.google.gson.Gson
import net.minecraft.client.gui.GuiGraphics
import org.solidhax.apostle.Apostle

abstract class Module(val name: String, val description: String, val defaultConfig: String? = null) {
    private val widgetsInternal: MutableList<HudElement> = mutableListOf()

    protected inline val mc get() = Apostle.mc

    val widgets: List<HudElement> get() = widgetsInternal.toList()

    internal fun registerWidget(widget: HudElement) {
        widgetsInternal.add(widget)
    }

    @Suppress("FunctionName")
    fun HUD(
        x: Int = 10,
        y: Int = 10,
        scale: Float = 2f,
        block: GuiGraphics.(example: Boolean) -> Pair<Int, Int>
    ): HudElement = HudElement(x, y, scale, false, this, block).also { registerWidget(it) }

    open fun loadConfig(gson: Gson, json: Any?) {}
    open fun saveConfig(gson: Gson): Any? = null

    fun loadDefaultConfig(): String? {
        val res = defaultConfig ?: return null
        val stream = javaClass.classLoader.getResourceAsStream(res) ?: return null
        return stream.bufferedReader().use { it.readText() }
    }
}