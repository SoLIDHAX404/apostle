package org.solidhax.apostle.modules.skyblock

import net.minecraft.network.chat.Component
import net.minecraft.world.entity.decoration.ArmorStand
import org.solidhax.apostle.event.EntityEvent
import org.solidhax.apostle.event.impl.on
import org.solidhax.apostle.modules.impl.Module

object MobNametagHider : Module() {

    var enabled = false

    val mobNameTagRegex = Regex("""(\d[\d,]*)/(\d[\d,]*)❤""")

    init {
        on<EntityEvent.Metadata> {
            if(!enabled) return@on

            val stand = entity as? ArmorStand ?: return@on

            val name = stand.customName?.string ?: return@on
            val match = mobNameTagRegex.find(name) ?: return@on

            val hp = match.groupValues[1].replace(",", "").toInt()
            val maxHp = match.groupValues[2].replace(",", "").toInt()

            stand.isCustomNameVisible = hp != maxHp
        }
    }
}