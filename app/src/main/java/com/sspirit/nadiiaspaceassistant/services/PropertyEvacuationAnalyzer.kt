package com.sspirit.nadiiaspaceassistant.services

import com.sspirit.nadiiaspaceassistant.models.missions.PropertyEvacuation
import com.sspirit.nadiiaspaceassistant.models.missions.building.devices.BuildingDevice
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingDoorLock
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingEvent
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingLocation
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingMaterial
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingMaterialLucidity
import com.sspirit.nadiiaspaceassistant.models.missions.building.specloot.BuildingDoorCode
import com.sspirit.nadiiaspaceassistant.models.missions.building.specloot.BuildingDoorKeyCard
import com.sspirit.nadiiaspaceassistant.ui.utils.fullRoomAddress
import com.sspirit.nadiiaspaceassistant.ui.utils.fullSlabAddress

private const val smallStabilizerId = "69"
private const val bigStabilizerId = "70"

enum class BuildingMaterialHolder {
    WALL,
    DOOR,
    SLAB
}

data class MaterialAnalyzingReport(
    val lucidity: Map<BuildingMaterialLucidity, Float>,
    val heatImmune: Float,
    val acidImmune: Float,
    val explosionImmune: Float,
)

data class LootAnalyzingReport(
    var totalPrice: Int = 0,
    var bigStabilizers: Int = 0,
    var smallStabilizers: Int = 0,
)

data class PropertyEvacuationAnalyzingReport(
    var loot: LootAnalyzingReport = LootAnalyzingReport(),
    var materials: MutableMap<BuildingMaterialHolder, MaterialAnalyzingReport> = mutableMapOf(),
    var missedKeys: MutableList<String> = mutableListOf(),
    var unreachableLocations: MutableList<BuildingLocation> = mutableListOf(),
    var otherIssues: MutableList<String> = mutableListOf()
)

object PropertyEvacuationAnalyzer {
    fun analyze(mission: PropertyEvacuation) : PropertyEvacuationAnalyzingReport {
        val report = PropertyEvacuationAnalyzingReport()

        val sectors = mission.building.sectors
        val slabs = mission.building.sectors
            .flatMap { it.slabs.values }
            .flatMap { it.asIterable() }
        val locations = sectors
            .flatMap { it.locations }
        val rooms = locations.flatMap { it.rooms.asIterable() }

        // Search unreachable locations
        val reachable = mission.building.transports
            .flatMap { it.rooms.asIterable() }
            .distinctBy { it.location }
            .map { it.location }

        if (locations.size > reachable.size) {
            for (location in locations) {
                if (location !in reachable)
                    report.unreachableLocations.add(location)
            }
        }

        // Search missed keys
        val doors = locations
            .flatMap { it.passages.asIterable() }
            .mapNotNull { it.door }
        val locks = doors
            .flatMap { it.locks.asIterable() }
            .distinct()

        val specLoots = rooms.flatMap { it.specLoot.asIterable() }
        val cardsColors = specLoots.filterIsInstance<BuildingDoorKeyCard>().map { it.color }
        val codes = specLoots.filterIsInstance<BuildingDoorCode>().map { it.code }
        val devices = rooms.flatMap { it.devices.asIterable() }

        for (lock in locks) {
            when (lock) {
                BuildingDoorLock.Undefined -> {
                    report.otherIssues.add("Обнаружен Undefined замок")
                    continue
                }

                BuildingDoorLock.Biometry -> continue

                BuildingDoorLock.Remote -> {
                    if (!devices.any { it is BuildingDevice.SafetyConsole }) {
                        val stubConsole = BuildingDevice.SafetyConsole(false)
                        report.missedKeys.add("Отсутствует устройство \"${stubConsole.title}\"")
                        continue
                    }

                }

                is BuildingDoorLock.Card -> {
                    if (lock.color !in cardsColors) {
                        report.missedKeys.add("Отсутствует карта доступа (${lock.color})")
                        continue
                    }
                }

                is BuildingDoorLock.Code -> {
                    if (lock.code !in codes) {
                        report.missedKeys.add("Отсутствует код для замка (${lock.code})")
                        continue
                    }
                }
            }

        }

        // Loot analyze
        val lootContainers = rooms.flatMap { it.loot.asIterable() }
        val lootItems = lootContainers.flatMap { it.items.asIterable() }
        val totalPrice = lootItems
            .fold(0) { acc, lootItem -> acc + lootItem.item.sellPrice * lootItem.amount  }
        report.loot.totalPrice = totalPrice
        report.loot.bigStabilizers = lootItems.filter { it.item.id == bigStabilizerId }.size
        report.loot.smallStabilizers = lootItems.filter { it.item.id == smallStabilizerId }.size

        //Materials
        val slabsMaterials = mission.building.sectors
            .flatMap { it.slabs.asIterable() }
            .flatMap { it.value.asIterable() }
            .map { it.material }
        report.materials[BuildingMaterialHolder.SLAB] = analyzeMaterials(slabsMaterials)

        val wallsMaterials = locations
            .flatMap { it.walls.asIterable() }
            .map { it.material }
        report.materials[BuildingMaterialHolder.WALL] = analyzeMaterials(wallsMaterials)

        val doorsMaterials = doors
            .map { it.material }
        report.materials[BuildingMaterialHolder.DOOR] = analyzeMaterials(doorsMaterials)

        // Other issues
        if (devices.isEmpty())
            report.otherIssues.add("На объекте нет устройств")

        val events = rooms.flatMap { it.events.asIterable() }
        if (events.isEmpty())
            report.otherIssues.add("На объекте нет событий")

        // Structural issues
        val outerSlabs = slabs
            .filter { it.isOuter}
            .filter { it.material != BuildingMaterial.outer }
        for (slab in outerSlabs) {
            report.otherIssues.add("Материал внешнего перекрытия не соответствует outer материалу: ${fullSlabAddress(slab)}")
        }

        // Events issues
        val floorFallsDownIssuedRooms = rooms
            .filter { BuildingEvent.FLOOR_FALL in it.events }
            .filter { it.floor.downValidRoom == null }
        for (room in floorFallsDownIssuedRooms) {
            report.otherIssues.add("Событие \"${BuildingEvent.FLOOR_FALL.title}\" в комнате без валидной комнаты снизу: ${fullRoomAddress(room)}")
        }

        val floorFallsHoleIssuedRooms = rooms
            .filter { BuildingEvent.FLOOR_FALL in it.events }
            .filter { it.floor.hasHole }
        for (room in floorFallsHoleIssuedRooms) {
            report.otherIssues.add("Событие \"${BuildingEvent.FLOOR_FALL.title}\" в комнате в которой уж есть дыра в полу: ${fullRoomAddress(room)}")
        }

        val poisonGasIssuedRooms = rooms
            .filter { BuildingEvent.POISON_GAS in it.events }
            .filter { r -> r.passages.any { it.vent != null } }
        for (room in poisonGasIssuedRooms) {
            report.otherIssues.add("Событие \"${BuildingEvent.POISON_GAS.title}\" в комнате с вентиляцией: ${fullRoomAddress(room)}")
        }

        val engineerEpiphanyIssuedRooms = rooms
            .filter { BuildingEvent.ENGINEER_EPIPHANY in it.events }
            .filter { r -> r.devices.any { it is BuildingDevice.EnergyNode } }
        for (room in engineerEpiphanyIssuedRooms) {
            report.otherIssues.add("Событие \"${BuildingEvent.ENGINEER_EPIPHANY.title}\" в комнате где уже есть энергоузел: ${fullRoomAddress(room)}")
        }

        return report
    }

    private fun analyzeMaterials(materials: List<BuildingMaterial>) : MaterialAnalyzingReport {
        val total = materials.size.toFloat()
        val luciduty = BuildingMaterialLucidity.entries
            .associateWith { l -> materials.filter { it.lucidity == l } }
            .mapValues { it.value.size / total }
        val heat = materials.filter { it.heatImmune }.size / total
        val acid = materials.filter { it.acidImmune }.size / total
        val explosion = materials.filter { it.explosionImmune }.size / total

        return MaterialAnalyzingReport(
            lucidity = luciduty,
            heatImmune = heat,
            acidImmune = acid,
            explosionImmune = explosion
        )
    }
}