package com.sspirit.nadiiaspaceassistant.models.missions.building

import com.sspirit.nadiiaspaceassistant.models.items.ItemDescriptor
import com.sspirit.nadiiaspaceassistant.models.items.LootGroup

data class BuildingLootContainerItem(
    val item: ItemDescriptor,
    var amount: Int
)

data class BuildingLootContainer(
    val id: String,
    val room: BuildingRoom,
    val group: LootGroup,
    val items: MutableList<BuildingLootContainerItem> = mutableListOf(),
)