package org.solidhax.apostle.modules.mining

import net.minecraft.core.BlockPos
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.decoration.ArmorStand
import net.minecraft.world.phys.AABB
import org.solidhax.apostle.event.ChatPacketEvent
import org.solidhax.apostle.event.RenderEvent
import org.solidhax.apostle.event.TickEvent
import org.solidhax.apostle.event.WorldEvent
import org.solidhax.apostle.event.impl.on
import org.solidhax.apostle.modules.impl.Module
import org.solidhax.apostle.utils.location.Area
import org.solidhax.apostle.utils.location.LocationUtils
import org.solidhax.apostle.utils.render.Color
import org.solidhax.apostle.utils.render.drawOutlineBox

enum class CorpseType {
    LAPIS,
    TUNGSTEN,
    UMBER,
    VANGUARD
}

object CorpseFinder : Module() {

    var enabled = false

    private val corpses = mutableMapOf<CorpseType, MutableSet<Entity>>()

    init {
        on<TickEvent.End> {
            if(!enabled || LocationUtils.currentArea != Area.Mineshaft) return@on

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

        on<RenderEvent.Extract> {
            if(!enabled || LocationUtils.currentArea != Area.Mineshaft) return@on

            corpses.forEach { (type, entities) ->

                val color = when (type) {
                    CorpseType.LAPIS -> Color(0, 0, 255)
                    CorpseType.TUNGSTEN -> Color(255, 255, 255)
                    CorpseType.UMBER -> Color(181, 98, 34)
                    CorpseType.VANGUARD -> Color(242, 36, 184)
                }

                entities.forEach { entity ->
                    val bp = entity.blockPosition()
                    val box = AABB(
                        bp.x.toDouble(), bp.y.toDouble(), bp.z.toDouble(),
                        (bp.x + 1).toDouble(), (bp.y + 1).toDouble(), (bp.z + 1).toDouble()
                    )

                    drawOutlineBox(box, color)
                }
            }
        }

        on<WorldEvent.Load> {
            corpses.clear()
        }
    }
}