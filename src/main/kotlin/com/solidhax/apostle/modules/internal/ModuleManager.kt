package com.solidhax.apostle.modules.internal

import com.solidhax.apostle.modules.dev.Dev
import com.solidhax.apostle.modules.dev.Test
import net.fabricmc.loader.api.FabricLoader
import tech.thatgravyboat.skyblockapi.utils.extentions.enumMapOf
import java.util.EnumMap

object ModuleManager {
    val modules: MutableList<Module> = mutableListOf()
    val modulesByCategory: EnumMap<Category, ArrayList<Module>> = enumMapOf()

    init {
        registerModules(Dev, Test)
    }

    fun registerModules(vararg modules: Module) {
        for (module in modules) {
            if(module.category == Category.DEV && !FabricLoader.getInstance().isDevelopmentEnvironment) continue

            this.modules.add(module)
            this.modulesByCategory.getOrPut(module.category) { arrayListOf() }.add(module)
        }
    }
}