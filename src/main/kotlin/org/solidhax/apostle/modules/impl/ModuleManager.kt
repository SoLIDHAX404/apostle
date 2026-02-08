package org.solidhax.apostle.modules.impl

import com.google.gson.Gson
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry
import net.minecraft.client.DeltaTracker
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.resources.ResourceLocation
import net.minecraft.resources.ResourceLocation.fromNamespaceAndPath
import org.solidhax.apostle.Apostle
import org.solidhax.apostle.Apostle.Companion.mc
import org.solidhax.apostle.modules.render.HUD
import org.solidhax.apostle.modules.skyblock.DamageSplash
import org.solidhax.apostle.modules.skyblock.ProtectItem
import org.solidhax.apostle.modules.skyblock.RarityDisplay
import java.nio.file.Files
import java.nio.file.Path

object ModuleManager {
    private val HUD_LAYER: ResourceLocation = fromNamespaceAndPath(Apostle.MOD_ID, "hud")
    private val CONFIG_DIR: Path = Path.of("config/apostle")

    private val modulesById: LinkedHashMap<String, Module> = linkedMapOf()

    val modules: List<Module>
        get() = modulesById.values.toList()

    val widgets: List<Widget>
        get() = modules.flatMap { module -> module.widgets }

    val gson = Gson()

    fun init() {
        listOf(RarityDisplay, DamageSplash, HUD, ProtectItem).forEach { register(it) }

        loadAllConfigs(CONFIG_DIR)
        HudElementRegistry.addFirst(HUD_LAYER, ModuleManager::render)
    }

    fun shutdown() {
        saveAllConfigs(CONFIG_DIR)
    }

    fun register(vararg modules: Module) {
        modules.forEach { module ->
            require(module.name !in modulesById) {"A module with id ${module.name} already exists."}
            modulesById[module.name] = module
        }
    }

    fun loadAllConfigs(configDir: Path) {
        Files.createDirectories(configDir)

        modules.forEach { module ->
            val file = configDir.resolve("${module.defaultConfig}")
            val raw = if(Files.exists(file)) Files.readString(file) else module.loadDefaultConfig()

            val parsed = try { gson.fromJson(raw, Any::class.java) } catch (ex: Exception) { null }

            module.loadConfig(gson, parsed)
        }
    }

    fun saveAllConfigs(configDir: Path) {
        Files.createDirectories(configDir)

        for(module in modules) {
            val jsonObject = module.saveConfig(gson) ?: continue
            val text = gson.toJson(jsonObject)

            val file = configDir.resolve("${module.defaultConfig}")
            Files.writeString(file, text)
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