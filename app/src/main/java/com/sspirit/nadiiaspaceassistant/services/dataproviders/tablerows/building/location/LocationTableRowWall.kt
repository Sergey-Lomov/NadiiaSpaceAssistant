package com.sspirit.nadiiaspaceassistant.services.dataproviders.tablerows.building.location

import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingLocation
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingRoom
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingSector
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingSlab
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingWall
import com.sspirit.nadiiaspaceassistant.models.missions.building.RealLifeLocation
import com.sspirit.nadiiaspaceassistant.utils.readBoolean
import com.sspirit.nadiiaspaceassistant.utils.write
import kotlin.jvm.internal.Ref.IntRef

data class LocationTableRowWall(
    val material: LocationTableRowMaterial,
    val hasHole: Boolean
) {
    companion object {
        fun parse(raw: Array<Any>, displacement: IntRef): LocationTableRowWall {
            return LocationTableRowWall(
                material = LocationTableRowMaterial.parse(raw, displacement),
                hasHole = raw.readBoolean(displacement),
            )
        }

        fun from(source: BuildingWall) : LocationTableRowWall =
            LocationTableRowWall(
                material = LocationTableRowMaterial.from(source.material),
                hasHole = source.hasHole
            )
    }

    fun toBuildingWall(r1: BuildingRoom, r2: BuildingRoom) : BuildingWall {
        return BuildingWall(
            location = r1.location,
            room1 = r1,
            room2 = r2,
            material = material.toBuildingMaterial(),
            hasHole = hasHole
        )
    }
}

fun MutableList<String>.write(wall: LocationTableRowWall) {
    write(wall.material)
    write(wall.hasHole)
}
