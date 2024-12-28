package com.sspirit.nadiiaspaceassistant.services.dataproviders.tablerows.location

import com.sspirit.nadiiaspaceassistant.models.missions.building.Building
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingLocation
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingLocationType
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingSector
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingSlab
import com.sspirit.nadiiaspaceassistant.utils.readInt
import com.sspirit.nadiiaspaceassistant.utils.readString
import com.sspirit.nadiiaspaceassistant.models.missions.building.RealLifeLocation
import com.sspirit.nadiiaspaceassistant.utils.write
import kotlin.jvm.internal.Ref.IntRef

private val roomsLocations = mapOf(
    0 to RealLifeLocation.HALL,
    1 to RealLifeLocation.KITCHEN,
    2 to RealLifeLocation.PLAYROOM,
    3 to RealLifeLocation.BEDROOM,
    4 to RealLifeLocation.BATHROOM,
    5 to RealLifeLocation.TOILET,
)
private val backRoomsLocations = roomsLocations.entries.associate { it.value to it.key }

private val passagesLocations = mapOf(
    0 to Pair(RealLifeLocation.HALL, RealLifeLocation.KITCHEN),
    1 to Pair(RealLifeLocation.HALL, RealLifeLocation.PLAYROOM),
    2 to Pair(RealLifeLocation.HALL, RealLifeLocation.BEDROOM),
    3 to Pair(RealLifeLocation.HALL, RealLifeLocation.BATHROOM),
    4 to Pair(RealLifeLocation.HALL, RealLifeLocation.TOILET),
)
private val backPassagesLocations = passagesLocations.entries.associate { it.value to it.key }

private val wallsLocations = mapOf(
    0 to Pair(RealLifeLocation.KITCHEN, RealLifeLocation.BEDROOM),
    1 to Pair(RealLifeLocation.KITCHEN, RealLifeLocation.PLAYROOM),
    2 to Pair(RealLifeLocation.KITCHEN, RealLifeLocation.HALL),
    3 to Pair(RealLifeLocation.PLAYROOM, RealLifeLocation.HALL),
    4 to Pair(RealLifeLocation.PLAYROOM, RealLifeLocation.BATHROOM),
    5 to Pair(RealLifeLocation.BATHROOM, RealLifeLocation.TOILET),
)
private val backWallsLocations = wallsLocations.entries.associate { it.value to it.key }

private val floorsLocations = mapOf(
    0 to RealLifeLocation.HALL,
    1 to RealLifeLocation.KITCHEN,
    2 to RealLifeLocation.PLAYROOM,
    3 to RealLifeLocation.BEDROOM,
    4 to RealLifeLocation.BATHROOM,
    5 to RealLifeLocation.TOILET,
)
private val backFloorsLocations = floorsLocations.entries.associate { it.value to it.key }

typealias RealLifeLocations = Pair<RealLifeLocation, RealLifeLocation>

data class LocationTableRow(
    val id: String,
    val sector: String,
    val level: Int,
    val type: String,
    val title: String,
    val rooms: Map<RealLifeLocation, LocationTableRowRoom>,
    val passages: Map<RealLifeLocations, LocationTableRowPassage>,
    val walls: Map<RealLifeLocations, LocationTableRowWall>,
    val floors: Map<RealLifeLocation, LocationTableRowFloor>,
) {
    companion object {
        fun parse(raw: Array<Any>): LocationTableRow {
            val displacement = IntRef()
            return LocationTableRow(
                id = raw.readString(displacement),
                sector = raw.readString(displacement),
                level = raw.readInt(displacement),
                type = raw.readString(displacement),
                title = raw.readString(displacement),

                rooms = roomsLocations.keys
                    .sorted()
                    .associate { roomsLocations[it]!! to LocationTableRowRoom.parse(raw, displacement) },

                passages = passagesLocations.keys
                    .sorted()
                    .associate { passagesLocations[it]!! to LocationTableRowPassage.parse(raw, displacement) },

                walls = wallsLocations.keys
                    .sorted()
                    .associate { wallsLocations[it]!! to LocationTableRowWall.parse(raw, displacement) },

                floors = floorsLocations.keys
                    .sorted()
                    .associate { floorsLocations[it]!! to LocationTableRowFloor.parse(raw, displacement) }
            )
        }

        fun from(source: BuildingLocation) : LocationTableRow {
            val rooms = source.rooms
                .associate { it.realLocation to LocationTableRowRoom.from(it) }

            val passages = source.passages
                .associate {
                    val locations = RealLifeLocations(it.room1.realLocation, it.room2.realLocation)
                    locations to LocationTableRowPassage.from(it)
                }

            val walls = source.walls
                .associate {
                    val locations = RealLifeLocations(it.room1.realLocation, it.room2.realLocation)
                    locations to LocationTableRowWall.from(it)
                }

            val floors = source.floor
                .associate { it.realLocation to LocationTableRowFloor.from(it) }

            return LocationTableRow(
                id = source.id,
                sector = source.sector.title,
                level = source.level,
                type = source.type.string,
                title = source.title,
                rooms = rooms,
                passages = passages,
                walls = walls,
                floors = floors
            )
        }
    }

    fun toBuildingLocation(sector: BuildingSector) : BuildingLocation {
        val location = BuildingLocation(
            id = id,
            type = BuildingLocationType.byString(type),
            sector = sector,
            level = level,
            title = title,
        )

        location.rooms = rooms
            .map { it.value.toBuildingRoom(location, it.key) }
            .toTypedArray()

        val realToRoom = location.rooms.associateBy { it.realLocation }
        location.walls = walls
            .map {
                val r1 = realToRoom[it.key.first] ?: return@map null
                val r2 = realToRoom[it.key.second] ?: return@map null
                it.value.toBuildingWall(r1, r2)
            }
            .filterNotNull()
            .toTypedArray()

        location.passages = passages
            .map {
                val r1 = realToRoom[it.key.first] ?: return@map null
                val r2 = realToRoom[it.key.second] ?: return@map null
                it.value.toBuildingPassage(r1, r2)
            }
            .filterNotNull()
            .toTypedArray()

        return location
    }
}

fun Array<LocationTableRow>.toBuildingSectors(building: Building) : Array<BuildingSector> {
    val sectors = mutableMapOf<String, BuildingSector>()
    for (row in this) {
        val sectorId = row.sector
        if (sectorId !in sectors.keys) sectors[sectorId] = BuildingSector(sectorId, building)
        val sector = sectors[sectorId]!!
        if (sector.slabs.isEmpty()) sector.slabs[0.5f] = outerSlabs(sector)

        val location = row.toBuildingLocation(sector)
        sector.locations.add(location)

        val slabs = row.floors
            .map { it.value.toBuildingSlab(sector, it.key, location.floorLevel) }
            .toTypedArray()
        sector.slabs[location.floorLevel] = slabs
    }

    return sectors.values.toTypedArray()
}

private fun outerSlabs(sector: BuildingSector) : Array<BuildingSlab> {
    return RealLifeLocation.entries
        .filter { it != RealLifeLocation.UNDEFINED }
        .map { BuildingSlab.outer(sector, it, 0.5f)}
        .toTypedArray()
}

fun MutableList<String>.write(location: LocationTableRow) {
    val orderedRooms = location.rooms.entries
        .sortedBy { backRoomsLocations[it.key] }
        .map { it.value }

    val orderedPassage = location.passages.entries
        .sortedBy { backPassagesLocations[it.key] }
        .map { it.value }

    val orderedWalls = location.walls.entries
        .sortedBy { backWallsLocations[it.key] }
        .map { it.value }

    val orderedFloors = location.floors.entries
        .sortedBy { backFloorsLocations[it.key] }
        .map { it.value }

    write(location.id)
    write(location.sector)
    write(location.level)
    write(location.type)
    write(location.title)
    orderedRooms.forEach { write(it) }
    orderedPassage.forEach { write(it) }
    orderedWalls.forEach { write(it) }
    orderedFloors.forEach { write(it) }
}