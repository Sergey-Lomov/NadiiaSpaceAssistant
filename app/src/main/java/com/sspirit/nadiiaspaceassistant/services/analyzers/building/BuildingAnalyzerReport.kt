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
    var issues: MutableMap<BuildingIssuesType, MutableSet<String>> = mutableMapOf(),
    var fixes: MutableMap<BuildingFixingType, MutableSet<BuildingFixingData>> = mutableMapOf(),
) {
    val hasIssues: Boolean
        get() = issues.isNotEmpty()

    val hasFixes: Boolean
        get() = fixes.isNotEmpty()

    fun addIssue(type: BuildingIssuesType, issue: String) {
        if (issues[type] == null) issues[type] = mutableSetOf()
        issues[type]?.add(issue)
    }

    fun addFix(type: BuildingFixingType, data: BuildingFixingData) {
        if (fixes[type] == null) fixes[type] = mutableSetOf()
        fixes[type]?.add(data)
    }
}