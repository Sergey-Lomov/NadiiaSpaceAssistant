package com.sspirit.nadiiaspaceassistant.models.cosmology

import com.sspirit.nadiiaspaceassistant.utils.IdEquatableEntity

private const val OUTER_OBJECTS_ORBIT = "OUTER"

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
    val isOuter: Boolean
        get() = orbit == OUTER_OBJECTS_ORBIT

    override fun equals(other: Any?): Boolean = super.equals(other)
    override fun hashCode(): Int = super.hashCode()
}