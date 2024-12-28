package com.sspirit.nadiiaspaceassistant.services.dataproviders.tablerows.location

import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingLocation
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingMaterial
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingMaterialLucidity
import com.sspirit.nadiiaspaceassistant.utils.readBoolean
import com.sspirit.nadiiaspaceassistant.utils.readString
import com.sspirit.nadiiaspaceassistant.utils.write
import kotlin.jvm.internal.Ref.IntRef

data class LocationTableRowMaterial(
    val lucidity: String,
    val heatImmune: Boolean,
    val acidImmune: Boolean,
    val explosionImmune: Boolean,
) {
    companion object {
        fun parse(raw: Array<Any>, displacement: IntRef): LocationTableRowMaterial {
            return LocationTableRowMaterial(
                lucidity = raw.readString(displacement),
                heatImmune = raw.readBoolean(displacement),
                acidImmune = raw.readBoolean(displacement),
                explosionImmune = raw.readBoolean(displacement)
            )
        }

        fun from(source: BuildingMaterial) : LocationTableRowMaterial =
            LocationTableRowMaterial(
                lucidity = source.lucidity.string,
                heatImmune = source.heatImmune,
                acidImmune = source.acidImmune,
                explosionImmune = source.explosionImmune
            )
    }

    fun toBuildingMaterial(): BuildingMaterial =
        BuildingMaterial(
            lucidity = BuildingMaterialLucidity.byString(lucidity),
            heatImmune = heatImmune,
            acidImmune = acidImmune,
            explosionImmune = explosionImmune
        )
}

fun MutableList<String>.write(material: LocationTableRowMaterial) {
    write(material.lucidity)
    write(material.heatImmune)
    write(material.acidImmune)
    write(material.explosionImmune)
}