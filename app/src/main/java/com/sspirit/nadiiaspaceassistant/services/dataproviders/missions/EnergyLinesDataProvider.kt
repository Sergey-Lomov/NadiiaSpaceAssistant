package com.sspirit.nadiiaspaceassistant.services.dataproviders.missions

import android.util.Log
import com.google.api.services.sheets.v4.model.ValueRange
import com.sspirit.nadiiaspaceassistant.extensions.getBoolean
import com.sspirit.nadiiaspaceassistant.extensions.getDate
import com.sspirit.nadiiaspaceassistant.extensions.getFloat
import com.sspirit.nadiiaspaceassistant.extensions.getInt
import com.sspirit.nadiiaspaceassistant.extensions.getSplitedString
import com.sspirit.nadiiaspaceassistant.extensions.getString
import com.sspirit.nadiiaspaceassistant.models.missions.EnergyLines
import com.sspirit.nadiiaspaceassistant.models.missions.EnergyLinesKeys
import com.sspirit.nadiiaspaceassistant.services.dataproviders.GoogleSheetDataProvider
import com.sspirit.nadiiaspaceassistant.services.dataproviders.logTag

private val missionRange = "A1:EZ1"

object EnergyLinesDataProvider : GoogleSheetDataProvider(), MissionsDataProvider<EnergyLines> {
    var missions = mutableMapOf<String, EnergyLines>()

    override fun getBy(id: String): EnergyLines? {
        return missions[id]
    }

    override fun download(id: String) {
        val range = "$id!$missionRange"
        val response = service
            .spreadsheets()
            .values()
            .get(MissionsListDataProvider.spreadsheetId, range)
            .execute()

        val mission = parseMission(response)
        if (mission != null)
            missions[id] = mission
    }

    private fun parseMission(valueRange: ValueRange): EnergyLines? {
        val rawLines = valueRange.getValues()?.map { it.toTypedArray() }?.toTypedArray()
        val raw = rawLines?.firstOrNull()

        try {
            if (raw != null) {
                return EnergyLines(
                    id = raw.getString(EnergyLinesKeys.ID),
                    client = raw.getString(EnergyLinesKeys.CLIENT),
                    reward = raw.getInt(EnergyLinesKeys.REWARD),
                    difficult = raw.getFloat(EnergyLinesKeys.DIFFICULT),
                    expiration = raw.getDate(EnergyLinesKeys.EXPIRATION, dateFormatter),
                    requirements = raw.getString(EnergyLinesKeys.REQUIREMENTS),
                    place = raw.getString(EnergyLinesKeys.PLACE),
                    landingTimeMult = raw.getFloat(EnergyLinesKeys.TIME_MULT),
                    landingLengthMult = raw.getFloat(EnergyLinesKeys.LENGTH_MULT),
                    values = raw.getSplitedString(EnergyLinesKeys.VALUES, ","),
                    rules = raw.getSplitedString(EnergyLinesKeys.RULES, "\n"),
                    landingInfo = raw.getString(EnergyLinesKeys.LANDING_INFO),
                    hardPlaces = raw.getBoolean(EnergyLinesKeys.HARD_PLACES, false),
                    light = raw.getBoolean(EnergyLinesKeys.LIGHT, true),
                )
            }
        } catch (e: Exception) {
            Log.e(logTag, "Invalid meds tests data: ${e.toString()}")
        }

        return null
    }
}