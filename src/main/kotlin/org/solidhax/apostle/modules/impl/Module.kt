package org.solidhax.apostle.modules.impl

import com.google.gson.Gson
import org.solidhax.apostle.Apostle

abstract class Module(val name: String, val description: String, val defaultConfig: String? = null) {
    private val widgetsInternal: MutableList<Widget> = mutableListOf()

    protected inline val mc get() = Apostle.mc

    val widgets: List<Widget> get() = widgetsInternal.toList()

    internal fun registerWidget(widget: Widget) {
        widgetsInternal.add(widget)
    }

    open fun loadConfig(gson: Gson, json: Any?) {}
    open fun saveConfig(gson: Gson): Any? = null

    fun loadDefaultConfig(): String? {
        val res = defaultConfig ?: return null
        val stream = javaClass.classLoader.getResourceAsStream(res) ?: return null
        return stream.bufferedReader().use { it.readText() }
    }
}