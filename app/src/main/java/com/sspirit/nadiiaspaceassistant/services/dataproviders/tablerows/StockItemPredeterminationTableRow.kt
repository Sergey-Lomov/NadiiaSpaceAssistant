package com.sspirit.nadiiaspaceassistant.services.dataproviders.tablerows

import com.sspirit.nadiiaspaceassistant.models.items.QuantumStorage
import com.sspirit.nadiiaspaceassistant.models.items.StockItem
import com.sspirit.nadiiaspaceassistant.models.items.StockItemPredetermination
import com.sspirit.nadiiaspaceassistant.models.items.StockItemPredeterminationType
import com.sspirit.nadiiaspaceassistant.services.dataproviders.ItemDataProvider
import com.sspirit.nadiiaspaceassistant.utils.readDate
import com.sspirit.nadiiaspaceassistant.utils.readInt
import com.sspirit.nadiiaspaceassistant.utils.readString
import com.sspirit.nadiiaspaceassistant.utils.write
import java.time.LocalDate
import java.util.UUID
import kotlin.jvm.internal.Ref.IntRef

data class StockItemPredeterminationTableRow (
    val id: String,
    val type: String,
    val itemId: String,
    val amount: Int,
    val price: Int,
    val placeId: String,
    val fromDate: LocalDate,
    val toDate: LocalDate,
) : RawDataConvertibleTableRow {
    companion object {
        fun parse(
            raw: Array<Any>,
            ref: IntRef = IntRef()
        ): StockItemPredeterminationTableRow {
            return StockItemPredeterminationTableRow(
                id = raw.readString(ref),
                type = raw.readString(ref),
                itemId = raw.readString(ref),
                amount = raw.readInt(ref),
                price = raw.readInt(ref),
                placeId = raw.readString(ref),
                fromDate = raw.readDate(ref),
                toDate = raw.readDate(ref),
            )
        }

        fun from(source: StockItemPredetermination): StockItemPredeterminationTableRow =
            StockItemPredeterminationTableRow(
                id = source.id,
                type = source.type.string,
                itemId = source.item.descriptor.id,
                amount = source.item.amount,
                price = source.item.price,
                placeId = source.placeId,
                fromDate = source.period.start,
                toDate = source.period.endInclusive
            )
    }

    fun toStockItemPredetermination(): StockItemPredetermination? {
        val descriptor = ItemDataProvider.getDescriptor(itemId) ?: return null
        val stockItem = StockItem(descriptor, amount, price)
        return StockItemPredetermination(
            id = id,
            placeId = placeId,
            item = stockItem,
            type = StockItemPredeterminationType.byString(type),
            period = fromDate .. toDate
        )
    }

    override fun toRawData(): List<String> {
        val data = mutableListOf<String>()
        data.write(id)
        data.write(type)
        data.write(itemId)
        data.write(amount)
        data.write(price)
        data.write(placeId)
        data.write(fromDate)
        data.write(toDate)
        return data
    }
}