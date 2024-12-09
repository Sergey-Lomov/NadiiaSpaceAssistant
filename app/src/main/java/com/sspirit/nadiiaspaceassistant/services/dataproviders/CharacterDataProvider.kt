package com.sspirit.nadiiaspaceassistant.services.dataproviders

import android.util.Log
import com.google.api.services.sheets.v4.Sheets
import com.sspirit.nadiiaspaceassistant.extensions.getInt
import com.sspirit.nadiiaspaceassistant.extensions.getString
import com.sspirit.nadiiaspaceassistant.models.character.Character
import com.sspirit.nadiiaspaceassistant.models.character.CharacterRoutine
import com.sspirit.nadiiaspaceassistant.models.character.CharacterRoutineItem
import com.sspirit.nadiiaspaceassistant.models.character.CharacterRoutineItemKeys
import com.sspirit.nadiiaspaceassistant.models.character.CharacterRoutineItemStatus
import com.sspirit.nadiiaspaceassistant.models.character.CharacterSkill
import com.sspirit.nadiiaspaceassistant.models.character.CharacterSkillKeys
import com.sspirit.nadiiaspaceassistant.models.character.CharacterSkillType
import java.time.LocalDate
import java.time.temporal.ChronoUnit

private const val characterSheetId = "1rVty48hc2Q1zpkfyZ8zSvNkGQKDwKrXFxrLMADTga7w"
private const val skillsListRange = "Skills!A2:D9"
private const val progressColumn = "C"
private const val skillsDataFirstRow = 2
private const val routineItemsFirstRow = 3
private const val itemsMetaRange = "A3:B20"
private val zeroDay = LocalDate.of(2024, 12, 7)
private val zeroDayColumn = CharacterRoutineItemKeys.entries.size + 1

object CharacterDataProvider : GoogleSheetDataProvider() {
    var character = Character.emptyInstance()
    private var routinesLists = mutableMapOf<CharacterSkillType, String>()

    fun getCharacter() {
        val sheetsService: Sheets = getSheetsService()
        val skillsResponse = sheetsService
            .spreadsheets()
            .values()
            .get(characterSheetId, skillsListRange)
            .execute()

        routinesLists.clear()
        val skills = mutableListOf<CharacterSkill>()
        val routines = mutableMapOf<CharacterSkillType, CharacterRoutine>()

        val rawSkills = skillsResponse.getValues()?.map { it.toTypedArray() }?.toTypedArray()
        if (rawSkills != null) {
            for (rawSkill in rawSkills) {
                val typeId = rawSkill.getString(CharacterSkillKeys.ID)
                val type = CharacterSkillType.byId(typeId)
                val skill = CharacterSkill(
                    type = type,
                    title = rawSkill.getString(CharacterSkillKeys.TITLE),
                    progress = rawSkill.getInt(CharacterSkillKeys.PROGRESS)
                )
                skills.add(skill)

                val routineList = rawSkill.getString(CharacterSkillKeys.ROUTINE)
                routinesLists[type] = routineList
                if (routineList.isNotEmpty()) {
                    val routine = loadRoutine(sheetsService, routineList)
                    routines[type] = routine
                }
            }
        }

        character = Character(skills.toTypedArray(), routines)
    }

    fun updateSkillProgress(skill: CharacterSkill, progress: Int) {
        val index = character.skills.indexOf(skill)
        val range = progressColumn + (index + skillsDataFirstRow).toString()
        updateCell(characterSheetId, range, progress.toString()) {
            skill.progress = progress
        }
    }

    fun updateRoutineItemStatus(
        skillType: CharacterSkillType,
        item: CharacterRoutineItem,
        date: LocalDate,
        status: CharacterRoutineItemStatus
    ) {
        val list = routinesLists[skillType] ?: ""
        if (list.isEmpty()) {
            Log.e(logTag, "No routine list for skill type $skillType")
            return
        }

        val routine = character.routines[skillType] ?: arrayOf()
        if (routine.isEmpty()) {
            Log.e(logTag, "No routine for skill type $skillType")
            return
        }

        val columnInt = zeroDayColumn + ChronoUnit.DAYS.between(zeroDay, date)
        val column = columnIndexByInt(columnInt)
        val row = routineItemsFirstRow + routine.indexOf(item)
        val range = "$list!$column$row"
        updateCell(characterSheetId, range, status.toString()) {
            item.snapshots[date] = status
        }
    }

    private fun loadRoutine(
        service: Sheets,
        routineList: String,
        from: LocalDate = LocalDate.now().minusDays(1),
        to: LocalDate = LocalDate.now()
    ) : CharacterRoutine {
        val metaRange = "$routineList!$itemsMetaRange"
        val metaResponse = service
            .spreadsheets()
            .values()
            .get(characterSheetId, metaRange)
            .execute()

        val items = mutableListOf<CharacterRoutineItem>()
        val rawItems = metaResponse.getValues()?.map { it.toTypedArray() }?.toTypedArray()
        if (rawItems != null) {
            for (rawItem in rawItems) {
                val item = CharacterRoutineItem(
                    id = rawItem.getString(CharacterRoutineItemKeys.ID),
                    title = rawItem.getString(CharacterRoutineItemKeys.TITLE)
                )
                items.add(item)
            }
        }

        val fromColumnIndex = ChronoUnit.DAYS.between(zeroDay, from)
        val toColumnIndex = ChronoUnit.DAYS.between(zeroDay, to)
        val fromColumn = columnIndexByInt(fromColumnIndex + zeroDayColumn)
        val toColumn = columnIndexByInt(toColumnIndex + zeroDayColumn)
        val lastRoutineRow = items.size + routineItemsFirstRow - 1
        val dataRange = "${routineList}!${fromColumn}${routineItemsFirstRow}:${toColumn}${lastRoutineRow}"

        val dataResponse = service
            .spreadsheets()
            .values()
            .get(characterSheetId, dataRange)
            .execute()
        val rawData = dataResponse.getValues()?.map { it.toTypedArray() }?.toTypedArray()

        try {
            if (rawData != null) {
                for (itemIndex: Int in rawData.indices) {
                    for (dateIndex: Int in rawData[itemIndex].indices) {
                        val dateRawData = rawData[itemIndex][dateIndex]
                        val dateStatus = CharacterRoutineItemStatus.byString(dateRawData.toString())
                        val date = from.plusDays(dateIndex.toLong())
                        items[itemIndex].snapshots[date] = dateStatus
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(logTag, "Space map data invalid: ${e.toString()}")
        }

        return items.toTypedArray()
    }
}