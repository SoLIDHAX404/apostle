package org.solidhax.apostle.modules.farming

import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.decoration.ArmorStand
import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.AABB
import org.solidhax.apostle.event.RenderEvent
import org.solidhax.apostle.event.TickEvent
import org.solidhax.apostle.event.WorldEvent
import org.solidhax.apostle.event.impl.on
import org.solidhax.apostle.modules.impl.Module
import org.solidhax.apostle.utils.chat.modMessage
import org.solidhax.apostle.utils.location.Area
import org.solidhax.apostle.utils.location.LocationUtils
import org.solidhax.apostle.utils.render.Colors
import org.solidhax.apostle.utils.render.drawTracer
import org.solidhax.apostle.utils.render.drawWireFrameBox

object Trapper : Module() {

    var enabled = false
    var depthTest = true
    var showBoundingBox = false
    var showTracer = false
    var showTitle = false
    var playSound = false

    private val trapperMobs = hashSetOf("Trackable", "Untrackable", "Undetected", "Endangered", "Elusive")
    private val entities = mutableSetOf<Entity>()

    init {
        on<TickEvent.End> {
            if(!enabled || !LocationUtils.isInSkyblock || LocationUtils.currentArea != Area.FarmingIsland) return@on

            mc.level?.entitiesForRendering()?.forEach { e ->
                val entity = e ?: return@forEach
                if(!entity.isAlive || entity !is ArmorStand) return@forEach

                val entityName = entity.name.string
                if(entityName == "Armor Stand" || !entity.isInvisible) return@forEach
                if(!trapperMobs.any{ it in entityName }) return@forEach

                mc.level?.getEntities(entity, entity.boundingBox.inflate(0.5).move(0.0, -1.0, 0.0)) { isValidEntity(it) && (!depthTest || mc.player?.hasLineOfSight(it) == true) }?.firstOrNull()?.let {
                    if(entities.add(it)) {
                        if(showTitle) {
                            mc.gui.setTimes(0, 20, 5)
                            mc.gui.setTitle(Component.literal("Found Trapper Animal!").withColor(Colors.RED.rgba))
                        }

                        if(playSound) mc.player?.playSound(SoundEvents.NOTE_BLOCK_CHIME.value(), 1.0f, 1.0f)
                    }
                }
            }

            entities.removeIf { entity -> !entity.isAlive }
        }

        on<RenderEvent.Extract> {
            entities.forEach { entity ->
                if(!entity.isAlive) return@forEach

                if(showBoundingBox) drawWireFrameBox(entity.boundingBox, Colors.RED)
                if(showTracer) drawTracer(entity.boundingBox.center, Colors.RED, false)
            }
        }

        on<WorldEvent.Unload> {
            entities.clear()
        }
    }

    private fun isValidEntity(entity: Entity): Boolean =
        when (entity) {
            is ArmorStand -> false
            is Player -> false
            else -> !entity.isInvisible && entity.isAlive
        }
}