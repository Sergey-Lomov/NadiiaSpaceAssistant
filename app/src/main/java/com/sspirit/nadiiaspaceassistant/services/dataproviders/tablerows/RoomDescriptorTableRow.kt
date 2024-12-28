package com.sspirit.nadiiaspaceassistant.services.dataproviders.tablerows

import com.sspirit.nadiiaspaceassistant.utils.readSplittedString
import com.sspirit.nadiiaspaceassistant.utils.readString
import kotlin.jvm.internal.Ref.IntRef

data class RoomDescriptorTableRow(
    val type: String,
    val description: String,
    val devices: Array<String>,
    val lootId: String,
) {
    companion object {
        fun parse(raw: Array<Any>): RoomDescriptorTableRow {
            val iterator = IntRef()
            return RoomDescriptorTableRow(
                type = raw.readString(iterator),
                description = raw.readString(iterator),
                devices = raw.readSplittedString(iterator),
                lootId = raw.readString(iterator),
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RoomDescriptorTableRow

        if (type != other.type) return false
        if (description != other.description) return false
        if (!devices.contentEquals(other.devices)) return false
        if (lootId != other.lootId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + devices.contentHashCode()
        result = 31 * result + lootId.hashCode()
        return result
    }
}