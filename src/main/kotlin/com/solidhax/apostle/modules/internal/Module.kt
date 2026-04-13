package com.solidhax.apostle.modules.internal

import com.solidhax.apostle.Apostle.bus
import com.solidhax.apostle.ui.setting.Setting

abstract class Module(
    val name: String,
    var description: String,
    var category: Category,
    var toggled: Boolean = false
) {
    var enabled: Boolean = toggled
    private set
    var expanded: Boolean = false
        private set

    val settings: LinkedHashMap<String, Setting<*>> = linkedMapOf()

    init {
        bus.subscribe(this)
    }

    open fun onEnable() {
        bus.subscribe(this)
    }

    open fun onDisable() {
        bus.unsubscribe(this)
    }

    fun toggle() {
        enabled = !enabled
        if(enabled) onEnable() else onDisable()
    }

    fun toggleExpanded() {
        expanded = !expanded
    }

    fun <K : Setting<*>> registerSetting(setting: K): K {
        settings[setting.name] = setting
        return setting
    }

    operator fun <K : Setting<*>> K.unaryPlus(): K = registerSetting(this)
}
