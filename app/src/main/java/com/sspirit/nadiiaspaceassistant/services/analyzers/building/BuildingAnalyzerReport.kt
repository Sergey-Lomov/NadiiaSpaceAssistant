package com.sspirit.nadiiaspaceassistant.services.analyzers.building

import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingMaterialLucidity

enum class BuildingIssuesType {
    LOCKS,
    LOCATIONS,
    SLABS,
    WALLS,
    DEVICES,
    EVENTS,
}

enum class BuildingMaterialHolder {
    WALL,
    DOOR,
    SLAB
}

data class BuildingMaterialsAnalyzingReport(
    val lucidity: Map<BuildingMaterialLucidity, Float>,
    val heatImmune: Float,
    val acidImmune: Float,
    val explosionImmune: Float,
)

data class BuildingLootAnalyzingReport(
    var totalPrice: Int = 0,
    var bigStabilizers: Int = 0,
    var smallStabilizers: Int = 0,
)

data class BuildingAnalyzingReport(
    var loot: BuildingLootAnalyzingReport = BuildingLootAnalyzingReport(),
    var materials: MutableMap<BuildingMaterialHolder, BuildingMaterialsAnalyzingReport> = mutableMapOf(),
    var warnings: MutableMap<BuildingIssuesType, MutableSet<String>> = mutableMapOf(),
    var errors: MutableMap<BuildingIssuesType, MutableSet<String>> = mutableMapOf(),
    var fixes: MutableMap<BuildingFixingType, MutableSet<BuildingFixingData>> = mutableMapOf(),
) {
    val hasErrors: Boolean
        get() = errors.isNotEmpty()

    val hasWarnings: Boolean
        get() = warnings.isNotEmpty()

    val hasFixes: Boolean
        get() = fixes.isNotEmpty()

    fun addError(type: BuildingIssuesType, issue: String) {
        if (errors[type] == null) errors[type] = mutableSetOf()
        errors[type]?.add(issue)
    }

    fun addWarning(type: BuildingIssuesType, issue: String) {
        if (warnings[type] == null) warnings[type] = mutableSetOf()
        warnings[type]?.add(issue)
    }

    fun addFix(type: BuildingFixingType, data: BuildingFixingData) {
        if (fixes[type] == null) fixes[type] = mutableSetOf()
        fixes[type]?.add(data)
    }
}