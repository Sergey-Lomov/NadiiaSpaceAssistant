package com.sspirit.nadiiaspaceassistant.models.missions.building

data class BuildingSector(
    val title: String,
    val building: Building,
    val locations: MutableList<BuildingLocation> = mutableListOf(),
    val slabs: MutableMap<Float, Array<BuildingSlab>> = mutableMapOf(),
)