package com.solidhax.apostle.api

import com.solidhax.apostle.Apostle.mc
import com.solidhax.apostle.events.PacketEvent
import com.solidhax.apostle.events.WorldEvent
import com.solidhax.apostle.utils.equalsOneOf
import com.solidhax.apostle.utils.noControlCodes
import com.solidhax.apostle.utils.startsWithOneOf
import meteordevelopment.orbit.EventHandler
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket
import net.minecraft.network.protocol.game.ClientboundSetObjectivePacket
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket
import kotlin.jvm.optionals.getOrNull

enum class Island(val displayName: String) {
    SinglePlayer("Singleplayer"),
    PrivateIsland("Private Island"),
    Garden("Garden"),
    SpiderDen("Spider's Den"),
    CrimsonIsle("Crimson Isle"),
    TheEnd("The End"),
    GoldMine("Gold Mine"),
    DeepCaverns("Deep Caverns"),
    DwarvenMines("Dwarven Mines"),
    CrystalHollows("Crystal Hollows"),
    FarmingIsland("The Farming Islands"),
    ThePark("The Park"),
    Dungeon("Catacombs"),
    DungeonHub("Dungeon Hub"),
    Hub("Hub"),
    DarkAuction("Dark Auction"),
    JerryWorkshop("Jerry's Workshop"),
    Kuudra("Kuudra"),
    Mineshaft("Mineshaft"),
    Rift("The Rift"),
    BackwaterBayou("Backwater Bayou"),
    Galatea("Galatea"),
    Unknown("(Unknown)");
}

object LocationAPI {

    var isInSkyblock: Boolean = false
        private set

    var currentArea: Island = Island.Unknown
        private set

    var lobbyId: String? = null
        private set

    val miningIslands: Array<Island> = arrayOf(Island.DwarvenMines, Island.CrystalHollows, Island.Mineshaft)

    private val lobbyRegex = Regex("\\d\\d/\\d\\d/\\d\\d (\\w{0,6}) *")

    init {
        @EventHandler
        fun onReceivePacket(event: PacketEvent.Receive) {
            when(event.packet) {
                is ClientboundPlayerInfoUpdatePacket -> parseArea(event.packet)
                is ClientboundSetObjectivePacket -> parseObjectiveName(event.packet)
                is ClientboundSetPlayerTeamPacket -> parseLobby(event.packet)
                else -> return
            }
        }

        @EventHandler
        fun onWorldLoad(event: WorldEvent.Load) {
            currentArea = if (mc.isSingleplayer) Island.SinglePlayer else Island.Unknown
            isInSkyblock = false
            lobbyId = null
        }
    }

    private fun parseArea(packet: ClientboundPlayerInfoUpdatePacket) {
        if (!isCurrentArea(Island.Unknown) || packet.actions().none { it.equalsOneOf(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME) }) return
        val area = packet.entries().find { it.displayName?.string?.startsWithOneOf("Area: ", "Dungeon: ") == true }?.displayName?.string ?: return
        currentArea = Island.entries.firstOrNull { area.contains(it.displayName, true) } ?: Island.Unknown
    }

    private fun parseObjectiveName(packet: ClientboundSetObjectivePacket) {
        if (!isInSkyblock) isInSkyblock = packet.objectiveName == "SBScoreboard"
    }

    private fun parseLobby(packet: ClientboundSetPlayerTeamPacket) {
        if (!isCurrentArea(Island.Unknown)) return
        val text = packet.parameters.getOrNull()?.let { it.playerPrefix.string.plus(it.playerSuffix.string).noControlCodes } ?: return

        lobbyRegex.find(text)?.groupValues?.get(1)?.let { lobbyId = it }
    }

    fun isCurrentArea(vararg areas: Island): Boolean =
        if (currentArea == Island.SinglePlayer) true
        else areas.any { currentArea == it }
}