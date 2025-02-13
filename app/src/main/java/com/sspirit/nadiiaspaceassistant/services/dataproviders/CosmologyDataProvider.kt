package com.sspirit.nadiiaspaceassistant.services.dataproviders

import com.sspirit.nadiiaspaceassistant.models.cosmology.SpaceObject
import com.sspirit.nadiiaspaceassistant.models.cosmology.SpaceSectorMap
import com.sspirit.nadiiaspaceassistant.services.dataproviders.tablerows.cosmology.SpaceObjectTableRow
import com.sspirit.nadiiaspaceassistant.services.dataproviders.tablerows.cosmology.SpacePOITableRow
import com.sspirit.nadiiaspaceassistant.services.dataproviders.tablerows.cosmology.SpaceSystemTableRow
import com.sspirit.nadiiaspaceassistant.services.dataproviders.tablerows.cosmology.toSpacePOIs
import com.sspirit.nadiiaspaceassistant.utils.plusHours
import java.time.Duration
import java.time.LocalDateTime

// In scope of this file 'object' means space object

private val bigExplosionDate = LocalDateTime.of(2024, 11, 10,0,0)
private const val expirationHours = 24
private const val spaceMapSpreadsheetId = "1ho20Ap51LCX19HfurhMIk7T3G61LdA3Mh3EtnNgnPPY"
private const val systemsListRange = "Systems!A2:F30"
private const val objectsListRange = "Objects!A2:Z50"
private const val poiListRange = "POI!A2:Z200"

object CosmologyDataProvider : GoogleSheetDataProvider() {
        var sectorMap = SpaceSectorMap.empty

        fun downloadSpaceMap(forced: Boolean = false) {
            if (expirationDate != null && !forced) {
                if (LocalDateTime.now() < expirationDate) {
                    return
                }
            }

            val systemsResponse = request(spaceMapSpreadsheetId, systemsListRange)
            val systemsRows = parseToArray(systemsResponse, "Space systems data invalid", SpaceSystemTableRow::parse)
            val systems = systemsRows.map { it.toSpaceSystem() }.toTypedArray()
            sectorMap = SpaceSectorMap(systems)

            val objectsResponse = request(spaceMapSpreadsheetId, objectsListRange)
            val objetsRows = parseToArray(objectsResponse, "Space objects data invalid", SpaceObjectTableRow::parse)
            objetsRows.forEach { it.toSpaceObject(sectorMap) }

            val poisResponse = request(spaceMapSpreadsheetId, poiListRange)
            val poisRows = parseToArray(poisResponse, "Space POIs data invalid", SpacePOITableRow::parse)
            poisRows.toSpacePOIs(sectorMap)

            expirationDate = LocalDateTime.now().plusHours(expirationHours)
        }

    fun currentPosition(obj: SpaceObject) : Float {
        if (obj.orbitPeriod.toInt() == 0) return obj.initialAngle
        val diff = Duration.between(bigExplosionDate, LocalDateTime.now()).toMinutes().toFloat()
        return (diff % obj.orbitPeriod) / obj.orbitPeriod * 360 + obj.initialAngle
    }
}