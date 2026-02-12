package org.solidhax.apostle.modules.skyblock

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.world.entity.decoration.ArmorStand
import org.solidhax.apostle.event.EntityEvent
import org.solidhax.apostle.event.impl.on
import org.solidhax.apostle.modules.impl.Module
import org.solidhax.apostle.utils.location.LocationUtils

object DamageSplash : Module() {

    var enabled = false

    private val damageRegex = "^([✧✯]?)(\\d{1,3}(?:,\\d{3})*)([⚔+✧❤♞☄✷ﬗ✯]*)$".toRegex()

    private val colorsHypixel: Array<ChatFormatting> = arrayOf(
        ChatFormatting.WHITE,
        ChatFormatting.YELLOW,
        ChatFormatting.GOLD,
        ChatFormatting.RED,
        ChatFormatting.RED,
        ChatFormatting.WHITE
    )

    init {
        on<EntityEvent.Metadata> {
            if(!enabled) return@on
            if(!LocationUtils.isInSkyblock) return@on

            val stand = entity as? ArmorStand ?: return@on

            val name = stand.customName ?: return@on
            val nameString = name.string

            val match = damageRegex.matchEntire(nameString) ?: return@on

            val prefix = match.groupValues[1]
            val numberPart = match.groupValues[2]
            val suffix = match.groupValues[3]

            val isCritical = prefix.contains("✧") || prefix.contains("✯")
            val shortened = shortenDamageNumber(numberPart)
            val finalText = prefix + shortened + suffix

            stand.customName = buildShortenedDamageTag(finalText, isCritical)
        }
    }

    private fun buildShortenedDamageTag(text: String, isCritical: Boolean): Component {
        val root: MutableComponent = Component.empty()

        if(isCritical) {
            text.forEachIndexed { index, ch ->
                val color = colorsHypixel[index % colorsHypixel.size]
                root.append(Component.literal(ch.toString()).withStyle(color))
            }
        } else {
            root.append(Component.literal(text)).withStyle(ChatFormatting.GRAY)
        }

        return root
    }

    private fun shortenDamageNumber(raw: String): String {
        val clean = raw.replace(",", "")
        val value = clean.toLongOrNull() ?: return raw

        return when {
            value >= 1_000_000_000 -> format(value, 1_000_000_000, "b")
            value >= 1_000_000     -> format(value, 1_000_000, "m")
            value >= 1_000         -> format(value, 1_000, "k")
            else -> value.toString()
        }
    }

    private fun format(value: Long, divisor: Long, suffix: String): String {
        val scaled = value.toDouble() / divisor.toDouble()
        val out = if (scaled % 1.0 == 0.0) {
            scaled.toLong().toString()
        } else {
            String.format("%.1f", scaled)
        }
        return out + suffix
    }
}
