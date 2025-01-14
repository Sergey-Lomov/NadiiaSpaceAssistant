package com.sspirit.nadiiaspaceassistant.models.missions.building

import com.sspirit.nadiiaspaceassistant.models.items.LootGroup
import com.sspirit.nadiiaspaceassistant.models.missions.building.specloot.BuildingSpecialLootContainer
import com.sspirit.nadiiaspaceassistant.models.missions.building.transport.BuildingTransport

data class Building (
    var sectors: Array<BuildingSector> = arrayOf(),
    var transports: Array<BuildingTransport> = arrayOf(),
    var bigObjects: MutableList<BuildingBigObject> = mutableListOf(),
    var loot: MutableList<BuildingLootContainer> = mutableListOf(),
    var specLoot: MutableList<BuildingSpecialLootContainer> = mutableListOf(),
    var availableLoot: Array<LootGroup> = arrayOf(),
) {
    fun transport(id: String) : BuildingTransport? =
        transports.firstOrNull { it.id == id }

    fun sector(title: String) : BuildingSector? =
        sectors.firstOrNull { it.title == title }

    fun location(id: String) : BuildingLocation? =
        sectors
            .flatMap { it.locations }
            .firstOrNull { it.id == id }

    fun bigObject(id: String) : BuildingBigObject? =
        bigObjects.firstOrNull { it.id == id }

    fun room(locId: String, real: RealLifeLocation) : BuildingRoom? =
        location(locId)
            ?.rooms
            ?.firstOrNull { it.realLocation == real }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Building

        if (!sectors.contentEquals(other.sectors)) return false
        if (!transports.contentEquals(other.transports)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = sectors.contentHashCode()
        result = 31 * result + transports.contentHashCode()
        return result
    }
}