package com.solidhax.apostle.modules.internal

import com.solidhax.apostle.Apostle
import com.solidhax.apostle.Apostle.mc
import com.solidhax.apostle.modules.dev.Dev
import com.solidhax.apostle.ui.HudManager
import com.solidhax.apostle.ui.setting.HudSetting
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.resources.Identifier
import net.minecraft.resources.Identifier.fromNamespaceAndPath
import tech.thatgravyboat.skyblockapi.utils.extentions.enumMapOf
import java.util.*

object ModuleManager {
    val modules: MutableList<Module> = mutableListOf()
    val modulesByCategory: EnumMap<Category, ArrayList<Module>> = enumMapOf()
    private val HUD_LAYER: Identifier = fromNamespaceAndPath(Apostle.MOD_ID, "hud")
    val hudSettings: List<HudSetting>
        get() = modules.asSequence()
            .flatMap { module -> module.settings.values.asSequence() }
            .mapNotNull { it as? HudSetting }
            .toList()

    init {
        registerModules(Dev)

        HudElementRegistry.attachElementBefore(VanillaHudElements.SLEEP, HUD_LAYER, ModuleManager::render)
    }

    fun registerModules(vararg modules: Module) {
        for (module in modules) {
            if(module.category == Category.DEV && !FabricLoader.getInstance().isDevelopmentEnvironment) continue

            this.modules.add(module)
            this.modulesByCategory.getOrPut(module.category) { arrayListOf() }.add(module)
        }
    }

    fun render(gui: GuiGraphics, tickDelta: DeltaTracker) {
        if (mc.screen === HudManager) {
            return
        }

        hudSettings.asSequence()
            .filter { it.isVisible && it.isEnabled }
            .forEach { hudSetting ->
                hudSetting.value.draw(gui, preview = false)
            }
    }
}
