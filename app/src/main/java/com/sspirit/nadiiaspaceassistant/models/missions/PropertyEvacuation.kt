package com.sspirit.nadiiaspaceassistant.models.missions

import com.sspirit.nadiiaspaceassistant.utils.IndexConvertible
import com.sspirit.nadiiaspaceassistant.models.missions.building.Building
import java.time.LocalDate

enum class PropertyEvacuationGoal(val string: String) {
    OBJECT("Объект"),
    DATA("Данные"),
    LIFETIME("Время"),
    UNDEFINED("Неизвестно");

    companion object {
        fun byString(string: String): PropertyEvacuationGoal {
            return PropertyEvacuationGoal.entries.find { it.string == string } ?: UNDEFINED
        }
    }

    override fun toString(): String {
        return string
    }
}

enum class PropertyEvacuationKeys(override val index: Int) : IndexConvertible {
    ID(0),
    CLIENT(1),
    REWARD(2),
    DIFFICULT(3),
    EXPIRATION(4),
    REQUIREMENTS(5),
    PLACE(6),

    SPREADSHEET_ID(7),
    LOOT_TAGS(8),
    GOAL(9),
}

data class PropertyEvacuation (
    val id: String,
    val client: String,
    val reward: Int,
    val difficult: Float,
    val expiration: LocalDate,
    val requirements: String,
    val place: String,

    val building: Building,
    val lootTags: Array<String>,
    val goal: PropertyEvacuationGoal
)
