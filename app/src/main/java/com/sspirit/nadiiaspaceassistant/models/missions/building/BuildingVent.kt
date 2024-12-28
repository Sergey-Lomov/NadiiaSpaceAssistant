package com.sspirit.nadiiaspaceassistant.models.missions.building

import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingVentSize.UNDEFINED

enum class BuildingVentSize(val string: String) {
    BIOMETRY("Минимальная"),
    RED_CARD("Узкая"),
    GREEN_CARD("Стандартная"),
    BLUE_CARD("Широкая"),
    UNDEFINED("Неизвестно");

    companion object {
        fun byString(string: String): BuildingVentSize {
            return BuildingVentSize.entries.find { it.string == string } ?: UNDEFINED
        }
    }
}

enum class BuildingVentGrilleState(val string: String) {
    MISSED("Нет"),
    UP("Поднята"),
    DOWN("Опущена"),
    UNDEFINED("?");

    companion object {
        fun byString(string: String): BuildingVentGrilleState {
            return BuildingVentGrilleState.entries.find { it.string == string } ?: UNDEFINED
        }
    }
}

data class BuildingVent (
    val passageway: BuildingPassageway,
    val size: BuildingVentSize,
    val grilleState: BuildingVentGrilleState
)