package com.sspirit.nadiiaspaceassistant.models.cosmology

import com.sspirit.nadiiaspaceassistant.utils.IdEquatableEntity

data class SpaceSystem(
    override val id: String,
    val title: String,
    val info: String,
    var objects: MutableList<SpaceObject> = mutableListOf()
) : IdEquatableEntity() {
    override fun equals(other: Any?): Boolean = super.equals(other)
    override fun hashCode(): Int = super.hashCode()
}