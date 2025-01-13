package com.sspirit.nadiiaspaceassistant.models.missions.building

data class BuildingSector(
    val title: String,
    val building: Building,
    val locations: MutableList<BuildingLocation> = mutableListOf(),
    val slabs: MutableMap<Float, Array<BuildingSlab>> = mutableMapOf(),
) {
    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + locations.hashCode()
        result = 31 * result + slabs.hashCode()
        return result
    }
}