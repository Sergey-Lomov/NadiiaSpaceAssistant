package com.sspirit.nadiiaspaceassistant.models.missions.building

import com.sspirit.nadiiaspaceassistant.models.items.LootGroup
import com.sspirit.nadiiaspaceassistant.models.missions.building.transport.BuildingTransport

data class Building (
    val sectors: Array<BuildingSector>,
    val transports: Array<BuildingTransport>,
    val availableLoot: Array<LootGroup>
) {
    fun location(id: String) : BuildingLocation? =
        sectors
            .flatMap { it.locations }
            .firstOrNull { it.id == id }

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