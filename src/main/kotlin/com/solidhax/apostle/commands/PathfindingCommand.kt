package com.solidhax.apostle.commands

import com.github.stivais.commodore.Commodore
import com.solidhax.apostle.modules.dev.PathfindingDebug
import net.minecraft.core.BlockPos

val pathfindingCommand = Commodore("pathfind", "pathfind") {
    literal("to").runs { x: Double, y: Double, z: Double ->
        PathfindingDebug.setTarget(BlockPos(x.toInt(), y.toInt(), z.toInt()))
    }
}