package com.sspirit.nadiiaspaceassistant.models.missions.building.specloot

import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingRoom

data class BuildingSpecialLootContainer (
    val room: BuildingRoom,
    val loot: SpecialLoot
)