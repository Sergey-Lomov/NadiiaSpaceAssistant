package com.sspirit.nadiiaspaceassistant.models.items

import com.sspirit.nadiiaspaceassistant.models.ItemsStorageNode

data class QuantumStorage(
    val id: Int,
    var nodes: Array<ItemsStorageNode>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as QuantumStorage

        if (id != other.id) return false
        if (!nodes.contentEquals(other.nodes)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + nodes.contentHashCode()
        return result
    }
}