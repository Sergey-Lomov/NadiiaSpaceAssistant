package com.sspirit.nadiiaspaceassistant.services.dataproviders.tablerows.cosmology

import com.sspirit.nadiiaspaceassistant.models.cosmology.SpacePOI
import com.sspirit.nadiiaspaceassistant.models.cosmology.SpacePOIOffice
import com.sspirit.nadiiaspaceassistant.models.cosmology.SpacePOIPlace
import com.sspirit.nadiiaspaceassistant.models.cosmology.SpacePOIPlaceType
import com.sspirit.nadiiaspaceassistant.models.cosmology.SpacePOIAccessStatus
import com.sspirit.nadiiaspaceassistant.models.cosmology.SpaceSectorMap
import com.sspirit.nadiiaspaceassistant.utils.ignore
import com.sspirit.nadiiaspaceassistant.utils.readBoolean
import com.sspirit.nadiiaspaceassistant.utils.readFloat
import com.sspirit.nadiiaspaceassistant.utils.readSplitString
import com.sspirit.nadiiaspaceassistant.utils.readString
import kotlin.jvm.internal.Ref.IntRef

data class SpacePOITableRow (
    val id: String,
    val parentId: String,
    val title: String,
    val subtitle: String,
    val info: String,
    val isLandable: Boolean,
    val accessStatus: String,
    val navLength: Float,
    val navTime: Float,
    val visitRequirements: String,
    val connectedPOIs: Array<String>,
    val places: Array<String>,
    val offices: Array<String>,
) {
    companion object {
        fun parse(raw: Array<Any>, ref: IntRef = IntRef()): SpacePOITableRow {
            return SpacePOITableRow(
                id = raw.readString(ref),
                parentId = raw.readString(ref),
                title = raw.ignore(ref,1).readString(ref),
                subtitle = raw.readString(ref),
                info = raw.readString(ref),
                isLandable = raw.readBoolean(ref),
                navLength = raw.readFloat(ref),
                navTime = raw.readFloat(ref),
                accessStatus = raw.ignore(ref,1).readString(ref),
                visitRequirements = raw.readString(ref),
                connectedPOIs = raw.readSplitString(ref),
                places = raw.readSplitString(ref),
                offices = raw.readSplitString(ref),
            )
        }
    }

    fun toSpacePOI(sector: SpaceSectorMap): SpacePOI? {
        val parent = sector.objectBy(parentId) ?: return null
        val offices = offices
            .map { SpacePOIOffice.byString(it) }
            .toTypedArray()

        val poi = SpacePOI(
            id = id,
            title = title,
            subtitle = subtitle,
            info = info,
            visitRequirements = visitRequirements,
            parent = parent,
            isLandable = isLandable,
            accessStatus = SpacePOIAccessStatus.byString(accessStatus),
            navigationLengthMultiplier = navLength,
            navigationTimeMultiplier = navTime,
            offices = offices
        )

        parent.pois.add(poi)
        poi.places = places
            .map { SpacePOIPlace(poi, SpacePOIPlaceType.byString(it)) }
            .toTypedArray()

        return poi
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SpacePOITableRow

        return id == other.id
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + parentId.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + subtitle.hashCode()
        result = 31 * result + info.hashCode()
        result = 31 * result + accessStatus.hashCode()
        result = 31 * result + navLength.hashCode()
        result = 31 * result + navTime.hashCode()
        result = 31 * result + visitRequirements.hashCode()
        result = 31 * result + places.contentHashCode()
        result = 31 * result + offices.contentHashCode()
        result = 31 * result + connectedPOIs.contentHashCode()
        return result
    }
}

fun Array<SpacePOITableRow>.toSpacePOIs(sector: SpaceSectorMap): Array<SpacePOI> {
    val connectedIds = associate { it.toSpacePOI(sector) to it.connectedPOIs }
    val pois = connectedIds.keys.filterNotNull()
    pois.forEach { poi ->
        val ids = connectedIds[poi] ?: arrayOf()
        poi.connectedPOIs = pois.filter { it.id in ids}.toTypedArray()
    }

    return pois.toTypedArray()
}