package com.solidhax.apostle.modules.internal

import com.google.gson.*
import com.solidhax.apostle.ui.setting.Savable
import java.io.File

class ModuleConfig internal constructor(file: File) {

    internal val modules: HashMap<String, Module> = hashMapOf()

    private val file: File = file.apply {
        parentFile.mkdirs()
        createNewFile()
    }

    fun load() {
        with(file.bufferedReader().use { it.readText() }) {
            if (isEmpty()) return

            val jsonArray = JsonParser.parseString(this).asJsonArray ?: return
            for (modules in jsonArray) {
                val moduleObj = modules?.asJsonObject ?: continue
                val module = this@ModuleConfig.modules[moduleObj.get("name").asString.lowercase()] ?: continue
                if (moduleObj.get("enabled").asBoolean != module.enabled) module.toggle()
                val settingObj = moduleObj.get("settings")?.takeIf { it.isJsonObject }?.asJsonObject?.entrySet() ?: continue
                for ((key, value) in settingObj) {
                    (module.settings[key] as? Savable)?.apply { read(value ?: continue, gson) }
                }
            }
        }
    }

    fun save() {
        val jsonArray = JsonArray().apply {
            for ((_, module) in modules) {
                add(JsonObject().apply {
                    add("name", JsonPrimitive(module.name))
                    add("enabled", JsonPrimitive(module.enabled))
                    add("settings", JsonObject().apply {
                        for ((name, setting) in module.settings) {
                            if (setting is Savable) add(name, setting.write(gson))
                        }
                    })
                })
            }
        }

        file.bufferedWriter().use { it.write(gson.toJson(jsonArray)) }
    }

    private companion object {
        private val gson: Gson = GsonBuilder().setPrettyPrinting().create()
    }

    override fun toString(): String {
        return "ModuleConfig(file=$file)"
    }
}