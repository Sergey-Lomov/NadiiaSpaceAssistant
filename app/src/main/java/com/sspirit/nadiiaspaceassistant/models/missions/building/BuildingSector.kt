package com.sspirit.nadiiaspaceassistant.models.missions.building

data class BuildingSector(
    val title: String,
    val locations: MutableList<BuildingLocation> = mutableListOf(),
    val slabs: MutableList<Array<BuildingSlab>> = mutableListOf(),
)