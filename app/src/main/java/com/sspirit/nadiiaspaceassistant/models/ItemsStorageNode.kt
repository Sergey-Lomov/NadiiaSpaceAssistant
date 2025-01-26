package com.sspirit.nadiiaspaceassistant.models

import com.sspirit.nadiiaspaceassistant.models.items.ItemDescriptor

data class ItemsStorageNode (
    val item: ItemDescriptor,
    var amount: Int
)

val Array<ItemsStorageNode>.sellPrice: Int
    get() = sumOf { (it.item.sellPrice ?: 0) * it.amount }

val Array<ItemsStorageNode>.containsUnsellable: Boolean
    get() = any { it.item.sellPrice == null }

val Collection<ItemsStorageNode>.sellPrice: Int
    get() = sumOf { (it.item.sellPrice ?: 0) * it.amount }

val Collection<ItemsStorageNode>.containsUnsellable: Boolean
    get() = any { it.item.sellPrice == null }

