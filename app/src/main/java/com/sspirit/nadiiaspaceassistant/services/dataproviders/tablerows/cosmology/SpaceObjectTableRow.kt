package com.sspirit.nadiiaspaceassistant.services.dataproviders.tablerows.cosmology

import com.sspirit.nadiiaspaceassistant.models.cosmology.SpaceObject
import com.sspirit.nadiiaspaceassistant.models.cosmology.SpaceSectorMap
import com.sspirit.nadiiaspaceassistant.utils.readFloat
import com.sspirit.nadiiaspaceassistant.utils.readString
import kotlin.jvm.internal.Ref.IntRef

data class SpaceObjectTableRow (
    val id: String,
    val parentId: String,
    val title: String,
    val info: String,
    val orbit: String,
    val initialAngle: Float,
    val orbitPeriod: Float
) {
    companion object {
        fun parse(raw: Array<Any>, ref: IntRef = IntRef()): SpaceObjectTableRow {
            return SpaceObjectTableRow(
                id = raw.readString(ref),
                parentId = raw.readString(ref),
                title = raw.readString(ref),
                info =raw.readString(ref),
                orbit = raw.readString(ref),
                initialAngle = raw.readFloat(ref),
                orbitPeriod = raw.readFloat(ref),
            )
        }
    }

    fun toSpaceObject(sector: SpaceSectorMap): SpaceObject? {
        val parent = sector.systemBy(parentId) ?: return null
        val obj = SpaceObject(
            id = id,
            title = title,
            info = info,
            parent = parent,
            orbit = orbit,
            initialAngle = initialAngle,
            orbitPeriod = orbitPeriod
        )

        parent.objects.add(obj)
        return obj
    }
}