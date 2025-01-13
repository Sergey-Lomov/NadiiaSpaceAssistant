package com.sspirit.nadiiaspaceassistant.services.analyzers.building

import com.sspirit.nadiiaspaceassistant.models.missions.building.Building
import com.sspirit.nadiiaspaceassistant.models.missions.building.devices.BuildingDevice
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingDoorLock
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingEvent
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingMaterial
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingMaterialLucidity
import com.sspirit.nadiiaspaceassistant.models.missions.building.specloot.BuildingDoorCode
import com.sspirit.nadiiaspaceassistant.models.missions.building.specloot.BuildingDoorKeyCard
import com.sspirit.nadiiaspaceassistant.services.dataproviders.missions.propertyevacuation.RoomsDescriptorsDataProvider
import com.sspirit.nadiiaspaceassistant.ui.utils.fullAddress
import com.sspirit.nadiiaspaceassistant.utils.flatArrayMap

private const val smallStabilizerId = "69"
private const val bigStabilizerId = "70"

class BuildingAnalyzer(val building: Building) {
    val report = BuildingAnalyzingReport()

    private val sectors = building.sectors
    private val locations = sectors.flatMap { it.locations }
    private val rooms = locations.flatArrayMap { it.rooms }
    private val validRooms = rooms.filter { it.isValid }
    private val walls = locations.flatArrayMap { it.walls }
    private val passages = locations.flatArrayMap { it.passages }
    private val doors = passages.mapNotNull { it.door }
    private val locks = doors.flatArrayMap { it.locks }
    private val validRoomsSlabs = validRooms.flatArrayMap { it.slabs }.toSet()
    private val validRoomsWalls = validRooms.flatArrayMap { it.walls }.toSet()
    private val specLoots = building.specLoot.map { it.loot }
    private val cardsColors = specLoots.filterIsInstance<BuildingDoorKeyCard>().map { it.color }
    private val codes = specLoots.filterIsInstance<BuildingDoorCode>().map { it.code }
    private val devices = rooms.flatArrayMap { it.devices }
    private val events = rooms.flatArrayMap { it.events }
    private val lootItems = rooms.flatArrayMap { it.loot }.flatArrayMap { it.nodes }

    init {
        analyzeLoot()
        analyzeMaterials()
        analyzeLocations()
        analyzeLocks()
        analyzeSlabs()
        analyzeWalls()
        analyzeDevices()
        analyzeEvents()
    }

    private fun addIssue(type: BuildingIssuesType, issue: String) =
        report.addIssue(type, issue)

    private fun addFix(type: BuildingFixingType, data: BuildingFixingData) =
        report.addFix(type, data)

    private fun analyzeLoot() {
        val totalPrice = lootItems
            .fold(0) { acc, lootItem -> acc + lootItem.item.sellPrice * lootItem.amount  }

        report.loot = BuildingLootAnalyzingReport(
            totalPrice = totalPrice,
            bigStabilizers = lootItems.filter { it.item.id == bigStabilizerId }.size,
            smallStabilizers = lootItems.filter { it.item.id == smallStabilizerId }.size
        )
    }

    private fun analyzeMaterials() {
        val slabsMaterials = validRoomsSlabs
            .filter { !it.isOuter }
            .map { it.material }
        report.materials[BuildingMaterialHolder.SLAB] = analyzeMaterialsList(slabsMaterials)

        val wallsMaterials = walls
            .filter { !it.isOuter }
            .map { it.material }
        report.materials[BuildingMaterialHolder.WALL] = analyzeMaterialsList(wallsMaterials)

        val doorsMaterials = doors.map { it.material }
        report.materials[BuildingMaterialHolder.DOOR] = analyzeMaterialsList(doorsMaterials)
    }

    private fun analyzeMaterialsList(materials: List<BuildingMaterial>) : BuildingMaterialsAnalyzingReport {
        val total = materials.size.toFloat()
        val luciduty = BuildingMaterialLucidity.entries
            .associateWith { l -> materials.filter { it.lucidity == l } }
            .mapValues { it.value.size / total }
        val heat = materials.filter { it.heatImmune }.size / total
        val acid = materials.filter { it.acidImmune }.size / total
        val explosion = materials.filter { it.explosionImmune }.size / total

        return BuildingMaterialsAnalyzingReport(luciduty, heat, acid, explosion)
    }

    private fun analyzeLocations() {
        val reachable = building.transports
            .flatArrayMap { it.rooms }
            .distinctBy { it.location }
            .map { it.location }

        if (locations.size > reachable.size) {
            for (location in locations) {
                if (location !in reachable) {
                    val issue = "Недостижима локация ${fullAddress(location)}"
                    addIssue(BuildingIssuesType.LOCATIONS, issue)
                }
            }
        }
    }

    private fun analyzeLocks() {
        for (lock in locks) {
            when (lock) {
                BuildingDoorLock.Undefined -> {
                    addIssue(BuildingIssuesType.LOCKS, "Обнаружен Undefined замок")
                    continue
                }

                BuildingDoorLock.Biometry -> continue

                BuildingDoorLock.Remote -> {
                    if (!devices.any { it is BuildingDevice.SafetyConsole }) {
                        val stubConsole = BuildingDevice.SafetyConsole(false)
                        val issue = "Отсутствует устройство \"${stubConsole.title}\""
                        addIssue(BuildingIssuesType.LOCKS, issue)
                        continue
                    }

                }

                is BuildingDoorLock.Card -> {
                    if (lock.color !in cardsColors) {
                        val issue = "Отсутствует карта доступа (${lock.color})"
                        addIssue(BuildingIssuesType.LOCKS, issue)
                        continue
                    }
                }

                is BuildingDoorLock.Code -> {
                    if (lock.code !in codes) {
                        val issue = "Отсутствует код для замка (${lock.code})"
                        addIssue(BuildingIssuesType.LOCKS, issue)
                        continue
                    }
                }
            }
        }
    }

    private fun analyzeSlabs() {
        val outerSlabs = validRoomsSlabs
            .filter { it.isOuter }
            .filter { it.material != BuildingMaterial.outer }
        for (slab in outerSlabs) {
            val issue = "Материал внешнего перекрытия не соответствует outer материалу: ${fullAddress(slab)}"
            addIssue(BuildingIssuesType.SLABS, issue)
            addFix(BuildingFixingType.OUTER_SLAB_MATERIAL, OuterSlabMaterialFixing(slab))
        }
    }

    private fun analyzeWalls() {
        val outerWalls = validRoomsWalls
            .filter { it.isOuter }
            .filter { it.material != BuildingMaterial.outer }
        for (wall in outerWalls) {
            val issue = "Материал внешней стены не соответствует outer материалу: ${fullAddress(wall)}"
            addIssue(BuildingIssuesType.WALLS, issue)
            addFix(BuildingFixingType.OUTER_WALL_MATERIAL, OuterWallMaterialFixing(wall))
        }
    }

    private fun analyzeDevices() {
        if (devices.isEmpty())
            addIssue(BuildingIssuesType.DEVICES, "На объекте нет устройств")

        for (room in rooms) {
            val descriptor = RoomsDescriptorsDataProvider.getFor(room) ?: continue
            for (device in room.devices) {
                if (device == BuildingDevice.Undefined) {
                    val issue = "Неопознанное устройство в комнате (${fullAddress(room)})"
                    addIssue(BuildingIssuesType.DEVICES, issue)
                }

                if (device.title !in descriptor.deviceTypes) {
                    val issue =
                        "Нерекомендованное устройство ${device.title} расположено в комнате ${room.type} (${
                            fullAddress(room)
                        })"
                    addIssue(BuildingIssuesType.DEVICES, issue)
                }
            }
        }
    }

    private fun analyzeEvents() {
        if (events.isEmpty())
            addIssue(BuildingIssuesType.EVENTS, "На объекте нет событий")

        val floorFallsDownIssuedRooms = rooms
            .filter { BuildingEvent.FLOOR_FALL in it.events }
            .filter { it.floor.downValidRoom == null }
        for (room in floorFallsDownIssuedRooms) {
            val issue = "Событие \"${BuildingEvent.FLOOR_FALL.title}\" в комнате без валидной комнаты снизу: ${fullAddress(room)}"
            addIssue(BuildingIssuesType.EVENTS, issue)
        }

        val floorFallsHoleIssuedRooms = rooms
            .filter { BuildingEvent.FLOOR_FALL in it.events }
            .filter { it.floor.hasHole }
        for (room in floorFallsHoleIssuedRooms) {
            val issue = "Событие \"${BuildingEvent.FLOOR_FALL.title}\" в комнате в которой уж есть дыра в полу: ${fullAddress(room)}"
            addIssue(BuildingIssuesType.EVENTS, issue)
        }

        val poisonGasIssuedRooms = rooms
            .filter { BuildingEvent.POISON_GAS in it.events }
            .filter { r -> r.passages.any { it.vent != null } }
        for (room in poisonGasIssuedRooms) {
            val issue = "Событие \"${BuildingEvent.POISON_GAS.title}\" в комнате с вентиляцией: ${fullAddress(room)}"
            addIssue(BuildingIssuesType.EVENTS, issue)
        }

        val engineerEpiphanyIssuedRooms = rooms
            .filter { BuildingEvent.ENGINEER_EPIPHANY in it.events }
            .filter { r -> r.devices.any { it is BuildingDevice.EnergyNode } }
        for (room in engineerEpiphanyIssuedRooms) {
            val issue = "Событие \"${BuildingEvent.ENGINEER_EPIPHANY.title}\" в комнате где уже есть энергоузел: ${fullAddress(room)}"
            addIssue(BuildingIssuesType.EVENTS, issue)
        }
    }
}