package com.sspirit.nadiiaspaceassistant.models.items

import com.sspirit.nadiiaspaceassistant.extensions.IndexConvertibleKey

typealias StockList = Array<StockListItem>

enum class StockListItemKeys(override val index: Int) : IndexConvertibleKey {
    ID(0),
    AMOUNT(1),
    PRICE(2),
    IS_PREORDER(3),
}

data class StockListItem(
    val descriptor: ItemDescriptor,
    var amount: Int,
    val price: Int,
    val isPreOrder: Boolean = false
)