package com.sspirit.nadiiaspaceassistant.models.missions.building.devices

import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingDoorHackingLevel
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingDoorHackingLevel.UNDEFINED

enum class EnergyNodeState(val string: String) {
    UNOPTIMIZED("Не оптимизирован"),
    OPTIMIZED("Оптимизирован"),
    BROKEN("Сломан"),
    UNDEFINED("Неизвестно");

    companion object {
        fun byString(string: String): EnergyNodeState {
            return EnergyNodeState.entries.find { it.string == string } ?: UNDEFINED
        }
    }

    override fun toString(): String {
        return string
    }
}