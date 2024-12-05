@file:Suppress("DEPRECATION")

package com.sspirit.nadiiaspaceassistant.services.dataproviders

import android.content.Context
import android.util.Log
import android.widget.Space
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential.*
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.api.services.sheets.v4.model.ValueRange
import com.sspirit.nadiiaspaceassistant.NadiiaSpaceApplication
import com.sspirit.nadiiaspaceassistant.R
import com.sspirit.nadiiaspaceassistant.extensions.getFloat
import com.sspirit.nadiiaspaceassistant.extensions.getString
import com.sspirit.nadiiaspaceassistant.models.spacemap.SpaceObject
import com.sspirit.nadiiaspaceassistant.models.spacemap.SpaceObjectKeys
import com.sspirit.nadiiaspaceassistant.models.spacemap.SpaceObjectKeys.PARENT
import com.sspirit.nadiiaspaceassistant.models.spacemap.SpacePOI
import com.sspirit.nadiiaspaceassistant.models.spacemap.SpacePOIKeys
import com.sspirit.nadiiaspaceassistant.models.spacemap.SpacePOIStatus
import com.sspirit.nadiiaspaceassistant.models.spacemap.SpaceSystem
import com.sspirit.nadiiaspaceassistant.models.spacemap.SpaceSystemKeys
import com.sspirit.nadiiaspaceassistant.models.spacemap.SpaceSystemKeys.*
import java.io.InputStream

// In scope of this file 'object' means space object

private const val spaceMapSheetId = "1ho20Ap51LCX19HfurhMIk7T3G61LdA3Mh3EtnNgnPPY"
private const val starsListRange = "Systems!A2:F30"
private const val objectsListRange = "Objects!A2:Z50"
private const val poiListRange = "POI!A2:Z200"

class CosmologyDataProvider {
    companion object {
        var spaceMap: Array<SpaceSystem> = arrayOf()

        fun updateSpaceMap() {
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
        }
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
                )
                handleObjects(system, rawObjects, rawPOIs)
                updatedMap.add(system)
            }
        }
    } catch (e: Exception) {
        Log.e("Data validation", "Space map data invalid: ${e.toString()}")
    }

    return updatedMap.toTypedArray()
}

private fun handleObjects(system: SpaceSystem, rawObjects: MutableList<Array<Any>>, rawPOIs:  MutableList<Array<Any>>) {
    val systemObjects = mutableListOf<SpaceObject>()
    val handledObjectsIndices = mutableListOf<Int>()
    for (i: Int in rawObjects.indices) {
        val rawObject = rawObjects[i]
        val parent = rawObject[PARENT.index].toString()
        if (parent != system.id) {
            continue
        }

        val spaceObject = SpaceObject(
            id = rawObject[SpaceObjectKeys.ID.index].toString(),
            title = rawObject[SpaceObjectKeys.TITLE.index].toString(),
            parent = system,
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
        val parent = rawPOI[SpacePOIKeys.PARENT.index].toString()
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

private fun getSheetsService(): Sheets {
    val context: Context = NadiiaSpaceApplication.getContext()
    val inputStream: InputStream = context.resources.openRawResource(R.raw.google_oauth)
    val credentials = fromStream(inputStream)
        .createScoped(listOf(SheetsScopes.SPREADSHEETS))

    val httpTransport = GoogleNetHttpTransport.newTrustedTransport()
    val jsonFactory: JsonFactory = GsonFactory.getDefaultInstance()

    return Sheets
        .Builder(httpTransport, jsonFactory, credentials)
        .setApplicationName("NadiiaSpaceAssistant")
        .build()
}

private fun poiStatus(stringValue: String) : SpacePOIStatus{

    return SpacePOIStatus.AVAILABLE
}

private fun parsePOI(raw: Array<Any>, parent: SpaceObject) : SpacePOI {
    val status = when (raw.getString(SpacePOIKeys.STATUS)) {
        "Доступно" -> SpacePOIStatus.AVAILABLE
        "Недоступно" -> SpacePOIStatus.UNAVAILABLE
        "Скрыто" -> SpacePOIStatus.HIDDEN
        else -> SpacePOIStatus.INVALID
    }

    return SpacePOI(
        id = raw.getString(SpacePOIKeys.ID),
        title = raw.getString(SpacePOIKeys.TITLE),
        parent = parent,
        status = status,
        navigationLengthMultiplier = raw.getFloat(SpacePOIKeys.NAV_LENGTH_MULT, 1.0f),
        navigationTimeMultiplier = raw.getFloat(SpacePOIKeys.NAV_TIME_MULT, 1.0f)
    )
}