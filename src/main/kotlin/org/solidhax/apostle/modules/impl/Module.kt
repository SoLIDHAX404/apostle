package org.solidhax.apostle.modules.impl

import org.solidhax.apostle.Apostle

abstract class Module(val id: String, val displayName: String, val description: String) {
    private val widgetsInternal: MutableList<Widget> = mutableListOf()

    protected inline val mc get() = Apostle.mc

    val widgets: List<Widget> get() = widgetsInternal.toList()

    internal fun registerWidget(widget: Widget) {
        widgetsInternal.add(widget)
    }
}