package org.solidhax.apostle.modules.impl

import com.google.gson.Gson
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements
import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphics
import org.solidhax.apostle.Apostle.Companion.CONFIG_DIR
import org.solidhax.apostle.Apostle.Companion.HUD_LAYER
import org.solidhax.apostle.Apostle.Companion.gson
import org.solidhax.apostle.Apostle.Companion.mc
import org.solidhax.apostle.event.ClientEvent
import org.solidhax.apostle.event.impl.on
import org.solidhax.apostle.modules.mining.Commissions
import org.solidhax.apostle.modules.mining.CorpseFinder
import org.solidhax.apostle.modules.render.HUD
import org.solidhax.apostle.modules.skyblock.DamageSplash
import org.solidhax.apostle.modules.skyblock.ProtectItem
import org.solidhax.apostle.modules.skyblock.RarityDisplay
import java.nio.file.Files

object ModuleManager {

    val modules: MutableList<Module> = mutableListOf()
    val widgets: List<HudElement> get() = modules.flatMap { module -> module.widgets }

    init {
        on<ClientEvent.Start> {
            listOf(RarityDisplay, DamageSplash, HUD, ProtectItem, Commissions, CorpseFinder).forEach { modules.add(it) }

            loadAllConfigs()
            loadHudPositions()

            HudElementRegistry.attachElementBefore(VanillaHudElements.SLEEP, HUD_LAYER, ModuleManager::render)
        }

        on<ClientEvent.Stop> {
            saveAllConfigs()
            saveHudPositions()
        }
    }

    fun loadAllConfigs() {
        Files.createDirectories(CONFIG_DIR)

        modules.forEach { module ->
            val file = CONFIG_DIR.resolve("${module.defaultConfig}")
            val raw = if(Files.exists(file)) Files.readString(file) else module.loadDefaultConfig()

            val parsed = try { gson.fromJson(raw, Any::class.java) } catch (ex: Exception) { null }

            module.loadConfig(gson, parsed)
        }
    }

    fun loadHudPositions() {
        Files.createDirectories(CONFIG_DIR)

        val file = CONFIG_DIR.resolve("hud-positions.json")
        val raw = if (Files.exists(file)) Files.readString(file) else return

        val parsed = try {
            gson.fromJson(raw, Any::class.java)
        } catch (ex: Exception) {
            null
        }

        val map = parsed as? Map<*, *> ?: return

        widgets.forEach { widget ->
            val widgetJson = map[widget.name]
            widget.loadConfig(widgetJson)
        }
    }

    fun saveAllConfigs() {
        Files.createDirectories(CONFIG_DIR)

        for(module in modules) {
            val jsonObject = module.saveConfig(gson) ?: continue
            val text = gson.toJson(jsonObject)

            val file = CONFIG_DIR.resolve("${module.defaultConfig}")
            Files.writeString(file, text)
        }
    }

    fun saveHudPositions() {
        Files.createDirectories(CONFIG_DIR)

        val file = CONFIG_DIR.resolve("hud-positions.json")

        val map = widgets.associate { widget ->
            widget.name to widget.saveConfig()
        }

        Files.writeString(file, gson.toJson(map))
    }

    fun render(guiGraphics: GuiGraphics, deltaTracker: DeltaTracker) {
        if(mc.screen != null) return

        guiGraphics.pose().pushMatrix()
        val sf = mc.window.guiScale
        guiGraphics.pose().scale(1f / sf, 1f / sf)

        for(widget in widgets) { widget.draw(guiGraphics, false) }

        guiGraphics.pose().popMatrix()
    }
}