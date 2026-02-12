package org.solidhax.apostle.utils.location

import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket
import net.minecraft.network.protocol.game.ClientboundSetObjectivePacket
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket
import org.solidhax.apostle.Apostle.Companion.mc
import org.solidhax.apostle.event.PacketEvent
import org.solidhax.apostle.event.WorldEvent
import org.solidhax.apostle.event.impl.on
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

    init {
        on<PacketEvent.Receive> {
            when (val packet = packet) {
                is ClientboundPlayerInfoUpdatePacket -> {
                    if(!isCurrentArea(Area.Unknown) || packet.actions().none { it.equalsOneOf(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME)}) return@on
                    val area = packet.entries()?.find { it?.displayName?.string?.startsWithOneOf("Area: ", "Dungeon: ") == true }?.displayName?.string ?: return@on
                    currentArea = Area.entries.firstOrNull { area.contains(it.displayName, true) } ?: Area.Unknown
                }

                is ClientboundSetObjectivePacket -> {
                    if(!isInSkyblock) isInSkyblock = packet.objectiveName == "SBScoreboard"
                }

                is ClientboundSetPlayerTeamPacket -> {
                    if(!isCurrentArea(Area.Unknown)) return@on
                    val text = packet.parameters?.getOrNull()?.let { it.playerPrefix?.string?.plus(it.playerSuffix?.string) } ?: return@on
                    lobbyRegex.find(text)?.groupValues?.get(1)?.let { lobbyId = it }
                }
            }
        }

        on<WorldEvent.Load> {
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