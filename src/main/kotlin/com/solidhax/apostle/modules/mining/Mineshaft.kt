package com.solidhax.apostle.modules.mining

import com.solidhax.apostle.Apostle.mc
import com.solidhax.apostle.api.Island
import com.solidhax.apostle.api.LocationAPI
import com.solidhax.apostle.api.MiningAPI
import com.solidhax.apostle.api.MiningAPI.Pity
import com.solidhax.apostle.events.RenderEvent
import com.solidhax.apostle.events.TickEvent
import com.solidhax.apostle.events.WorldEvent
import com.solidhax.apostle.modules.internal.Category
import com.solidhax.apostle.modules.internal.Module
import com.solidhax.apostle.ui.setting.BooleanSetting
import com.solidhax.apostle.ui.setting.ColorSetting
import com.solidhax.apostle.ui.setting.HudSetting
import com.solidhax.apostle.utils.alert
import com.solidhax.apostle.utils.drawWireFrameBox
import com.solidhax.apostle.utils.toMinecraftColor
import meteordevelopment.orbit.EventHandler
import net.minecraft.client.player.LocalPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.decoration.ArmorStand
import net.minecraft.world.phys.AABB
import java.awt.Color

enum class CorpseType {
    LAPIS,
    TUNGSTEN,
    UMBER,
    VANGUARD
}
object Mineshaft : Module("Mineshaft", "Various features for mineshafts", Category.MINING) {
    private val corpses = mutableMapOf<CorpseType, MutableSet<Entity>>()
    private val littlefoots = mutableSetOf<Entity>()

    private var pity: Pity? = null
    private val examplePity: Pity = Pity("Glacite Mineshafts", 532, 2000)

    private val pityHud by HudSetting("Pity Hud", "Pity Hud", 10, 10) { preview ->
        pity = if(preview) examplePity else MiningAPI.pity ?: return@HudSetting 0 to 0

        val line = "${pity?.name}: §e${pity?.progress}§7/§a${pity?.needed}"
        drawString(mc.font, line, 0, 0, Color.WHITE.toMinecraftColor())

        mc.font.width(line) to mc.font.lineHeight
    }

    private val highlightCorpses by BooleanSetting("Highlight Corpses")
    private val highlightLittlefoot by BooleanSetting("Highlight Littlefoot's")
    private val lapisColor by ColorSetting("Lapis Color", "The color for lapis corpses", Color(0, 0, 255))
    private val tungstenColor by ColorSetting("Tungsten Color", "The color for tungsten corpses", Color(255, 255, 255))
    private val umberColor by ColorSetting("Umber Color", "The color for umber corpses", Color(181, 98, 34))
    private val vanguardColor by ColorSetting("Vanguard Color", "The color for vanguard corpses", Color(242, 36, 184))
    private val littlefootHighlightColor by ColorSetting("Littlefoot Color", "The color for littlefoots", Color(255, 0, 0))

    init {
        @EventHandler
        fun onTickEnd(event: TickEvent.End) {
            if(!enabled || !LocationAPI.isCurrentArea(Island.Mineshaft)) return
            if (!highlightCorpses && !highlightLittlefoot) return

            mc.level?.entitiesForRendering()?.forEach { e ->
                if(!e.isAlive || e !is ArmorStand) return@forEach

                val entityName = e.name.string
                if (entityName == "Armor Stand" && !e.isInvisible) {
                    val helmetName = e.getItemBySlot(EquipmentSlot.HEAD).customName?.string
                    val type = when (helmetName) {
                        "Lapis Armor Helmet" -> CorpseType.LAPIS
                        "Mineral Helmet" -> CorpseType.TUNGSTEN
                        "Yog Helmet" -> CorpseType.UMBER
                        "Vanguard Helmet" -> CorpseType.VANGUARD
                        else -> null
                    }

                    if (type != null) corpses.getOrPut(type) { mutableSetOf() }.add(e)
                }

                if (entityName != "Armor Stand" && e.isInvisible && entityName.contains("Littlefoot")) {
                    mc.level?.getEntities(e, e.boundingBox.inflate(0.5).move(0.0, -1.0, 0.0)) { isValidEntity(it) }?.firstOrNull()?.let {
                        if (littlefoots.add(it)) {
                            alert("§cLITTLEFOOT!", false)
                            return@forEach
                        }
                    }
                }

                littlefoots.removeIf { entity -> !entity.isAlive }
            }
        }

        @EventHandler
        fun onRenderEventExtract(event: RenderEvent.Extract) {
            if(!enabled || !LocationAPI.isCurrentArea(Island.Mineshaft)) return
            if (!highlightCorpses && !highlightLittlefoot) return

            corpses.forEach { (type, corpse) ->
                val color = when (type) {
                    CorpseType.LAPIS -> lapisColor
                    CorpseType.TUNGSTEN -> tungstenColor
                    CorpseType.UMBER -> umberColor
                    CorpseType.VANGUARD -> vanguardColor
                }

                corpse.forEach { entity ->
                    event.drawWireFrameBox(AABB(entity.blockPosition()), color)
                }
            }

            littlefoots.forEach { entity ->
                event.drawWireFrameBox(entity.boundingBox, littlefootHighlightColor)
            }
        }

        @EventHandler
        fun onWorldEventLoad(event: WorldEvent.Load) {
            corpses.clear()
            littlefoots.clear()
        }
    }

    private fun isValidEntity(entity: Entity): Boolean =
        when (entity) {
            is LocalPlayer -> false
            is ArmorStand -> false
            else -> !entity.isInvisible && entity.isAlive
        }
}