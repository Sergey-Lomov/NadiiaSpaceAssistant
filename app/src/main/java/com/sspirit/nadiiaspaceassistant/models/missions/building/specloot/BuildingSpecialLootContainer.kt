package com.sspirit.nadiiaspaceassistant.models.missions.building.specloot

import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingRoom

data class BuildingSpecialLootContainer(
    val id: String,
    val loot: SpecialLoot,
    var room: BuildingRoom?, // null for gathered loot
)