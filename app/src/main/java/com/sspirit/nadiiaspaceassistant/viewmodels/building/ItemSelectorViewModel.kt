package com.sspirit.nadiiaspaceassistant.viewmodels.building

import com.sspirit.nadiiaspaceassistant.models.items.ItemDescriptor
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingLootContainer

data class ItemSelectorViewModel(
    val container: BuildingLootContainer? = null,
    val onSelect: (ItemDescriptor) -> Unit,
)