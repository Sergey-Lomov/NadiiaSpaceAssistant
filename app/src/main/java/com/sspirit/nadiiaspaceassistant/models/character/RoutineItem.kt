package com.sspirit.nadiiaspaceassistant.models.character

import com.sspirit.nadiiaspaceassistant.extensions.IndexConvertibleKey
import java.time.LocalDate

typealias CharacterRoutineItemSnapshot = MutableMap<LocalDate, CharacterRoutineItemStatus>

enum class CharacterRoutineItemKeys(override val index: Int) : IndexConvertibleKey {
    ID(0),
    TITLE(1),
}

enum class CharacterRoutineItemStatus {
    DONE,
    UNDONE,
    INACTIVE;

    companion object {
        fun byString(string: String): CharacterRoutineItemStatus {
            return when (string) {
                "✔" -> DONE
                "~" -> INACTIVE
                else -> UNDONE
            }
        }
    }
}

data class CharacterRoutineItem (
    val id: String,
    val title: String,
    var snapshots: CharacterRoutineItemSnapshot = mutableMapOf()
)