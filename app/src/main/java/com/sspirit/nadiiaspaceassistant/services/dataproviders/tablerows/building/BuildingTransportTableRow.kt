package com.sspirit.nadiiaspaceassistant.services.dataproviders.tablerows.building

import android.util.Log
import com.sspirit.nadiiaspaceassistant.models.missions.building.Building
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingRoom
import com.sspirit.nadiiaspaceassistant.models.missions.building.RealLifeLocation
import com.sspirit.nadiiaspaceassistant.models.missions.building.transport.BuildingElevator
import com.sspirit.nadiiaspaceassistant.models.missions.building.transport.BuildingShuttlePod
import com.sspirit.nadiiaspaceassistant.models.missions.building.transport.BuildingTeleport
import com.sspirit.nadiiaspaceassistant.models.missions.building.transport.BuildingTransport
import com.sspirit.nadiiaspaceassistant.services.dataproviders.logTag
import com.sspirit.nadiiaspaceassistant.utils.readString
import kotlin.jvm.internal.Ref.IntRef

data class BuildingTransportTableRow(
    val id: String,
    val type: String,
    val locationId: String,
    val realLocation: String,
) {
    companion object {
        fun parse(raw: Array<Any>): BuildingTransportTableRow {
            val iterator = IntRef()
            return BuildingTransportTableRow(
                id = raw.readString(iterator),
                type = raw.readString(iterator),
                locationId = raw.readString(iterator),
                realLocation = raw.readString(iterator)
            )
        }
    }
}

fun Array<BuildingTransportTableRow>.toBuildingTransports(building: Building) : Array<BuildingTransport> {
    val rooms = mutableMapOf<String, MutableList<BuildingRoom>>()
    for (row in this) {
        if (row.id !in rooms.keys)
            rooms[row.id] = mutableListOf()

        val realLocation = RealLifeLocation.byString(row.realLocation)
        val room = building.sectors
            .flatMap { it.locations }
            .firstOrNull { it.id == row.locationId }
            ?.rooms?.firstOrNull { it.realLocation == realLocation }

        if (room == null) {
            Log.e(logTag, "Cant find room for location ${row.locationId} and real locations $realLocation")
            continue
        }

        rooms[row.id]?.add(room)
    }

    var transports = rooms.map { entry ->
        val id = entry.key
        val type = first { it.id == id }.type
        val transportRooms = entry.value.toTypedArray()
        return@map setupBuildingTransport(id, type, transportRooms, building)
    }
    transports = transports.filterNotNull()

    return transports.toTypedArray()
}

private fun setupBuildingTransport(
    id: String,
    type: String,
    rooms: Array<BuildingRoom>,
    building: Building
) : BuildingTransport? {
    return when (type) {
        "Лифт" -> BuildingElevator(id, building, rooms)
        "Телепорт" -> BuildingTeleport(id, building, rooms.first(), rooms.last())
        "Монорельс" -> BuildingShuttlePod(id, building, rooms)
        else -> {
            Log.e(logTag, "Invalid transport type $type")
            return null
        }
    }
}