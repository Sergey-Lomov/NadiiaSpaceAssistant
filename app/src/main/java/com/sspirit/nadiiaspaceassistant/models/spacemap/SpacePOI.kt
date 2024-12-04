package com.sspirit.nadiiaspaceassistant.models.spacemap

import kotlinx.serialization.Serializable

enum class SpacePOIKeys(val index: Int) {
    ID(0),
    PARENT(1),
    TITLE(2),
}

@Serializable
data class SpacePOI (
    val id: String,
    val title: String,
    val parent: SpaceObject
)