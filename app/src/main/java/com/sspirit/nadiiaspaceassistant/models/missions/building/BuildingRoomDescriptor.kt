package com.sspirit.nadiiaspaceassistant.models.missions.building

import com.sspirit.nadiiaspaceassistant.models.items.LootGroup

data class BuildingRoomDescriptor(
    val type: String,
    val description: String,
    val deviceTypes: Array<String>,
    val loot: MutableList<LootGroup> = mutableListOf(),
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BuildingRoomDescriptor

        if (type != other.type) return false
        if (description != other.description) return false
        if (!deviceTypes.contentEquals(other.deviceTypes)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + deviceTypes.contentHashCode()
        return result
    }
}