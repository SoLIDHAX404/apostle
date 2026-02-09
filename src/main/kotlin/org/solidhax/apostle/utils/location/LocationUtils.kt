package org.solidhax.apostle.utils.location

import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket
import net.minecraft.network.protocol.game.ClientboundSetObjectivePacket
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket
import org.solidhax.apostle.Apostle.Companion.mc
import org.solidhax.apostle.event.PacketEvent
import org.solidhax.apostle.event.WorldEvent
import org.solidhax.apostle.event.impl.subscribe
import org.solidhax.apostle.utils.equalsOneOf
import org.solidhax.apostle.utils.startsWithOneOf
import kotlin.jvm.optionals.getOrNull

object LocationUtils {
    var isInSkyblock: Boolean = false
        private set

    var currentArea: Area = Area.Unknown
        private set

    var lobbyId: String? = null
        private set

    private val lobbyRegex = Regex("\\d\\d/\\d\\d/\\d\\d (\\w{0,6}) *")

    fun init() {
        subscribe<PacketEvent.Receive> { event ->
            when (val packet = event.packet) {
                is ClientboundPlayerInfoUpdatePacket -> {
                    if(!isCurrentArea(Area.Unknown) || packet.actions().none { it.equalsOneOf(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME)}) return@subscribe
                    val area = packet.entries()?.find { it?.displayName?.string?.startsWithOneOf("Area: ", "Dungeon: ") == true }?.displayName?.string ?: return@subscribe
                    currentArea = Area.entries.firstOrNull { area.contains(it.displayName, true) } ?: Area.Unknown
                }

                is ClientboundSetObjectivePacket -> {
                    if(!isInSkyblock) isInSkyblock = packet.objectiveName == "SBScoreboard"
                }

                is ClientboundSetPlayerTeamPacket -> {
                    if(!isCurrentArea(Area.Unknown)) return@subscribe
                    val text = packet.parameters?.getOrNull()?.let { it.playerPrefix?.string?.plus(it.playerSuffix?.string) } ?: return@subscribe
                    lobbyRegex.find(text)?.groupValues?.get(1)?.let { lobbyId = it }
                }
            }
        }

        subscribe<WorldEvent.Load> {
            currentArea = if(mc.isSingleplayer) Area.SinglePlayer else Area.Unknown
            isInSkyblock = false
            lobbyId = null
        }
    }

    fun isCurrentArea(vararg areas: Area): Boolean {
        return if(currentArea == Area.SinglePlayer) true
        else areas.any { currentArea == it }
    }
}