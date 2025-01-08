package com.sspirit.nadiiaspaceassistant.viewmodels

import com.sspirit.nadiiaspaceassistant.models.items.QuantumStorage

data class QuantumStoragesViewModel(
    val storages: Array<QuantumStorage>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as QuantumStoragesViewModel

        return storages.contentEquals(other.storages)
    }

    override fun hashCode(): Int {
        return storages.contentHashCode()
    }
}