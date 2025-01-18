package com.sspirit.nadiiaspaceassistant.models.items

import java.time.LocalDate

enum class StockItemPredeterminationType(val string: String) {
    SEARCH_RESULT("Поиск"),
    PREORDER("Предзаказ"),
    UNDEFINED("Неопределено");

    companion object {
        fun byString(string: String): StockItemPredeterminationType {
            return StockItemPredeterminationType.entries.find { it.string == string } ?: UNDEFINED
        }
    }

    override fun toString(): String = string
}

data class StockItemPredetermination(
    val id: String,
    val type: StockItemPredeterminationType,
    val placeId: String,
    var item: StockItem,
    val period: ClosedRange<LocalDate>,
)