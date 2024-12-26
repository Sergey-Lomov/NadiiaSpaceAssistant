package com.sspirit.nadiiaspaceassistant.models.missions.building

import com.sspirit.nadiiaspaceassistant.models.items.LootGroup

data class BuildingRoomDescriptor(
    val type: String,
    val description: String,
    val devices: Array<BuildingDevice>,
    val loot: MutableList<LootGroup> = mutableListOf(),
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BuildingRoomDescriptor

        if (type != other.type) return false
        if (description != other.description) return false
        if (!devices.contentEquals(other.devices)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + devices.contentHashCode()
        return result
    }
}