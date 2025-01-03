package com.sspirit.nadiiaspaceassistant.services.dataproviders.missions.propertyevacuation

import android.util.Log
import com.google.api.services.sheets.v4.model.ValueRange
import com.sspirit.nadiiaspaceassistant.utils.getBoolean
import com.sspirit.nadiiaspaceassistant.utils.getDate
import com.sspirit.nadiiaspaceassistant.utils.getFloat
import com.sspirit.nadiiaspaceassistant.utils.getInt
import com.sspirit.nadiiaspaceassistant.utils.getSplittedString
import com.sspirit.nadiiaspaceassistant.utils.getString
import com.sspirit.nadiiaspaceassistant.models.items.LootGroup
import com.sspirit.nadiiaspaceassistant.models.missions.PropertyEvacuation
import com.sspirit.nadiiaspaceassistant.models.missions.PropertyEvacuationGoal
import com.sspirit.nadiiaspaceassistant.models.missions.PropertyEvacuationKeys
import com.sspirit.nadiiaspaceassistant.models.missions.building.Building
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingBigObject
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingBigObjectPosition
import com.sspirit.nadiiaspaceassistant.models.missions.building.devices.BuildingDevice
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingDoorLock
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingLocation
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingPassage
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingPassagewayType
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingRoom
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingSector
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingSlab
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingVentGrilleState
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingWall
import com.sspirit.nadiiaspaceassistant.models.missions.building.devices.EnergyNodeState
import com.sspirit.nadiiaspaceassistant.models.missions.building.transport.BuildingTransport
import com.sspirit.nadiiaspaceassistant.services.dataproviders.CacheableDataLoader
import com.sspirit.nadiiaspaceassistant.services.dataproviders.GoogleSheetDataProvider
import com.sspirit.nadiiaspaceassistant.services.dataproviders.LootGroupsDataProvider
import com.sspirit.nadiiaspaceassistant.services.dataproviders.logTag
import com.sspirit.nadiiaspaceassistant.services.dataproviders.missions.MissionsDataProvider
import com.sspirit.nadiiaspaceassistant.services.dataproviders.missions.MissionsListDataProvider
import com.sspirit.nadiiaspaceassistant.services.dataproviders.tablerows.BuildingBigObjectRow
import com.sspirit.nadiiaspaceassistant.services.dataproviders.tablerows.TransportsTableRow
import com.sspirit.nadiiaspaceassistant.services.dataproviders.tablerows.location.LocationTableRow
import com.sspirit.nadiiaspaceassistant.services.dataproviders.tablerows.location.toBuildingSectors
import com.sspirit.nadiiaspaceassistant.services.dataproviders.tablerows.location.write
import com.sspirit.nadiiaspaceassistant.services.dataproviders.tablerows.toBuildingTransports

private val generationSpreadsheetId = "1e9BueiGhzgvlNSKBjG7Tt6lCJop30ZRkowxuBX4qnuk"
private val missionRange = "A1:Z1"
private val firstLocationRow = 4
private val firstBigObjectRow = 2
private val bigObjectsRange = "A$firstBigObjectRow:G75"
private val bigObjectsSheet = "BigObjects"
private val locationsRange = "A$firstLocationRow:EZ50"
private val locationsSheet = "Locations"
private val transportsRange = "A2:G50"
private val transportsSheet = "Transports"
private val lootTagsRange = "LootTags!A3:AZ10"

object PropertyEvacuationDataProvider : GoogleSheetDataProvider(),
    MissionsDataProvider<PropertyEvacuation> {
    val missions = mutableMapOf<String, PropertyEvacuation>()
    private val spreadsheets = mutableMapOf<String, String>()

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
                val goal = PropertyEvacuationGoal.byString(raw.getString(PropertyEvacuationKeys.GOAL))

                val mission = PropertyEvacuation(
                    id = raw.getString(PropertyEvacuationKeys.ID),
                    client = raw.getString(PropertyEvacuationKeys.CLIENT),
                    reward = raw.getInt(PropertyEvacuationKeys.REWARD),
                    difficult = raw.getFloat(PropertyEvacuationKeys.DIFFICULT),
                    expiration = raw.getDate(PropertyEvacuationKeys.EXPIRATION, dateFormatter),
                    requirements = raw.getString(PropertyEvacuationKeys.REQUIREMENTS),
                    place = raw.getString(PropertyEvacuationKeys.PLACE),
                    building = getBuilding(spreadsheetId, tags),
                    lootTags = tags,
                    goal = goal
                )
                spreadsheets[mission.id] = spreadsheetId
                return mission
            }
        } catch (e: Exception) {
            Log.e(logTag, "Invalid property evacuation data: ${e.toString()}")
        }

        return null
    }

    private fun getBuilding(spreadsheetId: String, tags: Array<String>) : Building {
        val building = Building()
        building.sectors = getSectors(spreadsheetId, building)
        building.transports = getTransports(spreadsheetId, building)
        building.availableLoot = getAvailableLootGroups(tags)
        building.bigObjects = getBigObjects(spreadsheetId, building)
        return building
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

    private fun getSectors(spreadsheetId: String, building: Building) : Array<BuildingSector> {
        val range = "$locationsSheet!$locationsRange"
        val response = service
            .spreadsheets()
            .values()
            .get(spreadsheetId, range)
            .execute()

        val rows = parseToArray(response, "Invalid sectors data", LocationTableRow::parse)
        return rows.toBuildingSectors(building)
    }

    private fun getTransports(
        spreadsheetId: String,
        building: Building
    ) : Array<BuildingTransport> {
        val range = "$transportsSheet!$transportsRange"
        val response = service
            .spreadsheets()
            .values()
            .get(spreadsheetId, range)
            .execute()

        val rows = parseToArray(response, "Invalid transports data", TransportsTableRow::parse)
        return rows.toBuildingTransports(building)
    }

    private fun getBigObjects(spreadsheetId: String, building: Building) : Array<BuildingBigObject> {
        val range = "$bigObjectsSheet!$bigObjectsRange"
        val response = service
            .spreadsheets()
            .values()
            .get(spreadsheetId, range)
            .execute()

        val rows = parseToArray(response, "Invalid big objects data", BuildingBigObjectRow::parse)
        return rows
            .mapNotNull { it.toBuildingBigObject(building) }
            .toTypedArray()
    }

    fun updatePassageType(
        missionId: String,
        passage: BuildingPassage,
        type: BuildingPassagewayType,
        completion: ((Boolean) -> Unit)?
    ) {
        val oldType = passage.type
        val oldDoor = passage.door
        passage.type = type
        if (type !in arrayOf(BuildingPassagewayType.DOOR, BuildingPassagewayType.OPEN_DOOR)) {
            passage.door = null
        }

        updateLocation(missionId, passage.location) { success ->
            if (!success) {
                passage.type = oldType
                passage.door = oldDoor
            }
            completion?.invoke(success)
        }
    }

    fun updatePassageVentGrille(
        missionId: String,
        passage: BuildingPassage,
        state: BuildingVentGrilleState,
        completion: ((Boolean) -> Unit)?
    ) {
        val old = passage.vent?.grilleState ?: BuildingVentGrilleState.UNDEFINED
        passage.vent?.grilleState = state
        updateLocation(missionId, passage.location) { success ->
            if (!success)
                passage.vent?.grilleState = old

            completion?.invoke(success)
        }
    }

    fun updatePassageLocks(
        missionId: String,
        passage: BuildingPassage,
        locks: Array<BuildingDoorLock>,
        completion: ((Boolean) -> Unit)?
    ) {
        val old = passage.door?.locks ?: arrayOf()
        passage.door?.locks = locks
        updateLocation(missionId, passage.location) { success ->
            if (!success)
                passage.door?.locks = old
            completion?.invoke(success)
        }
    }

    fun updateAllVentGrille(
        missionId: String,
        building: Building,
        state: BuildingVentGrilleState,
        completion: ((Boolean) -> Unit)?
    ) {
        for (sector in building.sectors) for (location in sector.locations) {
            val oldStates = location.passages.map { it.vent?.grilleState }
            location.passages.forEach { it.vent?.grilleState = state }

            updateLocation(missionId, location) { success ->
                if (!success) {
                    location.passages.mapIndexed{ i, p ->
                        p.vent?.grilleState = oldStates[i] ?: BuildingVentGrilleState.UNDEFINED
                    }
                }
                completion?.invoke(success)
            }
        }
    }

    fun updateAcidTankCharges(
        missionId: String,
        location: BuildingLocation,
        tank: BuildingDevice.AcidTank,
        charges: Int
    ) {
        val oldCharges = tank.charges
        tank.charges = charges
        updateLocation(missionId, location) { success ->
            if (!success)
                tank.charges = oldCharges
        }
    }

    fun updateEnergyNodeState(
        missionId: String,
        location: BuildingLocation,
        energyNode: BuildingDevice.EnergyNode,
        state: EnergyNodeState,
        completion: ((Boolean) -> Unit)?
    ) {
        val oldState = energyNode.state
        energyNode.state = state
        updateLocation(missionId, location) { success ->
            if (!success)
                energyNode.state = oldState
            completion?.invoke(success)
        }
    }

    fun updateSafetyConsoleHacked(
        missionId: String,
        location: BuildingLocation,
        console: BuildingDevice.SafetyConsole,
        hacked: Boolean,
        completion: ((Boolean) -> Unit)?
    ) {
        val oldHacked = console.hacked
        console.hacked = hacked
        updateLocation(missionId, location) { success ->
            if (!success)
                console.hacked = oldHacked
            completion?.invoke(success)
        }
    }

    fun removeAllRemoteLocks(missionId: String, building: Building) {
        for (sector in building.sectors) for (location in sector.locations) {
            val oldLocks = location.passages.map { it.door?.locks }
            for (passage in location.passages) {
                val door = passage.door ?: continue
                door.locks = door.locks.filter { it !is BuildingDoorLock.Remote }.toTypedArray()
            }

            updateLocation(missionId, location) { success ->
                if (!success) {
                    for (i in location.passages.indices) {
                        location.passages[i].door?.locks = oldLocks[i] ?: arrayOf()
                    }
                }
            }
        }
    }

    fun updateSlabHole(missionId: String, slab: BuildingSlab, hasHole: Boolean) {
        val location = slab.sector.locations.firstOrNull { it.floorLevel == slab.level} ?: return
        val old = slab.hasHole
        slab.hasHole = hasHole
        updateLocation(missionId, location) { success ->
            if (!success)
                slab.hasHole = old
        }
    }

    fun updateWallHole(missionId: String, wall: BuildingWall, hasHole: Boolean) {
        val old = wall.hasHole
        wall.hasHole = hasHole
        updateLocation(missionId, wall.location) { success ->
            if (!success)
                wall.hasHole = old
        }
    }

    fun updateBigObjectRoom(
        missionId: String,
        obj: BuildingBigObject,
        room: BuildingRoom,
        position: BuildingBigObjectPosition = BuildingBigObjectPosition.Free
    ) {
        val oldRoom = obj.room
        val oldPosition = obj.position
        obj.room = room
        obj.position = position
        updateBigObject(missionId, obj) { success ->
            if (!success) {
                obj.room = oldRoom
                obj.position = oldPosition
            }
        }
    }

    fun updateBigObjectPosition(missionId: String, obj: BuildingBigObject, position: BuildingBigObjectPosition) {
        val old = obj.position
        obj.position = position
        updateBigObject(missionId, obj) { success ->
            if (!success)
                obj.position = old
        }
    }

    private fun updateBigObject(
        missionId: String,
        obj: BuildingBigObject,
        completion: ((Boolean) -> Unit)? = null
        ) {
        val row = BuildingBigObjectRow.from(obj)
        uploadData(
            spreadsheetId = getSpreadsheet(missionId) ?: return,
            sheet = bigObjectsSheet,
            column = 1,
            startRow = firstBigObjectRow + obj.id.toInt() - 1,
            data = listOf(row.toRawData()),
            completion = completion
        )
    }

    private fun updateLocation(
        missionId: String,
        location: BuildingLocation,
        completion: ((Boolean) -> Unit)? = null
    ) {
        val dataList = mutableListOf<String>()
        val dataRow = LocationTableRow.from(location)
        dataList.write(dataRow)

        uploadData(
            spreadsheetId = getSpreadsheet(missionId) ?: return,
            sheet = locationsSheet,
            column = 1,
            startRow = firstLocationRow + location.id.toInt() - 1,
            data = listOf(dataList),
            completion = completion
        )
    }

    private fun getSpreadsheet(missionId: String) : String? {
        val spreadsheetId = spreadsheets[missionId]
        if (spreadsheetId == null)
            Log.e(logTag,"Missed spreadsheet id for mission id $missionId")
        return spreadsheetId
    }
}