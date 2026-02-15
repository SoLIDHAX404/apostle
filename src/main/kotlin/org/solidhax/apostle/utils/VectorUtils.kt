package org.solidhax.apostle.utils

import net.minecraft.world.phys.Vec3

operator fun Vec3.unaryMinus(): Vec3 = Vec3(-x, -y, -z)