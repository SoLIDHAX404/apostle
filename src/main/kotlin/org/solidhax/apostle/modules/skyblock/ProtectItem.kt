package org.solidhax.apostle.modules.skyblock

import com.google.gson.Gson
import org.solidhax.apostle.modules.impl.Module

object ProtectItem : Module(name = "Protect Item", description = "Protects items based on uuid.", defaultConfig = "protectitem.json") {
    val protectedItems = ArrayList<String>()

    override fun loadConfig(gson: Gson, json: Any?) {
        protectedItems.clear()

        if(json is List<*>) {
            json.filterIsInstance<String>().forEach { protectedItems.add(it) }
        }
    }

    override fun saveConfig(gson: Gson): Any {
        return protectedItems
    }
}