package org.solidhax.apostle.modules.impl

object ModuleManager {
    private val modulesById: LinkedHashMap<String, Module> = linkedMapOf()

    val modules: List<Module>
    get() = modulesById.values.toList()

    fun register(vararg modules: Module) {
        modules.forEach { module ->
            require(module.id !in modulesById) {"A module with id ${module.id} already exists."}
            modulesById[module.id] = module
        }
    }

    operator fun get(id: String): Module? = modulesById[id]
}