package com.sspirit.nadiiaspaceassistant.models.missions.building

import com.google.common.base.Objects

data class BuildingSlab (
    val sector: BuildingSector,
    val material: BuildingMaterial,
    val realLocation: RealLifeLocation,
    val level: Float,
    val hasHole: Boolean,
) {
    companion object {
        fun outer(sector: BuildingSector, realLocation: RealLifeLocation, level: Float) : BuildingSlab {
            return BuildingSlab(
                sector = sector,
                material = OuterMaterial.material,
                realLocation = realLocation,
                level = level,
                hasHole = false
            )
        }
    }
}