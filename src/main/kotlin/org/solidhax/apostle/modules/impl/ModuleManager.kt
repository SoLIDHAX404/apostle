package org.solidhax.apostle.modules.impl

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
import net.minecraft.client.DeltaTracker
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.resources.ResourceLocation
import net.minecraft.resources.ResourceLocation.fromNamespaceAndPath
import org.solidhax.apostle.Apostle
import org.solidhax.apostle.Apostle.Companion.mc
import org.solidhax.apostle.gui.widget.Widget

object ModuleManager {
    private val HUD_LAYER: ResourceLocation = fromNamespaceAndPath(Apostle.MOD_ID, "hud")
    private val modulesById: LinkedHashMap<String, Module> = linkedMapOf()

    val modules: List<Module>
        get() = modulesById.values.toList()

    val widgets: List<Widget>
        get() = modules.flatMap { module -> module.widgets }

    fun init() {
//        listOf().forEach { register(it) }

        HudElementRegistry.addFirst(HUD_LAYER, ModuleManager::render)
    }

    fun register(vararg modules: Module) {
        modules.forEach { module ->
            require(module.id !in modulesById) {"A module with id ${module.id} already exists."}
            modulesById[module.id] = module
        }
    }

    fun render(guiGraphics: GuiGraphics, deltaTracker: DeltaTracker) {
        if(mc.screen != null) return

        guiGraphics.pose().pushMatrix()

        val guiScale = Minecraft.getInstance().window.guiScale
        guiGraphics.pose().scale(1f / guiScale, 1f / guiScale)

        for(widget in widgets) { widget.render(guiGraphics) }

        guiGraphics.pose().popMatrix()
    }

    operator fun get(id: String): Module? = modulesById[id]
}