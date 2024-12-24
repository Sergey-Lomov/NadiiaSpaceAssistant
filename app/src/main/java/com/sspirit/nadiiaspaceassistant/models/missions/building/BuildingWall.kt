package com.sspirit.nadiiaspaceassistant.models.missions.building

data class BuildingWall (
    val location: BuildingLocation,
    val realLocation1: RealLifeLocation,
    val realLocation2: RealLifeLocation,
    val material: BuildingMaterial,
    val hasHole: Boolean
)