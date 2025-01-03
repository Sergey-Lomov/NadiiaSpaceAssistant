package com.sspirit.nadiiaspaceassistant.services

import com.sspirit.nadiiaspaceassistant.models.missions.PropertyEvacuation
import com.sspirit.nadiiaspaceassistant.models.missions.building.devices.BuildingDevice
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingDoorLock
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingLocation
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingMaterial
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingMaterialLucidity
import com.sspirit.nadiiaspaceassistant.models.missions.building.specloot.BuildingDoorCode
import com.sspirit.nadiiaspaceassistant.models.missions.building.specloot.BuildingDoorKeyCard

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

        // Search unreachable locations
        val locations = mission.building.sectors
            .flatMap { it.locations }
        val rooms = locations.flatMap { it.rooms.asIterable() }
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
        val loot = rooms
            .flatMap { it.loot.asIterable() }
        val totalPrice = loot.fold(0) { acc, instance -> acc + instance.item.sellPrice * instance.amount  }
        report.loot.totalPrice = totalPrice
        report.loot.bigStabilizers = loot.filter { it.item.id == bigStabilizerId }.size
        report.loot.smallStabilizers = loot.filter { it.item.id == smallStabilizerId }.size

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