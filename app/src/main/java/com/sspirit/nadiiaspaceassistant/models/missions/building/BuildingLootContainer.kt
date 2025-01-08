package com.sspirit.nadiiaspaceassistant.models.missions.building

import com.sspirit.nadiiaspaceassistant.models.ItemsStorageNode
import com.sspirit.nadiiaspaceassistant.models.items.ItemDescriptor
import com.sspirit.nadiiaspaceassistant.models.items.LootGroup
import com.sspirit.nadiiaspaceassistant.models.items.QuantumStorage

data class BuildingLootContainer(
    val id: String,
    val room: BuildingRoom,
    val group: LootGroup,
    var quantumStorages: Array<QuantumStorage> = arrayOf(),
    var nodes: Array<ItemsStorageNode> = arrayOf(),
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BuildingLootContainer

        if (id != other.id) return false
        if (room != other.room) return false
        if (group != other.group) return false
        if (!quantumStorages.contentEquals(other.quantumStorages)) return false
        if (!nodes.contentEquals(other.nodes)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + room.hashCode()
        result = 31 * result + group.hashCode()
        result = 31 * result + quantumStorages.contentHashCode()
        result = 31 * result + nodes.contentHashCode()
        return result
    }

}