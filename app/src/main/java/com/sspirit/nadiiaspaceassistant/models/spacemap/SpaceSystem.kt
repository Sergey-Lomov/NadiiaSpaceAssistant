package com.sspirit.nadiiaspaceassistant.models.spacemap

import kotlinx.serialization.Serializable

enum class SpaceSystemKeys(val index: Int) {
    ID(0),
    TITLE(1)
}

@Serializable
data class SpaceSystem(
    val id: String,
    val title: String,
    var objects: Array<SpaceObject> = arrayOf()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SpaceSystem

        if (id != other.id) return false
        if (title != other.title) return false
        if (!objects.contentEquals(other.objects)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + objects.contentHashCode()
        return result
    }
}