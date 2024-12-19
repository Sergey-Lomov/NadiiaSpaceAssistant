@file:Suppress("DEPRECATION")

package com.sspirit.nadiiaspaceassistant.services.dataproviders

import android.util.Log
import android.widget.Space
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.ValueRange
import com.sspirit.nadiiaspaceassistant.extensions.getFloat
import com.sspirit.nadiiaspaceassistant.extensions.getSplitedString
import com.sspirit.nadiiaspaceassistant.extensions.getString
import com.sspirit.nadiiaspaceassistant.models.cosmology.SpaceObject
import com.sspirit.nadiiaspaceassistant.models.cosmology.SpaceObjectKeys
import com.sspirit.nadiiaspaceassistant.models.cosmology.SpaceObjectKeys.PARENT
import com.sspirit.nadiiaspaceassistant.models.cosmology.SpacePOI
import com.sspirit.nadiiaspaceassistant.models.cosmology.SpacePOIKeys
import com.sspirit.nadiiaspaceassistant.models.cosmology.SpacePOIOffice
import com.sspirit.nadiiaspaceassistant.models.cosmology.SpacePOIPlace
import com.sspirit.nadiiaspaceassistant.models.cosmology.SpacePOIPlaceType
import com.sspirit.nadiiaspaceassistant.models.cosmology.SpacePOIStatus
import com.sspirit.nadiiaspaceassistant.models.cosmology.SpaceSystem
import com.sspirit.nadiiaspaceassistant.models.cosmology.SpaceSystemKeys
import java.time.Duration
import java.time.LocalDateTime

// In scope of this file 'object' means space object

private val bigExplosionDate = LocalDateTime.of(2024, 11, 10,0,0)
private const val expirationHours = 24
private const val spaceMapSpreadsheetId = "1ho20Ap51LCX19HfurhMIk7T3G61LdA3Mh3EtnNgnPPY"
private const val starsListRange = "Systems!A2:F30"
private const val objectsListRange = "Objects!A2:Z50"
private const val poiListRange = "POI!A2:Z200"

object CosmologyDataProvider : GoogleSheetDataProvider() {
        var spaceMap: Array<SpaceSystem> = arrayOf()

        fun getSpaceMap(forced: Boolean = false) {
            if (expirationDate != null && !forced) {
                if (LocalDateTime.now() < expirationDate) {
                    return
                }
            }

            val starsResponse = service
                .spreadsheets()
                .values()
                .get(spaceMapSpreadsheetId, starsListRange)
                .execute()

            val objectsResponse = service
                .spreadsheets()
                .values()
                .get(spaceMapSpreadsheetId, objectsListRange)
                .execute()

            val poisResponse = service
                .spreadsheets()
                .values()
                .get(spaceMapSpreadsheetId, poiListRange)
                .execute()

            spaceMap = parseMap(starsResponse, objectsResponse, poisResponse)
            expirationDate = LocalDateTime.now().plusHours(expirationHours.toLong())
        }

    fun indicesOf(system: SpaceSystem): Array<Int> {
        return arrayOf(spaceMap.indexOf(system))
    }

    fun indicesOf(obj: SpaceObject): Array<Int> {
        val index = obj.parent.objects.indexOf(obj)
        return indicesOf(obj.parent).plus(index)
    }

    fun indicesOf(poi: SpacePOI): Array<Int> {
        val index = poi.parent.pois.indexOf(poi)
        return indicesOf(poi.parent).plus(index)
    }

    fun indicesOf(place: SpacePOIPlace): Array<Int> {
        val index = place.parent.places.indexOf(place)
        return indicesOf(place.parent).plus(index)
    }

    fun currentPosition(obj: SpaceObject) : Float {
        val diff = Duration.between(bigExplosionDate, LocalDateTime.now()).toMinutes().toFloat()
        return (diff % obj.orbitPeriod) / obj.orbitPeriod * 360 + obj.initalAngle
    }

    fun filteredPOI(filter: (SpacePOI) -> Boolean): Array<SpacePOI> {
        val result = mutableListOf<SpacePOI>()
        for (system in spaceMap)
            for (obj in system.objects)
                for (poi in obj.pois)
                    if (filter(poi))
                        result.add(poi)

        return result.toTypedArray()
    }
}

private fun parseMap(
    systemsResponse: ValueRange,
    objectsResponse: ValueRange,
    poisResponse: ValueRange,
) : Array<SpaceSystem> {
    val rawSystems = systemsResponse.getValues()?.map { it.toTypedArray() }?.toTypedArray()
    val rawObjects = objectsResponse.getValues()?.map { it.toTypedArray() }?.toMutableList()
    val rawPOIs = poisResponse.getValues()?.map { it.toTypedArray() }?.toMutableList()
    val updatedMap = mutableListOf<SpaceSystem>()

    try {
        if (rawSystems != null && rawObjects != null && rawPOIs != null) {
            for (rawSystem in rawSystems) {
                val system = SpaceSystem(
                    id = rawSystem.getString(SpaceSystemKeys.ID),
                    title = rawSystem.getString(SpaceSystemKeys.TITLE),
                    info = rawSystem.getString(SpaceSystemKeys.INFO),
                )
                handleObjects(system, rawObjects, rawPOIs)
                updatedMap.add(system)
            }
        }
    } catch (e: Exception) {
        Log.e(logTag, "Space map data invalid: ${e.toString()}")
    }

    return updatedMap.toTypedArray()
}

private fun handleObjects(system: SpaceSystem, rawObjects: MutableList<Array<Any>>, rawPOIs:  MutableList<Array<Any>>) {
    val systemObjects = mutableListOf<SpaceObject>()
    val handledObjectsIndices = mutableListOf<Int>()
    for (i: Int in rawObjects.indices) {
        val rawObject = rawObjects[i]
        val parent = rawObject.getString(PARENT)
        if (parent != system.id) {
            continue
        }

        val spaceObject = SpaceObject(
            id = rawObject.getString(SpaceObjectKeys.ID),
            title = rawObject.getString(SpaceObjectKeys.TITLE),
            info = rawObject.getString(SpaceObjectKeys.INFO),
            parent = system,
            orbit = rawObject.getString(SpaceObjectKeys.ORBIT),
            initalAngle = rawObject.getFloat(SpaceObjectKeys.INITIAL_ANGLE),
            orbitPeriod = rawObject.getFloat(SpaceObjectKeys.ORBIT_PERIOD)
        )

        handlePOIs(spaceObject, rawPOIs)

        systemObjects.add(spaceObject)
        handledObjectsIndices.add(i)
    }

    for (i in handledObjectsIndices.reversed()) {
        rawObjects.removeAt(i)
    }

    system.objects = systemObjects.toTypedArray()
}

private fun handlePOIs(spaceObject: SpaceObject, rawPOIs: MutableList<Array<Any>>) {
    val objectPOIs = mutableListOf<SpacePOI>()
    val handledPOIsIndices = mutableListOf<Int>()
    for (i: Int in rawPOIs.indices) {
        val rawPOI = rawPOIs[i]
        val parent = rawPOI.getString(SpacePOIKeys.PARENT)
        if (parent != spaceObject.id) {
            continue
        }

        val poi = parsePOI(rawPOI, spaceObject)
        objectPOIs.add(poi)
        handledPOIsIndices.add(i)
    }

    for (i in handledPOIsIndices.reversed()) {
        rawPOIs.removeAt(i)
    }

    spaceObject.pois = objectPOIs.toTypedArray()
}

private fun parsePOI(raw: Array<Any>, parent: SpaceObject) : SpacePOI {
    val status = SpacePOIStatus.byString(raw.getString(SpacePOIKeys.STATUS))
    val rawSubtitle = raw.getString(SpacePOIKeys.SUBTITLE)
    val rawOffices = raw.getSplitedString(SpacePOIKeys.OFFICES, ",")
    val offices = rawOffices.map { SpacePOIOffice.byString(it) }

    val poi = SpacePOI(
        id = raw.getString(SpacePOIKeys.ID),
        title = raw.getString(SpacePOIKeys.TITLE),
        subtitle = rawSubtitle.ifEmpty { null },
        info = raw.getString(SpacePOIKeys.INFO),
        visitRequirements = raw.getString(SpacePOIKeys.VISIT_REQUIREMENTS),
        parent = parent,
        offices = offices.toTypedArray(),
        status = status,
        navigationLengthMultiplier = raw.getFloat(SpacePOIKeys.NAV_LENGTH_MULT, 1.0f),
        navigationTimeMultiplier = raw.getFloat(SpacePOIKeys.NAV_TIME_MULT, 1.0f)
    )

    val rawPlaces = raw.getSplitedString(SpacePOIKeys.PLACES, ",")
    val places = rawPlaces.map {
        SpacePOIPlace(
            parent = poi,
            type = SpacePOIPlaceType.byString(it)
        )
    }
    poi.places = places.toTypedArray()

    return poi
}