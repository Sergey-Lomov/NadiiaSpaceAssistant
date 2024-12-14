package com.sspirit.nadiiaspaceassistant.models.items

import android.util.Range
import com.sspirit.nadiiaspaceassistant.extensions.IndexConvertibleKey
import java.time.LocalDate

enum class StockListItemPredeterminationKeys(override val index: Int) : IndexConvertibleKey {
    ID(0),
    ITEM_ID(1),
    ITEM_AMOUNT(2),
    ITEM_PRICE(3),
    IS_PREORDER(4),
    PLACE_ID(5),
    FROM_DATE(6),
    TO_DATE(7),
}

data class StockListItemPredetermination(
    val id: String,
    val placeId: String,
    var item: StockListItem,
    val period: ClosedRange<LocalDate>,
)