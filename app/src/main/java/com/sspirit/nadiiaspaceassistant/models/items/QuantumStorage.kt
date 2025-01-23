package com.sspirit.nadiiaspaceassistant.models.items

import com.sspirit.nadiiaspaceassistant.models.ItemsStorageNode

data class QuantumStorage(
    val id: Int,
    var nodes: MutableList<ItemsStorageNode> = mutableListOf()
) {
    val sellPrice: Int
        get() = nodes.sumOf { it.item.sellPrice }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as QuantumStorage
        return id == other.id
    }

    override fun hashCode() = id.hashCode()
}