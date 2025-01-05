package com.sspirit.nadiiaspaceassistant.services.dataproviders.tablerows.building.location

import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingLocation
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingSector
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingSlab
import com.sspirit.nadiiaspaceassistant.models.missions.building.RealLifeLocation
import com.sspirit.nadiiaspaceassistant.utils.readBoolean
import com.sspirit.nadiiaspaceassistant.utils.write
import kotlin.jvm.internal.Ref.IntRef

data class LocationTableRowFloor(
    val material: LocationTableRowMaterial,
    val hasHole: Boolean
) {
    companion object {
        fun parse(raw: Array<Any>, displacement: IntRef): LocationTableRowFloor {
            return LocationTableRowFloor(
                material = LocationTableRowMaterial.parse(raw, displacement),
                hasHole = raw.readBoolean(displacement),
            )
        }

        fun from(source: BuildingSlab) : LocationTableRowFloor =
            LocationTableRowFloor(
                material = LocationTableRowMaterial.from(source.material),
                hasHole = source.hasHole
            )
    }

    fun toBuildingSlab(sector: BuildingSector, realLocation: RealLifeLocation, level: Float) : BuildingSlab {
        return BuildingSlab(
            sector = sector,
            material = material.toBuildingMaterial(),
            realLocation = realLocation,
            level = level,
            hasHole = hasHole
        )
    }
}

fun MutableList<String>.write(floor: LocationTableRowFloor) {
    write(floor.material)
    write(floor.hasHole)
}