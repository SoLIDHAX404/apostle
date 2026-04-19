package com.solidhax.apostle.api

import com.solidhax.apostle.events.TabListWidgetEvent
import meteordevelopment.orbit.EventHandler

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

    var currentArea: Island = Island.Unknown
        private set

    init {
        @EventHandler
        fun onWidgetAdded(event: TabListWidgetEvent.Add) {
            if(event.widget != TabListAPI.TabWidget.AREA) return

            val area = event.newContent.header.substringAfterLast(": ")
            currentArea = Island.entries.firstOrNull { area.contains(it.displayName, true) } ?: Island.Unknown
        }
    }

    fun isCurrentArea(vararg areas: Island): Boolean =
        if (currentArea == Island.SinglePlayer) true
        else areas.any { currentArea == it }
}