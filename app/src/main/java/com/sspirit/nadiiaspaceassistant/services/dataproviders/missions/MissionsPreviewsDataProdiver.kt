package com.sspirit.nadiiaspaceassistant.services.dataproviders.missions

import com.sspirit.nadiiaspaceassistant.extensions.getDate
import com.sspirit.nadiiaspaceassistant.extensions.getFloat
import com.sspirit.nadiiaspaceassistant.extensions.getString
import com.sspirit.nadiiaspaceassistant.models.missions.MissionPreview
import com.sspirit.nadiiaspaceassistant.models.missions.MissionKeys
import com.sspirit.nadiiaspaceassistant.models.missions.MissionStatus
import com.sspirit.nadiiaspaceassistant.models.missions.MissionType
import com.sspirit.nadiiaspaceassistant.services.dataproviders.GoogleSheetDataProvider
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

private const val expirationHours = 24
private val zeroDay = LocalDate.of(2024, 12, 17)
private const val previewsSheet = "List"
private const val previewsRange = "$previewsSheet!A1:K300"

object MissionsPreviewsDataProvider : GoogleSheetDataProvider() {
    const val spreadsheetId = "1ZooOnjs-5oEKHHOL2LRNMvColAzSU7d2nEskc2w_sHg"
    val progressionDifficult: Int
        get() = ChronoUnit.DAYS.between(zeroDay, LocalDate.now()).toInt()

    private var missionsPreviews = mutableListOf<MissionPreview>()
    private val activeStatuses = arrayOf(MissionStatus.AVAILABLE, MissionStatus.IN_PROGRESS)
    val activePreviews: Array<MissionPreview>
        get() = missionsPreviews
            .filter { it.status in activeStatuses }
            .toTypedArray()


    fun getMissions(forced: Boolean = false) {
        if (expirationDate != null && !forced) {
            if (LocalDateTime.now() < expirationDate) {
                return
            }
        }

        val response = service
            .spreadsheets()
            .values()
            .get(spreadsheetId, previewsRange)
            .execute()

        missionsPreviews = parseToArray(response, "Missions data invalid", ::parseMission)
            .toMutableList()
        expirationDate = LocalDateTime.now().plusHours(expirationHours.toLong())
    }
    
    private fun parseMission(raw: Array<Any>): MissionPreview {
        val rawType = raw.getString(MissionKeys.TYPE)
        val rawStatus = raw.getString(MissionKeys.STATUS)
        return MissionPreview(
            id = raw.getString(MissionKeys.ID),
            type = MissionType.byString(rawType),
            description = raw.getString(MissionKeys.DESCRIPTION),
            difficult = raw.getFloat(MissionKeys.DIFFICULT),
            expiration = raw.getDate(MissionKeys.EXPIRATION, dateFormatter),
            reward = raw.getString(MissionKeys.REWARD),
            status = MissionStatus.byString(rawStatus)
        )
    }

    fun uploadMissionPreview(mission: MissionPreview) {
        val data = listOf(listOf(
            mission.id,
            mission.type.string,
            mission.description,
            mission.difficult.toString(),
            mission.expiration.format(dateFormatter),
            mission.reward,
            mission.status.string
        ))

        uploadData(
            spreadsheetId = spreadsheetId,
            sheet = previewsSheet,
            column = 1,
            row = missionsPreviews.size + 1,
            data = data,
        ) { success ->
            if (success) {
                missionsPreviews.add(mission)
            }
        }
    }
}