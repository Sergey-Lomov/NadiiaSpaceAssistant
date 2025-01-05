package com.sspirit.nadiiaspaceassistant.viewmodels.building

import com.sspirit.nadiiaspaceassistant.models.items.LootGroup
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingLootContainer
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingRoom

data class LootGroupSelectorViewModel(
    val room: BuildingRoom? = null,
    val onSelect: (LootGroup) -> Unit,
)