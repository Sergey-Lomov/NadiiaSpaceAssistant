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
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingEvent
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingLocation
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingLootContainer
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingPassage
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingPassagewayType
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingRoom
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingSector
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingSlab
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingVentGrilleState
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingWall
import com.sspirit.nadiiaspaceassistant.models.missions.building.devices.EnergyNodeState
import com.sspirit.nadiiaspaceassistant.models.missions.building.specloot.BuildingSpecialLootContainer
import com.sspirit.nadiiaspaceassistant.models.missions.building.transport.BuildingTransport
import com.sspirit.nadiiaspaceassistant.services.dataproviders.CacheableDataLoader
import com.sspirit.nadiiaspaceassistant.services.dataproviders.Completion
import com.sspirit.nadiiaspaceassistant.services.dataproviders.GoogleSheetDataProvider
import com.sspirit.nadiiaspaceassistant.services.dataproviders.LootGroupsDataProvider
import com.sspirit.nadiiaspaceassistant.services.dataproviders.logTag
import com.sspirit.nadiiaspaceassistant.services.dataproviders.missions.MissionsDataProvider
import com.sspirit.nadiiaspaceassistant.services.dataproviders.missions.MissionsListDataProvider
import com.sspirit.nadiiaspaceassistant.services.dataproviders.tablerows.building.BuildingBigObjectTableRow
import com.sspirit.nadiiaspaceassistant.services.dataproviders.tablerows.building.BuildingLootContainerTableRow
import com.sspirit.nadiiaspaceassistant.services.dataproviders.tablerows.building.BuildingSpecialLootTableRow
import com.sspirit.nadiiaspaceassistant.services.dataproviders.tablerows.building.BuildingTransportTableRow
import com.sspirit.nadiiaspaceassistant.services.dataproviders.tablerows.building.location.LocationTableRow
import com.sspirit.nadiiaspaceassistant.services.dataproviders.tablerows.building.location.toBuildingSectors
import com.sspirit.nadiiaspaceassistant.services.dataproviders.tablerows.building.location.write
import com.sspirit.nadiiaspaceassistant.services.dataproviders.tablerows.building.toBuildingLootContainers
import com.sspirit.nadiiaspaceassistant.services.dataproviders.tablerows.building.toBuildingTransports

private val generationSpreadsheetId = "1e9BueiGhzgvlNSKBjG7Tt6lCJop30ZRkowxuBX4qnuk"
private val missionRange = "A1:Z1"

private val firstBigObjectRow = 2
private val bigObjectsRange = "A$firstBigObjectRow:G75"
private val bigObjectsSheet = "BigObjects"

private val firstLocationRow = 4
private val locationsRange = "A$firstLocationRow:EZ50"
private val locationsSheet = "Locations"

private val firstTransportRow = 2
private val transportsRange = "A$firstTransportRow:G50"
private val transportsSheet = "Transports"

private val firstLootRow = 3
private val lootRange = "A$firstLootRow:F150"
private val lootSheet = "Loot"

private val firstSpecialLootRow = 3
private val specialLootRange = "A$firstLootRow:E50"
private val specialLootSheet = "SpecLoot"

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
                    goal = goal,
                    time = raw.getInt(PropertyEvacuationKeys.TIME),
                )
                spreadsheets[mission.id] = spreadsheetId
                return mission
            }
        } catch (e: Exception) {
            Log.e(logTag, "Invalid property evacuation data: $e")
        }

        return null
    }

    private fun getBuilding(spreadsheetId: String, tags: Array<String>) : Building {
        val building = Building()
        building.sectors = getSectors(spreadsheetId, building)
        building.transports = getTransports(spreadsheetId, building)
        building.availableLoot = getAvailableLootGroups(tags)
        building.bigObjects = getBigObjects(spreadsheetId, building)
        building.loot = getLoot(spreadsheetId, building)
        building.specLoot = getSpecLoot(spreadsheetId, building)
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

    private fun getSectors(spreadsheetId: String, building: Building): Array<BuildingSector> {
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
    ): Array<BuildingTransport> {
        val range = "$transportsSheet!$transportsRange"
        val response = service
            .spreadsheets()
            .values()
            .get(spreadsheetId, range)
            .execute()

        val rows = parseToArray(response, "Invalid transports data", BuildingTransportTableRow::parse)
        return rows.toBuildingTransports(building)
    }

    private fun getBigObjects(spreadsheetId: String, building: Building): MutableList<BuildingBigObject> {
        val range = "$bigObjectsSheet!$bigObjectsRange"
        val response = service
            .spreadsheets()
            .values()
            .get(spreadsheetId, range)
            .execute()

        val rows = parseToArray(response, "Invalid big objects data", BuildingBigObjectTableRow::parse)
        return rows
            .mapNotNull { it.toBuildingBigObject(building) }
            .toMutableList()
    }

    private fun getLoot(spreadsheetId: String, building: Building): MutableList<BuildingLootContainer> {
        val range = "$lootSheet!$lootRange"
        val response = service
            .spreadsheets()
            .values()
            .get(spreadsheetId, range)
            .execute()

        val rows = parseToArray(
            range = response,
            error = "Invalid loot containers data",
            parser = BuildingLootContainerTableRow::parse,
            upDownMerge = arrayOf(0, 1, 2, 3)
        )
        return rows.toBuildingLootContainers(building).toMutableList()
    }

    private fun getSpecLoot(spreadsheetId: String, building: Building): MutableList<BuildingSpecialLootContainer> {
        val range = "$specialLootSheet!$specialLootRange"
        val response = service
            .spreadsheets()
            .values()
            .get(spreadsheetId, range)
            .execute()

        val rows = parseToArray(response, "Invalid spec loot data", BuildingSpecialLootTableRow::parse,)
        val loots = rows.mapNotNull { it.toBuildingSpecialLoot(building) }
        return loots.toMutableList()
    }

    fun updatePassageType(
        missionId: String,
        passage: BuildingPassage,
        type: BuildingPassagewayType,
        completion: Completion
    ) = synchronized(passage.location) {
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
        completion: Completion
    ) = synchronized(passage.location) {
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
        completion: Completion
    ) = synchronized(passage.location) {
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
        completion: Completion
    ) {
        for (sector in building.sectors)
            for (location in sector.locations) {
                synchronized(location) {
                    val oldStates = location.passages.map { it.vent?.grilleState }
                    location.passages.forEach { it.vent?.grilleState = state }

                    updateLocation(missionId, location) { success ->
                        if (!success) {
                            location.passages.mapIndexed { i, p ->
                                p.vent?.grilleState =
                                    oldStates[i] ?: BuildingVentGrilleState.UNDEFINED
                            }
                        }
                        completion?.invoke(success)
                    }
                }
        }
    }

    fun addDevice(
        missionId: String,
        room: BuildingRoom,
        device: BuildingDevice
    ) = synchronized(room.location) {
        room.addDevice(device)
        updateLocation(missionId, room.location) { success ->
            if (!success)
                room.removeDevice(device)
        }
    }

    fun updateAutoDoctorEnergy(
        missionId: String,
        location: BuildingLocation,
        doctor: BuildingDevice.AutoDoctor,
        energy: Int,
        completion: Completion = null
    ) = synchronized(location) {
        val oldEnergy = doctor.energy
        doctor.energy = energy
        updateLocation(missionId, location) { success ->
            if (!success)
                doctor.energy = oldEnergy
            completion?.invoke(success)
        }
    }

    fun updateAcidTankCharges(
        missionId: String,
        location: BuildingLocation,
        tank: BuildingDevice.AcidTank,
        charges: Int
    ) = synchronized(location) {
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
        completion: Completion
    ) = synchronized(location) {
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
        completion: Completion
    ) = synchronized(location) {
        val oldHacked = console.hacked
        console.hacked = hacked
        updateLocation(missionId, location) { success ->
            if (!success)
                console.hacked = oldHacked
            completion?.invoke(success)
        }
    }

    fun removeAllRemoteLocks(
        missionId: String,
        building: Building
    ) {
        for (sector in building.sectors)
            for (location in sector.locations) {
                synchronized(location) {
                    val oldLocks = location.passages.map { it.door?.locks }
                    for (passage in location.passages) {
                        val door = passage.door ?: continue
                        door.locks =
                            door.locks.filter { it !is BuildingDoorLock.Remote }.toTypedArray()
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
    }

    fun updateSlabHole(missionId: String, slab: BuildingSlab, hasHole: Boolean) {
        val location = slab.sector.locations.firstOrNull { it.floorLevel == slab.level} ?: return
        synchronized(location) {
            val old = slab.hasHole
            slab.hasHole = hasHole
            updateLocation(missionId, location) { success ->
                if (!success)
                    slab.hasHole = old
            }
        }
    }

    fun updateWallHole(
        missionId: String,
        wall: BuildingWall,
        hasHole: Boolean
    ) = synchronized(wall.location) {
        val old = wall.hasHole
        wall.hasHole = hasHole
        updateLocation(missionId, wall.location) { success ->
            if (!success)
                wall.hasHole = old
        }
    }

    fun removeEvent(
        missionId: String,
        event: BuildingEvent,
        room: BuildingRoom,
        completion: Completion = null
    ) = synchronized(room.location) {
        room.removeEvent(event)
        updateLocation(missionId, room.location) { success ->
            if (!success) room.addEvent(event)
            completion?.invoke(success)
        }
    }

    fun updateBigObjectRoom(
        missionId: String,
        obj: BuildingBigObject,
        room: BuildingRoom,
        position: BuildingBigObjectPosition = BuildingBigObjectPosition.Free
    ) = synchronized(obj) {
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

    fun updateBigObjectPosition(
        missionId: String,
        obj: BuildingBigObject,
        position: BuildingBigObjectPosition
    ) = synchronized(obj) {
        val old = obj.position
        obj.position = position
        updateBigObject(missionId, obj) { success ->
            if (!success)
                obj.position = old
        }
    }

    fun removeBigObject(
        missionId: String,
        obj: BuildingBigObject,
        completion: Completion = null
    ) = synchronized(obj) {
        val spreadsheetId = getSpreadsheet(missionId) ?: return
        val index = firstRowWithText(obj.id, spreadsheetId, bigObjectsSheet) ?: return
        deleteRow(spreadsheetId, bigObjectsSheet, index) { success ->
            if (success) {
                getBy(missionId)?.building?.bigObjects?.remove(obj)
            }
            completion?.invoke(success)
        }
    }

    fun removeLootContainer(
        missionId: String,
        loot: BuildingLootContainer,
        completion: Completion = null
    ) = synchronized(loot) {
        val spreadsheetId = spreadsheets[missionId]
        if (spreadsheetId == null) {
            completion?.invoke(false)
            return
        }

        val indices = searchRowsWithText(loot.id, spreadsheetId, lootSheet)
        deleteRows(spreadsheetId, lootSheet, indices) { success ->
            if (success) {
                getBy(missionId)?.building?.loot?.remove(loot)
            }
            completion?.invoke(success)
        }
    }

    private fun addLootContainer(
        missionId: String,
        loot: BuildingLootContainer,
        completion: Completion = null
    ) {
        val spreadsheetId = spreadsheets[missionId] ?: return
        val rows = BuildingLootContainerTableRow.from(loot)
        val data = rows.map { it.toRawData() }
        insert(spreadsheetId, lootSheet, firstLootRow, data) { success ->
            if (success)
                getBy(missionId)?.building?.loot?.add(loot)
            completion?.invoke(success)
        }
    }

    fun replaceLootContainer(
        missionId: String,
        newLoot: BuildingLootContainer,
        completion: Completion = null
    ) {
        val building = newLoot.room.location.sector.building
        val oldContainer = building.loot.firstOrNull { it.id == newLoot.id }
        if (oldContainer != null) {
            removeLootContainer(missionId, oldContainer) { success ->
                if (!success) {
                    completion?.invoke(false)
                }
            }
        }

        addLootContainer(missionId, newLoot) { success ->
            if (!success && oldContainer != null)
                getBy(missionId)?.building?.loot?.add(oldContainer)
            completion?.invoke(success)
        }
    }

    fun pickUpSpecialLoot(
        missionId: String,
        container: BuildingSpecialLootContainer,
        completion: Completion = null
    ) {
        val oldRoom = container.room
        container.room = null

        val building = oldRoom?.location?.sector?.building
        if (building == null) {
            completion?.invoke(true)
            return
        }

        updateSpecialLootContainer(missionId, container) { success ->
            if (!success) container.room = oldRoom
            completion?.invoke(success)
        }
    }

    private fun updateSpecialLootContainer(missionId: String, container: BuildingSpecialLootContainer, completion: Completion = null) {
        val spreadsheetId = getSpreadsheet(missionId) ?: return
        val index = firstRowWithText(container.id, spreadsheetId, specialLootSheet) ?: return
        val row = BuildingSpecialLootTableRow.from(container)
        uploadData(
            spreadsheetId = getSpreadsheet(missionId) ?: return,
            sheet = specialLootSheet,
            column = 1,
            startRow = index,
            data = listOf(row.toRawData()),
            completion = completion
        )
    }

    private fun updateBigObject(missionId: String, obj: BuildingBigObject, completion: Completion = null) {
        val spreadsheetId = getSpreadsheet(missionId) ?: return
        val index = firstRowWithText(obj.id, spreadsheetId, bigObjectsSheet) ?: return
        val row = BuildingBigObjectTableRow.from(obj)

        uploadData(
            spreadsheetId = spreadsheetId,
            sheet = bigObjectsSheet,
            column = 1,
            startRow = index,
            data = listOf(row.toRawData()),
            completion = completion
        )
    }

    fun updateLocation(missionId: String, location: BuildingLocation, completion: Completion = null) {
        val spreadsheetId = getSpreadsheet(missionId) ?: return
        val index = firstRowWithText(location.id, spreadsheetId, locationsSheet) ?: return
        val dataList = mutableListOf<String>()
        val dataRow = LocationTableRow.from(location)
        dataList.write(dataRow)

        uploadData(
            spreadsheetId = getSpreadsheet(missionId) ?: return,
            sheet = locationsSheet,
            column = 1,
            startRow = index,
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