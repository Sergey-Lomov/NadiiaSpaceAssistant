@file:Suppress("DEPRECATION")

package com.sspirit.nadiiaspaceassistant.services.dataproviders

import android.content.Context
import android.util.Log
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential.*
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.api.services.sheets.v4.model.ValueRange
import com.sspirit.nadiiaspaceassistant.NadiiaSpaceApplication
import com.sspirit.nadiiaspaceassistant.R
import java.io.InputStream

// In scope of this file 'object' means space object

private const val spaceMapSheetId = "1ho20Ap51LCX19HfurhMIk7T3G61LdA3Mh3EtnNgnPPY"
private const val starsListRange = "Systems!A2:F30"
private const val objectsListRange = "Objects!A2:Z50"

enum class StarKeys(val index: Int) {
    ID(0),
    TITLE(1)
}

enum class ObjectKeys(val index: Int) {
    ID(0),
    TITLE(1),
    PARENT(2)
}

data class SpaceObject(
    val id: String,
    val title: String,
    val parent: StarSystem
)

data class StarSystem(
    val id: String,
    val title: String,
    var objects: Array<SpaceObject> = arrayOf()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StarSystem

        if (id != other.id) return false
        if (title != other.title) return false
        if (!objects.contentEquals(other.objects)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + objects.contentHashCode()
        return result
    }
}

class CosmologyDataProvider {
    companion object {
        var spaceMap: Array<StarSystem> = arrayOf()

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

            spaceMap = parseMap(starsResponse, objectsResponse)
        }

        private fun parseMap(starsResponse: ValueRange, objectsResponse: ValueRange) : Array<StarSystem> {
            val rawStars = starsResponse.getValues()?.map { it.toTypedArray() }?.toTypedArray()
            val rawObjects = objectsResponse.getValues()?.map { it.toTypedArray() }?.toTypedArray()
            val updatedMap = mutableListOf<StarSystem>()

            try {
                if (rawStars != null && rawObjects != null) {
                    for (rawStar in rawStars) {
                        val starObjects = mutableListOf<SpaceObject>()
                        val star = StarSystem(
                            id = rawStar[StarKeys.ID.index].toString(),
                            title = rawStar[StarKeys.TITLE.index].toString()
                        )

                        for (rawObject in rawObjects) {
                            val parent = rawObject[ObjectKeys.PARENT.index].toString()
                            if (parent != star.id) { continue }

                            val spaceObject = SpaceObject(
                                id = rawObject[ObjectKeys.ID.index].toString(),
                                title = rawObject[ObjectKeys.TITLE.index].toString(),
                                parent = star
                            )
                            starObjects.add(spaceObject)
                        }

                        star.objects = starObjects.toTypedArray()
                        updatedMap.add(star)
                    }
                }
            } catch (e: Exception) {
                Log.e("Data validation", "Space map data invalid")
            }

            return updatedMap.toTypedArray()
        }
    }
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