package com.sspirit.nadiiaspaceassistant.models.spacemap

import com.sspirit.nadiiaspaceassistant.extensions.IndexConvertibleKey
import kotlinx.serialization.Serializable

enum class SpacePOIKeys(override val index: Int) : IndexConvertibleKey {
    ID(0),
    PARENT(1),
    TITLE(2),
    STATUS(3),
    NAV_LENGTH_MULT(4),
    NAV_TIME_MULT(5),
}

enum class SpacePOIStatus {
    AVAILABLE,
    UNAVAILABLE,
    HIDDEN,

    INVALID
}

@Serializable
data class SpacePOI (
    val id: String,
    val title: String,
    val parent: SpaceObject,
    val status: SpacePOIStatus,
    val navigationLengthMultiplier: Float,
    val navigationTimeMultiplier: Float,
)