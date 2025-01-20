package com.sspirit.nadiiaspaceassistant.services.dataproviders

import android.util.Log
import com.sspirit.nadiiaspaceassistant.utils.getInt
import com.sspirit.nadiiaspaceassistant.utils.getString
import com.sspirit.nadiiaspaceassistant.models.character.Character
import com.sspirit.nadiiaspaceassistant.models.character.CharacterRoutine
import com.sspirit.nadiiaspaceassistant.models.character.CharacterRoutineItem
import com.sspirit.nadiiaspaceassistant.models.character.CharacterRoutineItemKeys
import com.sspirit.nadiiaspaceassistant.models.character.CharacterRoutineItemStatus
import com.sspirit.nadiiaspaceassistant.models.character.CharacterSkill
import com.sspirit.nadiiaspaceassistant.models.character.CharacterSkillKeys
import com.sspirit.nadiiaspaceassistant.models.character.CharacterSkillType
import com.sspirit.nadiiaspaceassistant.models.character.CharacterTrait
import com.sspirit.nadiiaspaceassistant.services.dataproviders.tablerows.CharacterTraitRow
import com.sspirit.nadiiaspaceassistant.utils.daysToNow
import com.sspirit.nadiiaspaceassistant.utils.plusDays
import com.sspirit.nadiiaspaceassistant.utils.plusHours
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

private const val expirationHours = 2
private const val spreadsheetId = "1rVty48hc2Q1zpkfyZ8zSvNkGQKDwKrXFxrLMADTga7w"
private const val skillsFirstRow = 2
private const val traitsFirstRow = 2
private const val routineItemsFirstRow = 3
private const val skillsSheet = "Skills"
private const val progressColumn = "C"
private const val skillsRange = "$skillsSheet!A$skillsFirstRow:D9"
private const val traitsSheet = "Traits"
private const val traitsRange = "$traitsSheet!A$traitsFirstRow:F150"
private const val itemsMetaRange = "A3:B20"
private val zeroDay = LocalDate.of(2024, 12, 7)
private val zeroDayColumn = CharacterRoutineItemKeys.entries.size + 1

object CharacterDataProvider : GoogleSheetDataProvider() {
    const val MAX_SKILL_PROGRESS = 30
    var character = Character.emptyInstance()
    var expiredTraits: List<CharacterTrait> = listOf()
    private var routinesLists = mutableMapOf<CharacterSkillType, String>()

    fun downloadCharacter(forced: Boolean = false) {
        if (expirationDate != null && !forced) {
            if (LocalDateTime.now() < expirationDate) {
                return
            }
        }

        val skillsResponse = request(spreadsheetId, skillsRange)

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
                    val routine = downloadRoutine(routineList)
                    routines[type] = routine
                }
            }
        }

        val traits = downloadTraits()
        val activeTraits = traits.filter { !it.isExpired }.toMutableList()
        expiredTraits = traits.filter { it.isExpired }
        character = Character(skills.toTypedArray(), routines, activeTraits)
        expirationDate = LocalDateTime.now().plusHours(expirationHours)
    }

    fun updateSkillProgress(skill: CharacterSkill, progress: Int) {
        val index = character.skills.indexOf(skill)
        val row = index + skillsFirstRow
        uploadCell(spreadsheetId, skillsSheet, progressColumn, row, progress) { success ->
            if (success) skill.progress = progress
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

        val columnInt = zeroDayColumn + ChronoUnit.DAYS.between(zeroDay, date).toInt()
        val column = columnIndexByInt(columnInt)
        val row = routineItemsFirstRow + routine.indexOf(item)
        uploadCell(spreadsheetId, list, column, row, status) {
            item.snapshots[date] = status
        }
    }

    fun addTrait(trait: CharacterTrait, completion: Completion = null) {
        val oldTraits = character.traits
            .filter { it.type == trait.type }
            .sortedBy {
                val expiration = it.expiration ?: LocalDate.now()
                expiration.daysToNow()
            }

        if (oldTraits.size >= trait.type.limit) {
            val target = oldTraits.first()
            val targetExpiration = target.expiration ?: LocalDate.now()
            val targetDaysLeft = targetExpiration.daysToNow()
            val newExpiration = trait.expiration ?: LocalDate.now()
            val newDaysLeft = newExpiration.daysToNow()

            if (newDaysLeft <= targetDaysLeft) {
                completion?.invoke(true)
            } else {
                removeTrait(target) { removeSuccess ->
                    if (removeSuccess) {
                        addTraitWithoutValidation(trait) { addSuccess ->
                            completion?.invoke(addSuccess)
                        }
                    } else {
                        completion?.invoke(false)
                    }
                }
            }
        } else {
            addTraitWithoutValidation(trait) { success ->
                completion?.invoke(success)
            }
        }
    }

    private fun addTraitWithoutValidation(trait: CharacterTrait, completion: Completion) {
        val row = CharacterTraitRow.from(trait)
        insert(spreadsheetId, traitsSheet, traitsFirstRow, listOf(row.toRawData())) { success ->
            if (success)
                character.traits.add(trait)
            completion?.invoke(success)
        }
    }

    fun removeTrait(trait: CharacterTrait, completion: Completion = null) {
        val index = firstRowWithText(trait.id, spreadsheetId, traitsSheet) ?: return
        deleteRow(spreadsheetId, traitsSheet, index) { success ->
            if (success) {
                character.traits.remove(trait)
            }
            completion?.invoke(success)
        }
    }

    private fun downloadRoutine(
        routineList: String,
        from: LocalDate = LocalDate.now().minusDays(1),
        to: LocalDate = LocalDate.now()
    ) : CharacterRoutine {
        val metaRange = "$routineList!$itemsMetaRange"
        val metaResponse = request(spreadsheetId, metaRange)

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

        val fromColumnIndex = ChronoUnit.DAYS.between(zeroDay, from).toInt()
        val toColumnIndex = ChronoUnit.DAYS.between(zeroDay, to).toInt()
        val fromColumn = columnIndexByInt(fromColumnIndex + zeroDayColumn)
        val toColumn = columnIndexByInt(toColumnIndex + zeroDayColumn)
        val lastRoutineRow = items.size + routineItemsFirstRow - 1
        val dataRange = "${routineList}!${fromColumn}${routineItemsFirstRow}:${toColumn}${lastRoutineRow}"
        val dataResponse = request(spreadsheetId, dataRange)
        val rawData = dataResponse.getValues()?.map { it.toTypedArray() }?.toTypedArray()

        try {
            if (rawData != null) {
                for (itemIndex: Int in rawData.indices) {
                    for (dateIndex: Int in rawData[itemIndex].indices) {
                        val dateRawData = rawData[itemIndex][dateIndex]
                        val dateStatus = CharacterRoutineItemStatus.byString(dateRawData.toString())
                        val date = from.plusDays(dateIndex)
                        items[itemIndex].snapshots[date] = dateStatus
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(logTag, "Space map data invalid: ${e.toString()}")
        }

        return items.toTypedArray()
    }

    private fun downloadTraits(): List<CharacterTrait> {
        val response = request(spreadsheetId, traitsRange)
        val rows = parseToArray(response, "Character traits data invalid", CharacterTraitRow::parse)
        return rows.map { it.toTrait() }
    }
}