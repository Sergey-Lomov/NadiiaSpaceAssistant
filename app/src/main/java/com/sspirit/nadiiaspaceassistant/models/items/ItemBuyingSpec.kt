package com.sspirit.nadiiaspaceassistant.models.items

import android.util.Range

data class ItemBuyingSpec(
    val storesTypes: Array<ItemStoreType>,
    val amount: IntRange,
    val price: IntRange,
    val chance: Float,
    val reputationRequirement: Int,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ItemBuyingSpec

        if (!storesTypes.contentEquals(other.storesTypes)) return false
        if (amount != other.amount) return false
        if (price != other.price) return false
        if (chance != other.chance) return false
        if (reputationRequirement != other.reputationRequirement) return false

        return true
    }

    override fun hashCode(): Int {
        var result = storesTypes.contentHashCode()
        result = 31 * result + amount.hashCode()
        result = 31 * result + price.hashCode()
        result = 31 * result + chance.hashCode()
        result = 31 * result + reputationRequirement
        return result
    }
}