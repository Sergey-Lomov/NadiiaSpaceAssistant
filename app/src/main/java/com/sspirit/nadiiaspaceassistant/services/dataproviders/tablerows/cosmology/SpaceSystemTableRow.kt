package com.sspirit.nadiiaspaceassistant.services.dataproviders.tablerows.cosmology

import com.sspirit.nadiiaspaceassistant.models.cosmology.SpaceSystem
import com.sspirit.nadiiaspaceassistant.utils.readString
import kotlin.jvm.internal.Ref.IntRef

data class SpaceSystemTableRow (
    val id: String,
    val title: String,
    val info: String,
) {
    companion object {
        fun parse(raw: Array<Any>, ref: IntRef = IntRef()): SpaceSystemTableRow {
            return SpaceSystemTableRow(
                id = raw.readString(ref),
                title = raw.readString(ref),
                info =raw.readString(ref),
            )
        }
    }

    fun toSpaceSystem(): SpaceSystem  = SpaceSystem(id, title, info)
}