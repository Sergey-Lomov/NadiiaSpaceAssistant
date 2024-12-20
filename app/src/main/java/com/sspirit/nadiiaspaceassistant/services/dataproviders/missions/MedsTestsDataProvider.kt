package com.sspirit.nadiiaspaceassistant.services.dataproviders.missions

import android.util.Log
import com.google.api.services.sheets.v4.model.ValueRange
import com.sspirit.nadiiaspaceassistant.extensions.getDate
import com.sspirit.nadiiaspaceassistant.extensions.getFloat
import com.sspirit.nadiiaspaceassistant.extensions.getInt
import com.sspirit.nadiiaspaceassistant.extensions.getString
import com.sspirit.nadiiaspaceassistant.models.character.CharacterSkillType
import com.sspirit.nadiiaspaceassistant.models.missions.MedsTests
import com.sspirit.nadiiaspaceassistant.models.missions.MedsTestsKeys
import com.sspirit.nadiiaspaceassistant.models.missions.MedsTestsProgression
import com.sspirit.nadiiaspaceassistant.models.missions.MedsTestsProgressionKeys
import com.sspirit.nadiiaspaceassistant.models.missions.MissionPreview
import com.sspirit.nadiiaspaceassistant.models.missions.MissionStatus
import com.sspirit.nadiiaspaceassistant.models.missions.MissionType
import com.sspirit.nadiiaspaceassistant.services.dataproviders.CharacterDataProvider
import com.sspirit.nadiiaspaceassistant.services.dataproviders.GoogleSheetDataProvider
import com.sspirit.nadiiaspaceassistant.services.dataproviders.logTag
import com.sspirit.nadiiaspaceassistant.services.generators.generateMedsTestMission
import java.time.LocalDateTime

private const val expirationHours = 24
private const val progressionsSpreadsheetId = "1tlVcuI9aWjqPWmsVU2jeVXkoLsefLRR77ivMOZbMiI0"
private const val progressionRange = "Progressions!A2:G20"

object MedsTestsDataProvider : GoogleSheetDataProvider(), MissionsDataProvider<MedsTests> {
    var progressions = arrayOf<MedsTestsProgression>()
    var missions = mutableMapOf<String, MedsTests>()
    private var proposal: MedsTests? = null

    fun downloadProgressions(forced: Boolean = false) {
        if (expirationDate != null && !forced) {
            if (LocalDateTime.now() < expirationDate) {
                return
            }
        }

        val response = service
            .spreadsheets()
            .values()
            .get(progressionsSpreadsheetId, progressionRange)
            .execute()

        progressions = parseToArray(response, "Meds tests progressions invalid", ::parseProgression)
        expirationDate = LocalDateTime.now().plusHours(expirationHours.toLong())
    }

    override fun getCurrentProposal(): MedsTests {
        if (proposal == null) {
            proposal = generateMedsTestMission()
        }
        return proposal!!
    }

    override fun getBy(id: String): MedsTests? {
        return missions[id]
    }

    override fun regenerateProposal() {
        proposal = generateMedsTestMission()
    }

    override fun download(id: String) {
        val range = "$id!A1:K1"
        val response = service
            .spreadsheets()
            .values()
            .get(MissionsListDataProvider.spreadsheetId, range)
            .execute()

        val mission = parseMission(response)
        if (mission != null)
            missions[id] = mission
    }

    override fun upload(mission: MedsTests) {
        addSheet(
            spreadsheetId = MissionsListDataProvider.spreadsheetId,
            sheetName = mission.id,
        )

        val data = listOf(listOf(
            mission.id,
            mission.client,
            mission.reward.toString(),
            mission.difficult.toString(),
            mission.expiration.format(dateFormatter),
            mission.requirements,
            mission.place,

            mission.trial,
            mission.danger.toString(),
            mission.additionalReward.toString(),
        ))

        uploadData(
            spreadsheetId = MissionsListDataProvider.spreadsheetId,
            sheet = mission.id,
            column = 1,
            row = 1,
            data = data,
        ) { success ->
            if (success) {
                missions[mission.id] = mission
                uploadMissionPreview(mission)
            }
        }
    }

    private fun uploadMissionPreview(mission: MedsTests) {
        val description = "Нужно простестировать препарат направленный на улучшение навыков персонажа, разрабатываемый ${mission.client}. В случае побочных эффектов будет выплачена дополнительная компенсация. Для участия в эксперименте требуется ${mission.requirements}"
        val preview = MissionPreview(
            id = mission.id,
            type = MissionType.MEDS_TEST,
            description = description,
            difficult = mission.difficult,
            expiration = mission.expiration,
            reward = "Кредиты: ${mission.reward}",
            status = MissionStatus.AVAILABLE,
            place = mission.place
        )

        MissionsListDataProvider.uploadMissionPreview(preview)
    }

    private fun parseMission(valueRange: ValueRange): MedsTests? {
        val rawLines = valueRange.getValues()?.map { it.toTypedArray() }?.toTypedArray()
        val raw = rawLines?.firstOrNull()

        try {
            if (raw != null) {
                return MedsTests(
                    id = raw.getString(MedsTestsKeys.ID),
                    client = raw.getString(MedsTestsKeys.CLIENT),
                    trial = raw.getString(MedsTestsKeys.TRIAL),
                    reward = raw.getInt(MedsTestsKeys.REWARD),
                    difficult = raw.getFloat(MedsTestsKeys.DIFFICULT),
                    danger = raw.getInt(MedsTestsKeys.DANGER),
                    additionalReward = raw.getInt(MedsTestsKeys.ADDITIONAL_REWARD),
                    expiration = raw.getDate(MedsTestsKeys.EXPIRATION, dateFormatter),
                    requirements = raw.getString(MedsTestsKeys.REQUIREMENTS),
                    place = raw.getString(MedsTestsKeys.PLACE)
                )
            }
        } catch (e: Exception) {
            Log.e(logTag, "Invalid meds tests data: ${e.toString()}")
        }

        return null
    }

    private fun parseProgression(raw: Array<Any>): MedsTestsProgression {
        val skillTitle = raw.getString(MedsTestsProgressionKeys.SKILL)
        val skill = CharacterDataProvider.character.skills
            .firstOrNull() { it.title == skillTitle}
            ?.type ?: CharacterSkillType.UNDEFINE
        return MedsTestsProgression(
            skill = skill,
            trial = raw.getString(MedsTestsProgressionKeys.TRIAL),
            levels = arrayOf(
                raw.getString(MedsTestsProgressionKeys.L0),
                raw.getString(MedsTestsProgressionKeys.L1),
                raw.getString(MedsTestsProgressionKeys.L2),
                raw.getString(MedsTestsProgressionKeys.L3),
            )
        )
    }
}