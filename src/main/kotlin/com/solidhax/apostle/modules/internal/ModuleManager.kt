package com.solidhax.apostle.modules.internal

import com.solidhax.apostle.Apostle
import com.solidhax.apostle.Apostle.mc
import com.solidhax.apostle.modules.dev.Dev
import com.solidhax.apostle.modules.mining.Commissions
import com.solidhax.apostle.ui.ConfigScreen
import com.solidhax.apostle.ui.HudManager
import com.solidhax.apostle.ui.setting.HudSetting
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.resources.Identifier
import net.minecraft.resources.Identifier.fromNamespaceAndPath
import java.io.File
import java.util.*

object ModuleManager {
    val modules: MutableList<Module> = mutableListOf()
    val modulesByCategory: EnumMap<Category, ArrayList<Module>> = EnumMap(Category::class.java)
    val configs: ArrayList<ModuleConfig> = arrayListOf()

    private val HUD_LAYER: Identifier = fromNamespaceAndPath(Apostle.MOD_ID, "hud")
    val hudSettings: List<HudSetting>
        get() = modules.asSequence()
            .flatMap { module -> module.settings.values.asSequence() }
            .mapNotNull { it as? HudSetting }
            .toList()

    init {
        registerModules(ModuleConfig(file = File(Apostle.configFile, "config.json")),
            Dev, Commissions
        )

        HudElementRegistry.attachElementBefore(VanillaHudElements.SLEEP, HUD_LAYER, ModuleManager::render)
    }

    fun registerModules(config: ModuleConfig, vararg modules: Module) {
        for (module in modules) {
            if(module.category == Category.DEV && !FabricLoader.getInstance().isDevelopmentEnvironment) continue

            val lowercase = module.name.lowercase()
            config.modules[lowercase] = module

            this.modules.add(module)
            this.modulesByCategory.getOrPut(module.category) { arrayListOf() }.add(module)
        }

        configs.add(config)
        config.load()
    }

    fun loadConfigurations() {
        for (config in configs) {
            config.load()
        }
    }

    fun saveConfigurations() {
        for (config in configs) {
            config.save()
        }
    }

    fun render(gui: GuiGraphics, tickDelta: DeltaTracker) {
        if (mc.screen === HudManager || mc.screen == ConfigScreen) return

        hudSettings.asSequence()
            .filter { it.isVisible && it.isEnabled }
            .forEach { hudSetting ->
                hudSetting.value.draw(gui, preview = false)
            }
    }
}
