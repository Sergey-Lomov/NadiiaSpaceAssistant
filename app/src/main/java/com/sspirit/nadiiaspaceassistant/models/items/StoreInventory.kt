package com.sspirit.nadiiaspaceassistant.models.items

import java.time.LocalDate

data class StoreInventory (
    val stock: StockList,
    val available: MutableList<ItemDescriptor>,
    val preorders: MutableList<StockItemPredetermination>,
) {
    val expectedPreorders: List<StockItemPredetermination>
        get() = preorders.filter { it.period.start.isAfter(LocalDate.now()) }

    val currentPreorders: List<StockItemPredetermination>
        get() = preorders.filter { it.period.contains(LocalDate.now()) }

    val expiredPreorders: List<StockItemPredetermination>
        get() = preorders.filter { it.period.endInclusive.isBefore(LocalDate.now()) }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StoreInventory

        if (!stock.contentEquals(other.stock)) return false
        if (available != other.available) return false
        if (preorders != other.preorders) return false

        return true
    }

    override fun hashCode(): Int {
        var result = stock.contentHashCode()
        result = 31 * result + available.hashCode()
        result = 31 * result + preorders.hashCode()
        return result
    }
}