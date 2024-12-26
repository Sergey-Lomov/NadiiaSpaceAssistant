package com.sspirit.nadiiaspaceassistant.services.dataproviders.tablerows

import com.sspirit.nadiiaspaceassistant.extensions.readString
import kotlin.jvm.internal.Ref.IntRef

data class TransportsTableRow(
    val id: String,
    val type: String,
    val locationId: String,
    val realLocation: String,
) {
    companion object {
        fun parse(raw: Array<Any>): TransportsTableRow {
            val iterator = IntRef()
            return TransportsTableRow(
                id = raw.readString(iterator),
                type = raw.readString(iterator),
                locationId = raw.readString(iterator),
                realLocation = raw.readString(iterator)
            )
        }
    }
}