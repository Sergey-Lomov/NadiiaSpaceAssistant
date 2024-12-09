@file:Suppress("DEPRECATION")

package com.sspirit.nadiiaspaceassistant.services.dataproviders

import android.util.Log
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.ValueRange
import com.sspirit.nadiiaspaceassistant.extensions.getFloat
import com.sspirit.nadiiaspaceassistant.extensions.getString
import com.sspirit.nadiiaspaceassistant.models.cosmology.SpaceObject
import com.sspirit.nadiiaspaceassistant.models.cosmology.SpaceObjectKeys
import com.sspirit.nadiiaspaceassistant.models.cosmology.SpaceObjectKeys.PARENT
import com.sspirit.nadiiaspaceassistant.models.cosmology.SpacePOI
import com.sspirit.nadiiaspaceassistant.models.cosmology.SpacePOIKeys
import com.sspirit.nadiiaspaceassistant.models.cosmology.SpacePOIStatus
import com.sspirit.nadiiaspaceassistant.models.cosmology.SpaceSystem
import com.sspirit.nadiiaspaceassistant.models.cosmology.SpaceSystemKeys
import java.time.Duration
import java.time.LocalDateTime

// In scope of this file 'object' means space object

private val bigExplosionDate = LocalDateTime.of(2024, 11, 10,0,0)
private const val expirationHours = 2
private const val spaceMapSheetId = "1ho20Ap51LCX19HfurhMIk7T3G61LdA3Mh3EtnNgnPPY"
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

            val sheetsService: Sheets = getSheetsService()
            val starsResponse = sheetsService
                .spreadsheets()
                .values()
                .get(spaceMapSheetId, starsListRange)
                .execute()

            val objectsResponse = sheetsService
                .spreadsheets()
                .values()
                .get(spaceMapSheetId, objectsListRange)
                .execute()

            val poisResponse = sheetsService
                .spreadsheets()
                .values()
                .get(spaceMapSheetId, poiListRange)
                .execute()

            spaceMap = parseMap(starsResponse, objectsResponse, poisResponse)
            expirationDate = LocalDateTime.now().plusHours(expirationHours.toLong())
        }

    fun indicesOf(system: SpaceSystem): Array<Int> {
        return arrayOf(spaceMap.indexOf(system))
    }

    fun indicesOf(obj: SpaceObject): Array<Int> {
        val systemIndex = spaceMap.indexOf(obj.parent)
        val objectIndex = obj.parent.objects.indexOf(obj)
        return arrayOf(systemIndex, objectIndex)
    }

    fun indicesOf(poi: SpacePOI): Array<Int> {
        val systemIndex = spaceMap.indexOf(poi.parent.parent)
        val objectIndex = poi.parent.parent.objects.indexOf(poi.parent)
        val poiIndex = poi.parent.pois.indexOf(poi)
        return arrayOf(systemIndex, objectIndex, poiIndex)
    }

    fun currentPosition(obj: SpaceObject) : Float {
        val diff = Duration.between(bigExplosionDate, LocalDateTime.now()).toMinutes().toFloat()
        return (diff % obj.orbitPeriod) / obj.orbitPeriod * 360 + obj.initalAngle
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
    val status = when (raw.getString(SpacePOIKeys.STATUS)) {
        "Доступно" -> SpacePOIStatus.AVAILABLE
        "Недоступно" -> SpacePOIStatus.UNAVAILABLE
        "Скрыто" -> SpacePOIStatus.HIDDEN
        else -> SpacePOIStatus.INVALID
    }

    val rawSubtitle = raw.getString(SpacePOIKeys.SUBTITLE)
    return SpacePOI(
        id = raw.getString(SpacePOIKeys.ID),
        title = raw.getString(SpacePOIKeys.TITLE),
        subtitle = rawSubtitle.ifEmpty { null },
        parent = parent,
        status = status,
        navigationLengthMultiplier = raw.getFloat(SpacePOIKeys.NAV_LENGTH_MULT, 1.0f),
        navigationTimeMultiplier = raw.getFloat(SpacePOIKeys.NAV_TIME_MULT, 1.0f)
    )
}