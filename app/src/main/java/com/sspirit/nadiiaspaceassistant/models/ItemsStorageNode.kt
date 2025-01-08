package com.sspirit.nadiiaspaceassistant.models

import com.sspirit.nadiiaspaceassistant.models.items.ItemDescriptor

data class ItemsStorageNode (
    val item: ItemDescriptor,
    var amount: Int
)

