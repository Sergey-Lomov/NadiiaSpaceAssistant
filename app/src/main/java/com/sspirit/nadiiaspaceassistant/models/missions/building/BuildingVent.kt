package com.sspirit.nadiiaspaceassistant.models.missions.building

import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingVentSize.UNDEFINED

enum class BuildingVentSize(val string: String) {
    MINIMAL("Минимальная"),
    NARROW("Узкая"),
    STANDARD("Стандартная"),
    WIDE("Широкая"),
    UNDEFINED("Неизвестно");

    companion object {
        private val crawlable: Array<BuildingVentSize>
            get() = arrayOf(NARROW, STANDARD, WIDE)

        fun byString(string: String): BuildingVentSize {
            return BuildingVentSize.entries.find { it.string == string } ?: UNDEFINED
        }
    }

    val isCrawable: Boolean
        get() = this in crawlable
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
    var grilleState: BuildingVentGrilleState
)