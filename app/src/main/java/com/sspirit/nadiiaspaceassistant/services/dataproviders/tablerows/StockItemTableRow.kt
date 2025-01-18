package com.sspirit.nadiiaspaceassistant.services.dataproviders.tablerows

import com.sspirit.nadiiaspaceassistant.models.items.StockItem
import com.sspirit.nadiiaspaceassistant.services.dataproviders.ItemDataProvider
import com.sspirit.nadiiaspaceassistant.utils.readInt
import com.sspirit.nadiiaspaceassistant.utils.readString
import com.sspirit.nadiiaspaceassistant.utils.write
import kotlin.jvm.internal.Ref.IntRef

data class StockItemTableRow(
    val id: String,
    val itemId: String,
    val amount: Int,
    val price: Int
) : RawDataConvertibleTableRow {
    companion object {
        fun parse(raw: Array<Any>, ref: IntRef = IntRef()): StockItemTableRow =
            StockItemTableRow(
                id = raw.readString(ref),
                itemId = raw.readString(ref),
                amount = raw.readInt(ref),
                price = raw.readInt(ref),
            )

        fun from(source: StockItem): StockItemTableRow =
            StockItemTableRow(
                id = source.id,
                itemId = source.descriptor.id,
                amount = source.amount,
                price = source.price
            )
    }

    fun toStockItem(): StockItem? {
        val descriptor = ItemDataProvider.getDescriptor(itemId) ?: return null
        return StockItem(
            id = id,
            descriptor = descriptor,
            amount = amount,
            price = price
        )
    }

    override fun toRawData(): List<String> {
        val data = mutableListOf<String>()
        data.write(id)
        data.write(itemId)
        data.write(amount)
        data.write(price)
        return data
    }
}