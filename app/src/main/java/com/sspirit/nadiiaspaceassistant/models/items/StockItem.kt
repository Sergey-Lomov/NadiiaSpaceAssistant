package com.sspirit.nadiiaspaceassistant.models.items

import java.util.UUID

typealias StockList = Array<StockItem>

data class StockItem(
    val id: String,
    val descriptor: ItemDescriptor,
    var amount: Int,
    val price: Int,
) {
    constructor(descriptor: ItemDescriptor, amount: Int, price: Int) :
            this(UUID.randomUUID().toString(), descriptor, amount, price)
}