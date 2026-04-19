package com.solidhax.apostle.utils

import com.solidhax.apostle.Apostle.mc
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.Vec3

inline val Entity.renderX: Double
    get() =
        xo + (x - xo) * mc.deltaTracker.getGameTimeDeltaPartialTick(true)

inline val Entity.renderY: Double
    get() =
        yo + (y - yo) * mc.deltaTracker.getGameTimeDeltaPartialTick(true)

inline val Entity.renderZ: Double
    get() =
        zo + (z - zo) * mc.deltaTracker.getGameTimeDeltaPartialTick(true)

inline val Entity.renderPos: Vec3
    get() = Vec3(renderX, renderY, renderZ)