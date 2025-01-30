package com.sspirit.nadiiaspaceassistant.services.analyzers.building

import com.sspirit.nadiiaspaceassistant.models.missions.building.Building
import com.sspirit.nadiiaspaceassistant.models.missions.building.devices.BuildingDevice
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingDoorLock
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingEvent
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingMaterial
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingMaterialLucidity
import com.sspirit.nadiiaspaceassistant.models.missions.building.devices.BuildingDeviceType
import com.sspirit.nadiiaspaceassistant.models.missions.building.specloot.BuildingDoorCode
import com.sspirit.nadiiaspaceassistant.models.missions.building.specloot.BuildingDoorKeyCard
import com.sspirit.nadiiaspaceassistant.models.missions.building.specloot.BuildingDoorKeyCardColor
import com.sspirit.nadiiaspaceassistant.models.sellPrice
import com.sspirit.nadiiaspaceassistant.services.dataproviders.missions.propertyevacuation.RoomsDescriptorsDataProvider
import com.sspirit.nadiiaspaceassistant.ui.utils.fullAddress
import com.sspirit.nadiiaspaceassistant.utils.flatArrayMap

private const val smallStabilizerId = "69"
private const val bigStabilizerId = "70"

private val devicesLimits = mapOf(
    BuildingDeviceType.SAFETY_CONSOLE to 1,
    BuildingDeviceType.SUPPORT_CONSOLE to 1,
    BuildingDeviceType.ENERGY_NODE to 3,
    BuildingDeviceType.HOLO_PLAN to 1,
    BuildingDeviceType.ENERGY_CORE to 1,
    BuildingDeviceType.ACID_TANK to 1,
    BuildingDeviceType.MAINFRAME to 1,
    BuildingDeviceType.AUTO_DOCTOR to 1,
    BuildingDeviceType.UNDEFINED to 0,
)

private enum class LocksGroup(val title: String, val limit: Int) {
    REMOTE("Удаленные", 3),
    BIOMETRY("Биометрические", 3),
    RED_CARD("Красные карты", 3),
    BLUE_CARD("Синие карты", 3),
    GREEN_CARD("Зеленые карты", 3),
    CODE("Кодовые", 3),
    UNDEFINED("Неопределенные", 3);

    companion object {
        fun groupFor(lock: BuildingDoorLock): LocksGroup =
            when (lock) {
                BuildingDoorLock.Biometry -> BIOMETRY
                BuildingDoorLock.Card(BuildingDoorKeyCardColor.RED) -> RED_CARD
                BuildingDoorLock.Card(BuildingDoorKeyCardColor.BLUE) -> BLUE_CARD
                BuildingDoorLock.Card(BuildingDoorKeyCardColor.GREEN) -> GREEN_CARD
                is BuildingDoorLock.Code -> CODE
                BuildingDoorLock.Remote -> REMOTE
                else -> UNDEFINED
            }
    }
}

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

    private var currentIssuesType: BuildingIssuesType? = null
    private fun addError(issue: String) =
        currentIssuesType?.let { addError(it, issue) }

    private fun addWarning(issue: String) =
        currentIssuesType?.let { addWarning(it, issue) }

    private fun addError(type: BuildingIssuesType, issue: String) =
        report.addError(type, issue)

    private fun addWarning(type: BuildingIssuesType, issue: String) =
        report.addWarning(type, issue)

    private fun addFix(type: BuildingFixingType, data: BuildingFixingData) =
        report.addFix(type, data)

    private fun analyzeLoot() {
        val totalPrice = lootItems.sellPrice

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
                    val issue = "Недостижимая локация ${fullAddress(location)}"
                    addError(BuildingIssuesType.LOCATIONS, issue)
                }
            }
        }
    }

    private fun analyzeLocks() {
        currentIssuesType = BuildingIssuesType.LOCKS

        for (lock in locks) {
            when (lock) {
                BuildingDoorLock.Undefined -> addError("Обнаружен Undefined замок")

                BuildingDoorLock.Biometry -> continue

                BuildingDoorLock.Remote ->
                    if (!devices.any { it is BuildingDevice.SafetyConsole })
                        addError("Отсутствует устройство \"${BuildingDeviceType.SAFETY_CONSOLE.title}\"")

                is BuildingDoorLock.Card ->
                    if (lock.color !in cardsColors)
                        addError("Отсутствует карта доступа (${lock.color})")

                is BuildingDoorLock.Code ->
                    if (lock.code !in codes)
                        addError("Отсутствует код для замка (${lock.code})")
            }
        }

        for (location in locations) {
            location.passages
                .mapNotNull { it.door?.locks }
                .flatArrayMap { it }
                .groupBy { LocksGroup.groupFor(it) }
                .filter { it.value.size > it.key.limit }
                .forEach {
                    addError("В локации ${fullAddress(location)} превышено кол-во замков типа ${it.key.title}")
                }
        }
    }

    private fun analyzeSlabs() {
        val outerSlabs = validRoomsSlabs
            .filter { it.isOuter }
            .filter { it.material != BuildingMaterial.outer }
        for (slab in outerSlabs) {
            val issue = "Материал внешнего перекрытия не соответствует outer материалу: ${fullAddress(slab)}"
            addError(BuildingIssuesType.SLABS, issue)
            addFix(BuildingFixingType.OUTER_SLAB_MATERIAL, OuterSlabMaterialFixing(slab))
        }
    }

    private fun analyzeWalls() {
        val outerWalls = validRoomsWalls
            .filter { it.isOuter }
            .filter { it.material != BuildingMaterial.outer }
        for (wall in outerWalls) {
            val issue = "Материал внешней стены не соответствует outer материалу: ${fullAddress(wall)}"
            addError(BuildingIssuesType.WALLS, issue)
            addFix(BuildingFixingType.OUTER_WALL_MATERIAL, OuterWallMaterialFixing(wall))
        }
    }

    private fun analyzeDevices() {
        currentIssuesType = BuildingIssuesType.DEVICES

        if (devices.isEmpty())
            addWarning("На объекте нет устройств")

        for (room in rooms) {
            val descriptor = RoomsDescriptorsDataProvider.getFor(room) ?: continue
            for (device in room.devices) {
                if (device == BuildingDevice.Undefined) {
                    addError("Неопознанное устройство в комнате ${fullAddress(room)}")
                    continue
                }

                if (!device.validDetailsData)
                    addError("Детали устройства невалидны ${device.title} в комнате ${fullAddress(room)}")

                if (device.title !in descriptor.deviceTypes)
                    addWarning("Нерекомендованное устройство ${device.title} расположено в комнате ${room.type} (${fullAddress(room)})")
            }
        }

        for (location in locations) {
            for (deviceType in BuildingDeviceType.entries) {
                val limit = devicesLimits[deviceType]
                if (limit == null) {
                    addError("Не указан лимит устройств для $deviceType")
                    return
                }

                val count = location.rooms
                    .flatArrayMap { it.devices }
                    .count { it.type == deviceType }

                if (count > limit) {
                    addError("В локации ${fullAddress(location)} превышен лимит устройств $deviceType")
                }
            }
        }
    }

    private fun analyzeEvents() {
        currentIssuesType = BuildingIssuesType.EVENTS

        if (events.isEmpty())
            addWarning("На объекте нет событий")

        val floorFallsDownIssuedRooms = rooms
            .filter { BuildingEvent.FLOOR_FALL in it.events }
            .filter { it.floor.downValidRoom == null }
        for (room in floorFallsDownIssuedRooms)
            addError("Событие \"${BuildingEvent.FLOOR_FALL.title}\" в комнате без валидной комнаты снизу: ${fullAddress(room)}")

        val floorFallsHoleIssuedRooms = rooms
            .filter { BuildingEvent.FLOOR_FALL in it.events }
            .filter { it.floor.hasHole }
        for (room in floorFallsHoleIssuedRooms)
            addWarning("Событие \"${BuildingEvent.FLOOR_FALL.title}\" в комнате в которой уж есть дыра в полу: ${fullAddress(room)}")

        val poisonGasIssuedRooms = rooms
            .filter { BuildingEvent.POISON_GAS in it.events }
            .filter { r -> r.passages.any { it.vent != null } }
        for (room in poisonGasIssuedRooms)
            addWarning("Событие \"${BuildingEvent.POISON_GAS.title}\" в комнате с вентиляцией: ${fullAddress(room)}")

        val engineerEpiphanyIssuedRooms = rooms
            .filter { BuildingEvent.ENGINEER_EPIPHANY in it.events }
            .filter { r -> r.devices.any { it is BuildingDevice.EnergyNode } }
        for (room in engineerEpiphanyIssuedRooms)
            addWarning("Событие \"${BuildingEvent.ENGINEER_EPIPHANY.title}\" в комнате где уже есть энергоузел: ${fullAddress(room)}")

        for (location in locations) {
            val limit = devicesLimits[BuildingDeviceType.ENERGY_NODE] ?: continue
            val epiphanyCount = location.rooms
                .flatArrayMap { it.events }
                .count { it == BuildingEvent.ENGINEER_EPIPHANY }
            val energyNodesCount = location.rooms
                .flatArrayMap { it.devices }
                .count { it is BuildingDevice.EnergyNode }

            if (epiphanyCount + energyNodesCount > limit)
                addError("В локации ${fullAddress(location)} сумма энергоузлов и событий '${BuildingEvent.ENGINEER_EPIPHANY}' превышает лимит")
        }
    }
}