package com.sspirit.nadiiaspaceassistant.services.dataproviders.missions.propertyevacuation

import android.util.Log
import com.google.api.services.sheets.v4.model.ValueRange
import com.sspirit.nadiiaspaceassistant.extensions.getDate
import com.sspirit.nadiiaspaceassistant.extensions.getFloat
import com.sspirit.nadiiaspaceassistant.extensions.getInt
import com.sspirit.nadiiaspaceassistant.extensions.getString
import com.sspirit.nadiiaspaceassistant.models.missions.PropertyEvacuation
import com.sspirit.nadiiaspaceassistant.models.missions.PropertyEvacuationKeys
import com.sspirit.nadiiaspaceassistant.models.missions.building.Building
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingLocation
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingLocationType
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingMaterial
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingMaterialLucidity
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingRoom
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingSector
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingSlab
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingWall
import com.sspirit.nadiiaspaceassistant.models.missions.building.LootGroupInstance
import com.sspirit.nadiiaspaceassistant.models.missions.building.RealLifeLocation
import com.sspirit.nadiiaspaceassistant.models.missions.building.specloot.SpecialLoot
import com.sspirit.nadiiaspaceassistant.models.missions.building.transport.BuildingElevator
import com.sspirit.nadiiaspaceassistant.models.missions.building.transport.BuildingShuttlePod
import com.sspirit.nadiiaspaceassistant.models.missions.building.transport.BuildingTeleport
import com.sspirit.nadiiaspaceassistant.models.missions.building.transport.BuildingTransport
import com.sspirit.nadiiaspaceassistant.services.dataproviders.GoogleSheetDataProvider
import com.sspirit.nadiiaspaceassistant.services.dataproviders.logTag
import com.sspirit.nadiiaspaceassistant.services.dataproviders.missions.MissionsDataProvider
import com.sspirit.nadiiaspaceassistant.services.dataproviders.missions.MissionsListDataProvider
import java.util.Optional

private val missionRange = "A1:Z1"
private val locationsRange = "A4:EZ50"
private val locationsSheet = "Locations"
private val transportsRange = "A2:G50"
private val transportsSheet = "Transports"


object PropertyEvacuationDataProvider : GoogleSheetDataProvider(),
    MissionsDataProvider<PropertyEvacuation> {
    var missions = mutableMapOf<String, PropertyEvacuation>()

    override fun getBy(id: String): PropertyEvacuation? {
        return missions[id]
    }

    override fun download(id: String) {
        val range = "$id!$missionRange"
        val response = service
            .spreadsheets()
            .values()
            .get(MissionsListDataProvider.spreadsheetId, range)
            .execute()

        val mission = parseMission(response)
        if (mission != null)
            missions[id] = mission
    }

    private fun parseMission(valueRange: ValueRange): PropertyEvacuation? {
        val rawLines = valueRange.getValues()?.map { it.toTypedArray() }?.toTypedArray()
        val raw = rawLines?.firstOrNull()

        try {
            if (raw != null) {
                val spreadsheetId = raw.getString(PropertyEvacuationKeys.SPREADSHEET_ID)
                return PropertyEvacuation(
                    id = raw.getString(PropertyEvacuationKeys.ID),
                    client = raw.getString(PropertyEvacuationKeys.CLIENT),
                    reward = raw.getInt(PropertyEvacuationKeys.REWARD),
                    difficult = raw.getFloat(PropertyEvacuationKeys.DIFFICULT),
                    expiration = raw.getDate(PropertyEvacuationKeys.EXPIRATION, dateFormatter),
                    requirements = raw.getString(PropertyEvacuationKeys.REQUIREMENTS),
                    place = raw.getString(PropertyEvacuationKeys.PLACE),
                    building = getBuilding(spreadsheetId)
                )
            }
        } catch (e: Exception) {
            Log.e(logTag, "Invalid property evacuation data: ${e.toString()}")
        }

        return null
    }

    private fun getBuilding(spreadsheetId: String) : Building {
        val sectors = getSectors(spreadsheetId)
        val transports = getTransports(spreadsheetId, sectors)
        return Building(sectors, transports)
    }

    private fun getSectors(spreadsheetId: String) : Array<BuildingSector> {
        val range = "$locationsSheet!$locationsRange"
        val response = service
            .spreadsheets()
            .values()
            .get(spreadsheetId, range)
            .execute()

        val rows = parseToArray(response, "Invalid sectors data", LocationTableRow::parse)
        val sectors = mutableMapOf<String, BuildingSector>()
        for (row in rows) {
            val sectorId = row.sector
            if (sectorId !in sectors.keys) sectors[sectorId] = BuildingSector(sectorId)
            val sector = sectors[sectorId]!!
            val location = locationFrom(row, sector)
            sector.locations.add(location)
            val slabs = slabsFrom(row, sector)
            sector.slabs.add(slabs)
        }

        return sectors.values.toTypedArray()
    }

    private fun locationFrom(row: LocationTableRow, sector: BuildingSector) : BuildingLocation {
        val location = BuildingLocation(
            id = row.id,
            type = BuildingLocationType.byString(row.type),
            sector = sector,
            level = row.level,
            title = row.title,
        )

        location.rooms = roomsFrom(row.rooms, location)
        location.walls = wallsFrom(row.walls, location)

        return location
    }

    private fun roomsFrom(
        rowRooms: Map<RealLifeLocation, LocationTableRowRoom>,
        location: BuildingLocation
    ) : Array<BuildingRoom> {
        return rowRooms.map {
            BuildingRoom(
                type = it.value.type,
                location = location,
                realLocation = it.key,
                light = it.value.light,
                loot = parseLoot(it.value.loot),
                specLoot = parseSpecLoot(it.value.loot),
                devices = it.value.devices,
                events = it.value.events
            )
        }.toTypedArray()
    }

    private fun wallsFrom(
        rowWalls: Map<RealLifeLocations, LocationTableRowWall>,
        location: BuildingLocation
    ) : Array<BuildingWall> {
        return rowWalls.map {
            BuildingWall(
                location = location,
                realLocation1 = it.key.first,
                realLocation2 = it.key.second,
                material = materialFrom(it.value.material),
                hasHole = it.value.hasHole
            )
        }.toTypedArray()
    }

    private fun materialFrom(row: LocationTableRowMaterial) : BuildingMaterial {
        return BuildingMaterial(
            lucidity = BuildingMaterialLucidity.byString(row.lucidity),
            heatImmune = row.heatImmune,
            acidImmune = row.acidImmune,
            explosionImmune = row.explosionImmune
        )
    }

    private fun slabsFrom(row: LocationTableRow, sector: BuildingSector) : Array<BuildingSlab> {
        return row.floors.map {
            BuildingSlab(
                sector = sector,
                material = materialFrom(it.value.material),
                realLocation = it.key,
                level = row.level,
                hasHole = it.value.hasHole
            )
        }.toTypedArray()
    }

    private fun parseLoot(string: String) : Array<LootGroupInstance> {
        return arrayOf()
    }

    private fun parseSpecLoot(string: String) : Array<SpecialLoot> {
        return arrayOf()
    }

    private fun getTransports(
        spreadsheetId: String,
        sectors: Array<BuildingSector>
    ) : Array<BuildingTransport> {
        val range = "$transportsSheet!$transportsRange"
        val response = service
            .spreadsheets()
            .values()
            .get(spreadsheetId, range)
            .execute()

        val rows = parseToArray(response, "Invalid transports data", TransportsTableRow::parse)
        val rooms = mutableMapOf<String, MutableList<BuildingRoom>>()
        for (row in rows) {
            if (row.id !in rooms.keys)
                rooms[row.id] = mutableListOf()

            val realLocation = RealLifeLocation.byString(row.realLocation)
            val room = sectors
                .flatMap { it.locations }
                .firstOrNull { it.id == row.locationId }
                ?.rooms?.firstOrNull { it.realLocation == realLocation }

            if (room == null) {
                Log.e(logTag, "Cant find room for location ${row.locationId} and real locations $realLocation")
                continue
            }

            rooms[row.id]?.add(room)
        }

        return rooms.map { entry ->
            val id = entry.key
            val type = rows.first { it.id == id }.type
            val transportRooms = entry.value.toTypedArray()
            return@map transport(id, type, transportRooms)
        }
            .mapNotNull { it }
            .toTypedArray()
    }

    private fun transport(id: String, type: String, rooms: Array<BuildingRoom>) : BuildingTransport? {
        return when (type) {
            "Лифт" -> BuildingElevator(id, rooms)
            "Телепорт" -> BuildingTeleport(id, rooms.first(), rooms.last())
            "Монорельс" -> BuildingShuttlePod(id, rooms)
            else -> {
                Log.e(logTag, "Invalid transport type $type")
                return null
            }
            }
    }
}