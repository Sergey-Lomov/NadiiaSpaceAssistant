package com.sspirit.nadiiaspaceassistant.models.missions

import com.sspirit.nadiiaspaceassistant.extensions.IndexConvertibleKey
import java.time.LocalDate

enum class MissionStatus(val string: String) {
    AVAILABLE("Доступное"),
    IN_PROGRESS("Выполняется"),
    DONE("Выполнено"),
    FAILED("Провалено"),
    EXPIRED("Истекло"),
    UNDEFINED("Неизвестно");

    companion object {
        fun byString(string: String): MissionStatus {
            return MissionStatus.entries.find { it.string == string } ?: UNDEFINED
        }
    }
}

enum class MissionType(val string: String) {
    STORY("История"),
    MEDS_TEST("Испытания препаратов"),
    CHAIN_FIX("Починка цепей реактора"),
    PROPERTY_EVACUATION("Эвакуация собственности"),
    UNDEFINED("Неизвестно");

    companion object {
        fun byString(string: String): MissionType {
            return MissionType.entries.find { it.string == string } ?: UNDEFINED
        }
    }
}

enum class MissionKeys(override val index: Int) : IndexConvertibleKey {
    ID(0),
    TYPE(1),
    DESCRIPTION(2),
    DIFFICULT(3),
    EXPIRATION(4),
    REWARD(5),
    STATUS(6),
}

data class MissionPreview(
    val id: String,
    val type: MissionType,
    val description: String,
    val difficult: Float,
    val expiration: LocalDate,
    val reward: String,
    val status: MissionStatus,
)