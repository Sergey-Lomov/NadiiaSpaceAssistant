package com.sspirit.nadiiaspaceassistant.viewmodels

import com.sspirit.nadiiaspaceassistant.models.items.ItemDescriptor

data class ItemSelectorViewModel<T>(
    val unavailable: Array<ItemDescriptor> = arrayOf(),
    val onSelect: (ItemDescriptor) -> Unit,
    val grouper: ((ItemDescriptor) -> T)?,
    val order: (Array<T>)?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ItemSelectorViewModel<*>

        if (!unavailable.contentEquals(other.unavailable)) return false
        if (onSelect != other.onSelect) return false
        if (grouper != other.grouper) return false
        if (!order.contentEquals(other.order)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = unavailable.contentHashCode()
        result = 31 * result + onSelect.hashCode()
        result = 31 * result + grouper.hashCode()
        result = 31 * result + order.contentHashCode()
        return result
    }
}