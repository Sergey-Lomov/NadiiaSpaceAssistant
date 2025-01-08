package com.sspirit.nadiiaspaceassistant.services.dataproviders.tablerows

import com.sspirit.nadiiaspaceassistant.models.ItemsStorageNode
import com.sspirit.nadiiaspaceassistant.models.items.QuantumStorage
import com.sspirit.nadiiaspaceassistant.services.dataproviders.ItemDataProvider
import com.sspirit.nadiiaspaceassistant.utils.readInt
import com.sspirit.nadiiaspaceassistant.utils.readString
import com.sspirit.nadiiaspaceassistant.utils.write
import kotlin.jvm.internal.Ref.IntRef

data class QuantumStorageTableRow(
    val id : String,
    val itemId: String,
    val amount: Int,
) {

    companion object {
        fun parse(raw: Array<Any>, ref: IntRef = IntRef()): QuantumStorageTableRow {
            return QuantumStorageTableRow(
                id = raw.readString(ref),
                itemId = raw.readString(ref),
                amount = raw.readInt(ref),
            )
        }

        fun from(source: QuantumStorage): Array<QuantumStorageTableRow> =
            source.nodes.map {
                QuantumStorageTableRow(
                    id = source.id.toString(),
                    itemId = it.item.id,
                    amount = it.amount,
                )
            }.toTypedArray()
    }

    fun toRawData(): List<String> {
        val data = mutableListOf<String>()
        data.write(id)
        data.write(itemId)
        data.write(amount)
        return data
    }
}

fun Array<QuantumStorageTableRow>.toQuantumStorages(): MutableList<QuantumStorage> =
    groupBy { it.id }
        .mapNotNull {
            val items = it.value.mapNotNull inner@{ row ->
                val item = ItemDataProvider.getDescriptor(row.itemId) ?: return@inner null
                ItemsStorageNode(item, row.amount)
            }
            QuantumStorage(
                id = it.key.toInt(),
                nodes = items.toTypedArray()
            )
        }
        .toMutableList()