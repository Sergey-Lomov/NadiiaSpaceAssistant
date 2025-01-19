package com.sspirit.nadiiaspaceassistant.models.cosmology

import com.sspirit.nadiiaspaceassistant.utils.IdEquatableEntity

data class SpaceObject(
    override val id: String,
    val title: String,
    val info: String,
    val orbit: String,
    val initialAngle: Float,
    val orbitPeriod: Float,
    val parent: SpaceSystem,
    var pois: MutableList<SpacePOI> = mutableListOf()
) : IdEquatableEntity() {
    override fun equals(other: Any?): Boolean = super.equals(other)
    override fun hashCode(): Int = super.hashCode()
}