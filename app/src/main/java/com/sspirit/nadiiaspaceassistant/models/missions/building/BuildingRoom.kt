package com.sspirit.nadiiaspaceassistant.models.missions.building

import com.sspirit.nadiiaspaceassistant.models.missions.building.specloot.SpecialLoot

data class BuildingRoom (
    val type: String,
    val location: BuildingLocation,
    val realLocation: RealLifeLocation,
    val light: Boolean,
    val loot: Array<LootGroupInstance>,
    val specLoot: Array<SpecialLoot>,
    val devices: Array<String>,
    val events: Array<String>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BuildingRoom

        if (type != other.type) return false
        if (location != other.location) return false
        if (realLocation != other.realLocation) return false
        if (light != other.light) return false
        if (!loot.contentEquals(other.loot)) return false
        if (!specLoot.contentEquals(other.specLoot)) return false
        if (!devices.contentEquals(other.devices)) return false
        if (!events.contentEquals(other.events)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + location.hashCode()
        result = 31 * result + realLocation.hashCode()
        result = 31 * result + light.hashCode()
        result = 31 * result + loot.contentHashCode()
        result = 31 * result + specLoot.contentHashCode()
        result = 31 * result + devices.contentHashCode()
        result = 31 * result + events.contentHashCode()
        return result
    }
}