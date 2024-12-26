package com.sspirit.nadiiaspaceassistant.models.missions.building

data class BuildingWall (
    val location: BuildingLocation,
    val room1: BuildingRoom,
    val room2: BuildingRoom,
    val material: BuildingMaterial,
    val hasHole: Boolean
) {
    override fun hashCode(): Int {
        var result = location.id.hashCode()
        result = 31 * result + room1.hashCode()
        result = 31 * result + room2.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BuildingWall

        if (location != other.location) return false
        if (room1 != other.room1) return false
        if (room2 != other.room2) return false
        if (material != other.material) return false
        if (hasHole != other.hasHole) return false

        return true
    }
}