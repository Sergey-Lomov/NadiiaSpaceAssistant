package com.sspirit.nadiiaspaceassistant.models.missions.building.specloot

data class BuildingDoorCode (
    val code: String,
) : SpecialLoot {
    override val title: String
        get() = "PIN-код $code"
}