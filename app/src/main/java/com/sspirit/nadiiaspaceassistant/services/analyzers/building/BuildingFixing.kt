package com.sspirit.nadiiaspaceassistant.services.analyzers.building

import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingSlab
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingWall

enum class BuildingFixingType {
    OUTER_SLAB_MATERIAL,
    OUTER_WALL_MATERIAL
}

interface BuildingFixingData

data class OuterWallMaterialFixing (
    val wall: BuildingWall
) : BuildingFixingData

data class OuterSlabMaterialFixing (
    val slab: BuildingSlab
) : BuildingFixingData