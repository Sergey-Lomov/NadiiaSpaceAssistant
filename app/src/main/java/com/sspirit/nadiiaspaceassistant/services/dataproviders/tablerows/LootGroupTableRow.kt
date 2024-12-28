package com.sspirit.nadiiaspaceassistant.services.dataproviders.tablerows

import com.sspirit.nadiiaspaceassistant.utils.readFloat
import com.sspirit.nadiiaspaceassistant.utils.readInt
import com.sspirit.nadiiaspaceassistant.utils.readString
import kotlin.jvm.internal.Ref.IntRef

data class LootGroupTableRow(
    val id: String,
    val title: String,
    val description: String,
    val lock: String,
    val itemId: String,
    val minAmount: Int,
    val maxAmount: Int,
    val weight: Float,
) {
    companion object {
        fun parse(raw: Array<Any>, ref: IntRef = IntRef()): LootGroupTableRow {
            return LootGroupTableRow(
                id = raw.readString(ref),
                title = raw.readString(ref),
                description = raw.readString(ref),
                lock = raw.readString(ref),
                itemId = raw.readString(ref),
                minAmount = raw.readInt(ref),
                maxAmount = raw.readInt(ref),
                weight = raw.readFloat(ref),
            )
        }
    }
}