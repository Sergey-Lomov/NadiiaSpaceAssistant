package com.sspirit.nadiiaspaceassistant.viewmodels

import com.sspirit.nadiiaspaceassistant.models.items.QuantumStorage
import com.sspirit.nadiiaspaceassistant.screens.items.ui.QuantumStorageTool

data class QuantumStoragesViewModel(
    val tools: Array<QuantumStorageTool> = QuantumStorageTool.entries.toTypedArray(),
    val storagesProvider: () -> Iterable<QuantumStorage>,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as QuantumStoragesViewModel

        if (storagesProvider != other.storagesProvider) return false
        if (!tools.contentEquals(other.tools)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = storagesProvider.hashCode()
        result = 31 * result + tools.contentHashCode()
        return result
    }
}