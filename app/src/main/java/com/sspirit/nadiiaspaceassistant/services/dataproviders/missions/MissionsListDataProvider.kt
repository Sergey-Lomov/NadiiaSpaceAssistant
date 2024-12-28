package com.sspirit.nadiiaspaceassistant.services.dataproviders.missions

import com.sspirit.nadiiaspaceassistant.utils.getDate
import com.sspirit.nadiiaspaceassistant.utils.getFloat
import com.sspirit.nadiiaspaceassistant.utils.getString
import com.sspirit.nadiiaspaceassistant.models.missions.MissionPreview
import com.sspirit.nadiiaspaceassistant.models.missions.MissionPreviewKeys
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

object MissionsListDataProvider : GoogleSheetDataProvider() {
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
        val rawType = raw.getString(MissionPreviewKeys.TYPE)
        val rawStatus = raw.getString(MissionPreviewKeys.STATUS)
        return MissionPreview(
            id = raw.getString(MissionPreviewKeys.ID),
            type = MissionType.byString(rawType),
            description = raw.getString(MissionPreviewKeys.DESCRIPTION),
            difficult = raw.getFloat(MissionPreviewKeys.DIFFICULT),
            expiration = raw.getDate(MissionPreviewKeys.EXPIRATION, dateFormatter),
            reward = raw.getString(MissionPreviewKeys.REWARD),
            status = MissionStatus.byString(rawStatus),
            place = raw.getString(MissionPreviewKeys.PLACE),
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
            mission.status.string,
            mission.place
        ))

        uploadData(
            spreadsheetId = spreadsheetId,
            sheet = previewsSheet,
            column = 1,
            startRow = missionsPreviews.size + 1,
            data = data,
        ) { success ->
            if (success) {
                missionsPreviews.add(mission)
            }
        }
    }

    fun start(id: String) {
        setStatus(MissionStatus.IN_PROGRESS, id)
    }

    fun complete(id: String) {
        setStatus(MissionStatus.DONE, id)
    }

    private fun setStatus(status: MissionStatus, id: String) {
        val row = missionsPreviews.indexOfFirst { it.id == id } + 1
        val column = columnIndexByInt(MissionPreviewKeys.STATUS.index + 1)
        val range = "$previewsSheet!$column$row"
        uploadCell(
            spreadsheetId = spreadsheetId,
            range = range,
            newValue = status.string
        ) { success ->
            if (success) {
                val mission = missionsPreviews.first { it.id == id }
                mission.status = status
            }
        }
    }
}