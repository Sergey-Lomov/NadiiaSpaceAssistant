package com.sspirit.nadiiaspaceassistant.models.character

import com.sspirit.nadiiaspaceassistant.extensions.IndexConvertibleKey
import java.time.LocalDate

typealias CharacterRoutineItemSnapshot = MutableMap<LocalDate, CharacterRoutineItemStatus>

enum class CharacterRoutineItemKeys(override val index: Int) : IndexConvertibleKey {
    ID(0),
    TITLE(1),
}

enum class CharacterRoutineItemStatus(val label: String) {
    DONE("+"),
    UNDONE("-"),
    INACTIVE("~"),
    UNDEFINED("?");

    companion object {
        fun byString(string: String): CharacterRoutineItemStatus {
            return entries.find { it.label == string } ?: UNDEFINED
        }
    }

    override fun toString(): String {
        return label
    }
}

data class CharacterRoutineItem (
    val id: String,
    val title: String,
    var snapshots: CharacterRoutineItemSnapshot = mutableMapOf()
) {
    fun todayStatus(): CharacterRoutineItemStatus {
        return snapshots[LocalDate.now()] ?: CharacterRoutineItemStatus.UNDEFINED
    }

    fun yesterdayStatus(): CharacterRoutineItemStatus {
        return  snapshots[LocalDate.now().minusDays(1)] ?: CharacterRoutineItemStatus.UNDEFINED
    }
}