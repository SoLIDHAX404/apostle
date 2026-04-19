package com.solidhax.apostle.modules.mining

import com.solidhax.apostle.Apostle.mc
import com.solidhax.apostle.api.Island
import com.solidhax.apostle.api.LocationAPI
import com.solidhax.apostle.events.RenderEvent
import com.solidhax.apostle.events.TickEvent
import com.solidhax.apostle.events.WorldEvent
import com.solidhax.apostle.modules.internal.Category
import com.solidhax.apostle.modules.internal.Module
import com.solidhax.apostle.ui.setting.ColorSetting
import com.solidhax.apostle.utils.drawWireFrameBox
import com.solidhax.apostle.utils.modMessage
import meteordevelopment.orbit.EventHandler
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.decoration.ArmorStand
import net.minecraft.world.phys.AABB
import java.awt.Color
import kotlin.collections.mutableSetOf

enum class CorpseType {
    LAPIS,
    TUNGSTEN,
    UMBER,
    VANGUARD
}
object Mineshaft : Module("Mineshaft", "Various features for mineshafts", Category.MINING) {
    private val corpses = mutableMapOf<CorpseType, MutableSet<Entity>>()

    private val lapisColor by ColorSetting("Lapis Color", "The color for lapis corpses", Color(0, 0, 255))
    private val tungstenColor by ColorSetting("Tungsten Color", "The color for tungsten corpses", Color(255, 255, 255))
    private val umberColor by ColorSetting("Umber Color", "The color for umber corpses", Color(181, 98, 34))
    private val vanguardColor by ColorSetting("Vanguard Color", "The color for vanguard corpses", Color(242, 36, 184))

    init {
        @EventHandler
        fun onTickEnd(event: TickEvent.End) {
            if(!enabled || !LocationAPI.isCurrentArea(Island.Mineshaft)) return

            mc.level?.entitiesForRendering()?.forEach { e ->
                val entity = e ?: return@forEach
                if(!entity.isAlive || entity !is ArmorStand) return@forEach

                val entityName = entity.name.string
                if(entityName != "Armor Stand" || entity.isInvisible) return@forEach

                val helmetName = entity.getItemBySlot(EquipmentSlot.HEAD).customName?.string
                val type = when(helmetName) {
                    "Lapis Armor Helmet" -> CorpseType.LAPIS
                    "Mineral Helmet" -> CorpseType.TUNGSTEN
                    "Yog Helmet" -> CorpseType.UMBER
                    "Vanguard Helmet" -> CorpseType.VANGUARD
                    else -> return@forEach
                }

                corpses.getOrPut(type) { mutableSetOf() }.add(entity)
            }
        }

        @EventHandler
        fun onRenderEventExtract(event: RenderEvent.Extract) {
            if(!enabled || !LocationAPI.isCurrentArea(Island.Mineshaft)) return

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
        }

        @EventHandler
        fun onWorldEventLoad(event: WorldEvent.Load) {
            corpses.clear()
        }
    }
}