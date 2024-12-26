package com.sspirit.nadiiaspaceassistant.services.dataproviders.missions.propertyevacuation

import android.util.Log
import com.google.api.services.sheets.v4.model.ValueRange
import com.sspirit.nadiiaspaceassistant.extensions.getBoolean
import com.sspirit.nadiiaspaceassistant.extensions.getDate
import com.sspirit.nadiiaspaceassistant.extensions.getFloat
import com.sspirit.nadiiaspaceassistant.extensions.getInt
import com.sspirit.nadiiaspaceassistant.extensions.getSplittedString
import com.sspirit.nadiiaspaceassistant.extensions.getString
import com.sspirit.nadiiaspaceassistant.models.items.LootGroup
import com.sspirit.nadiiaspaceassistant.models.missions.PropertyEvacuation
import com.sspirit.nadiiaspaceassistant.models.missions.PropertyEvacuationKeys
import com.sspirit.nadiiaspaceassistant.models.missions.building.Building
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingDevice
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingDoor
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingDoorHackingLevel
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingDoorLock
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingDoorTurn
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingEvent
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingLocation
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingLocationType
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingMaterial
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingMaterialLucidity
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingPassageway
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingPassagewayType
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingRoom
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingSector
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingSlab
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingVent
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingVentGrilleState
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingVentSize
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingWall
import com.sspirit.nadiiaspaceassistant.models.missions.building.LootGroupInstance
import com.sspirit.nadiiaspaceassistant.models.missions.building.RealLifeLocation
import com.sspirit.nadiiaspaceassistant.models.missions.building.specloot.SpecialLoot
import com.sspirit.nadiiaspaceassistant.models.missions.building.transport.BuildingElevator
import com.sspirit.nadiiaspaceassistant.models.missions.building.transport.BuildingShuttlePod
import com.sspirit.nadiiaspaceassistant.models.missions.building.transport.BuildingTeleport
import com.sspirit.nadiiaspaceassistant.models.missions.building.transport.BuildingTransport
import com.sspirit.nadiiaspaceassistant.services.dataproviders.CacheableDataLoader
import com.sspirit.nadiiaspaceassistant.services.dataproviders.GoogleSheetDataProvider
import com.sspirit.nadiiaspaceassistant.services.dataproviders.LootGroupsDataProvider
import com.sspirit.nadiiaspaceassistant.services.dataproviders.logTag
import com.sspirit.nadiiaspaceassistant.services.dataproviders.missions.MissionsDataProvider
import com.sspirit.nadiiaspaceassistant.services.dataproviders.missions.MissionsListDataProvider
import com.sspirit.nadiiaspaceassistant.services.dataproviders.tablerows.LocationTableRow
import com.sspirit.nadiiaspaceassistant.services.dataproviders.tablerows.LocationTableRowMaterial
import com.sspirit.nadiiaspaceassistant.services.dataproviders.tablerows.LocationTableRowPassage
import com.sspirit.nadiiaspaceassistant.services.dataproviders.tablerows.LocationTableRowRoom
import com.sspirit.nadiiaspaceassistant.services.dataproviders.tablerows.LocationTableRowWall
import com.sspirit.nadiiaspaceassistant.services.dataproviders.tablerows.RealLifeLocations
import com.sspirit.nadiiaspaceassistant.services.dataproviders.tablerows.TransportsTableRow

private val generationSpreadsheetId = "1e9BueiGhzgvlNSKBjG7Tt6lCJop30ZRkowxuBX4qnuk"
private val missionRange = "A1:Z1"
private val locationsRange = "A4:EZ50"
private val locationsSheet = "Locations"
private val transportsRange = "A2:G50"
private val transportsSheet = "Transports"
private val lootTagsRange = "LootTags!A3:AZ10"

object PropertyEvacuationDataProvider : GoogleSheetDataProvider(),
    MissionsDataProvider<PropertyEvacuation> {
    var missions = mutableMapOf<String, PropertyEvacuation>()

    override fun getBy(id: String): PropertyEvacuation? {
        return missions[id]
    }

    override fun download(id: String) {
        CacheableDataLoader.reloadPropertyEvacuationData()

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
                val tags = raw.getSplittedString(PropertyEvacuationKeys.LOOT_TAGS)
                return PropertyEvacuation(
                    id = raw.getString(PropertyEvacuationKeys.ID),
                    client = raw.getString(PropertyEvacuationKeys.CLIENT),
                    reward = raw.getInt(PropertyEvacuationKeys.REWARD),
                    difficult = raw.getFloat(PropertyEvacuationKeys.DIFFICULT),
                    expiration = raw.getDate(PropertyEvacuationKeys.EXPIRATION, dateFormatter),
                    requirements = raw.getString(PropertyEvacuationKeys.REQUIREMENTS),
                    place = raw.getString(PropertyEvacuationKeys.PLACE),
                    building = getBuilding(spreadsheetId, tags)
                )
            }
        } catch (e: Exception) {
            Log.e(logTag, "Invalid property evacuation data: ${e.toString()}")
        }

        return null
    }

    private fun getBuilding(spreadsheetId: String, tags: Array<String>) : Building {
        val sectors = getSectors(spreadsheetId)
        val transports = getTransports(spreadsheetId, sectors)
        val lootGroups = getAvailableLootGroups(tags)
        return Building(sectors, transports, lootGroups)
    }

    private fun getAvailableLootGroups(tags: Array<String>) : Array<LootGroup> {
        val response = service
            .spreadsheets()
            .values()
            .get(generationSpreadsheetId, lootTagsRange)
            .execute()

        val availableGroups = mutableListOf<LootGroup>()
        val rawLines = response.getValues()?.map { it.toTypedArray() }?.toTypedArray()
        try {
            if (rawLines != null) {
                val filtered = rawLines.filter { it.getString(0) in tags }
                for (group in LootGroupsDataProvider.groups) {
                    val available = filtered.fold(false) { acc, it -> acc || it.getBoolean(group.id.toInt())}
                    if (available) availableGroups.add(group)
                }
            }
        } catch (e: Exception) {
            Log.e(logTag, "Invalid loots tag data: $e")
        }
        
        return availableGroups.toTypedArray()
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
        location.passages = passagesFrom(row.passages, location)

        return location
    }

    private fun roomsFrom(
        rowRooms: Map<RealLifeLocation, LocationTableRowRoom>,
        location: BuildingLocation
    ) : Array<BuildingRoom> {
        return rowRooms.map { entry ->
            val devices = entry.value.devices.map { BuildingDevice.byString(it) }.toTypedArray()
            val events = entry.value.events.map { BuildingEvent.byString(it) }.toTypedArray()
            BuildingRoom(
                type = entry.value.type,
                location = location,
                realLocation = entry.key,
                light = entry.value.light,
                loot = parseLoot(entry.value.loot),
                specLoot = parseSpecLoot(entry.value.loot),
                devices = devices,
                events = events
            )
        }.toTypedArray()
    }

    private fun wallsFrom(
        rowWalls: Map<RealLifeLocations, LocationTableRowWall>,
        location: BuildingLocation
    ) : Array<BuildingWall> {
        return rowWalls.map { entry ->
            val room1 = location.rooms.first { it.realLocation == entry.key.first }
            val room2 = location.rooms.first { it.realLocation == entry.key.second }
            BuildingWall(
                location = location,
                room1 = room1,
                room2 = room2,
                material = materialFrom(entry.value.material),
                hasHole = entry.value.hasHole
            )
        }.toTypedArray()
    }

    private fun passagesFrom(
        rowPassages: Map<RealLifeLocations, LocationTableRowPassage>,
        location: BuildingLocation
    ) : Array<BuildingPassageway> {
        return rowPassages.map { entry ->
            val room1 = location.rooms.first { it.realLocation == entry.key.first }
            val room2 = location.rooms.first { it.realLocation == entry.key.second }
            val type = BuildingPassagewayType.byString(entry.value.type)

            val passage = BuildingPassageway(
                room1 = room1,
                room2 = room2,
                type = type
            )
            passage.door = doorFrom(entry.value, passage, type)
            passage.vent = ventFrom(entry.value, passage)

            return@map passage
        }.toTypedArray()
    }
    
    private fun doorFrom(
        row: LocationTableRowPassage,
        passage: BuildingPassageway,
        type: BuildingPassagewayType
    ) : BuildingDoor? {
        if (type != BuildingPassagewayType.DOOR) return null
        val locks = row.locks.map { BuildingDoorLock.byString(it) }.toTypedArray()
        return BuildingDoor(
            passageway = passage,
            locks = locks,
            hacking = BuildingDoorHackingLevel.byString(row.hack),
            turn = BuildingDoorTurn.byString(row.turn),
            material = materialFrom(row.material)
        )
    }

    private const val NO_VENT = "Нет"
    private fun ventFrom(
        row: LocationTableRowPassage,
        passage: BuildingPassageway
    ) : BuildingVent? {
        if (row.ventSize == NO_VENT) return null
        return BuildingVent(
            passageway = passage,
            size = BuildingVentSize.byString(row.ventSize),
            grilleState = BuildingVentGrilleState.byString(row.ventState)
        )
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

        var transports = rooms.map { entry ->
            val id = entry.key
            val type = rows.first { it.id == id }.type
            val transportRooms = entry.value.toTypedArray()
            return@map transport(id, type, transportRooms)
        }

        transports = transports.mapNotNull { it }
        transports.forEach { transport ->
            transport.rooms.forEach {
                it.addTransport(transport)
            }
        }

        return transports.toTypedArray()
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