package com.sspirit.nadiiaspaceassistant.models.spacemap

import com.sspirit.nadiiaspaceassistant.extensions.IndexConvertibleKey
import kotlinx.serialization.Serializable

enum class SpaceObjectKeys(override val index: Int) : IndexConvertibleKey {
    ID(0),
    PARENT(1),
    TITLE(2),
}

@Serializable
data class SpaceObject(
    val id: String,
    val title: String,
    val parent: SpaceSystem,
    var pois: Array<SpacePOI> = arrayOf()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SpaceObject

        if (id != other.id) return false
        if (title != other.title) return false
        if (parent != other.parent) return false
        if (!pois.contentEquals(other.pois)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + parent.hashCode()
        result = 31 * result + pois.contentHashCode()
        return result
    }
}