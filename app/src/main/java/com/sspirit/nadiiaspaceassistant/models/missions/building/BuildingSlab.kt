package com.sspirit.nadiiaspaceassistant.models.missions.building

data class BuildingSlab (
    val sector: BuildingSector,
    val material: BuildingMaterial,
    val realLocation: RealLifeLocation,
    val level: Int,
    val hasHole: Boolean,
)