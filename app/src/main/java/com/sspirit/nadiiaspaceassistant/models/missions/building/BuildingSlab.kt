package com.sspirit.nadiiaspaceassistant.models.missions.building

data class BuildingSlab (
    val sector: BuildingSector,
    val material: BuildingMaterial,
    val realLocation: RealLifeLocation,
    val level: Float,
    var hasHole: Boolean,
) {
    companion object {
        fun outer(sector: BuildingSector, realLocation: RealLifeLocation, level: Float) : BuildingSlab {
            return BuildingSlab(
                sector = sector,
                material = BuildingMaterial.outer,
                realLocation = realLocation,
                level = level,
                hasHole = false
            )
        }
    }

    private val downLocation: BuildingLocation?
        get() = sector.locations.firstOrNull { it.ceilingLevel == this.level }

    private val upLocation: BuildingLocation?
        get() = sector.locations.firstOrNull { it.floorLevel == this.level }

    val downRoom: BuildingRoom?
        get() = downLocation?.rooms
            ?.firstOrNull { it.realLocation == this.realLocation }


    val upRoom: BuildingRoom?
        get() = upLocation?.rooms
            ?.firstOrNull { it.realLocation == this.realLocation }

    val downValidRoom: BuildingRoom?
        get() = downLocation?.validRooms
            ?.firstOrNull { it.realLocation == this.realLocation }

    val upValidRoom: BuildingRoom?
        get() = upLocation?.validRooms
            ?.firstOrNull { it.realLocation == this.realLocation }

    val isOuter: Boolean
        get() = upValidRoom == null || downValidRoom == null
}