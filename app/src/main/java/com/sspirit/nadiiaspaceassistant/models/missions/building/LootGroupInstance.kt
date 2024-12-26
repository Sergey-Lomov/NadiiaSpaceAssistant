package com.sspirit.nadiiaspaceassistant.models.missions.building

import com.sspirit.nadiiaspaceassistant.models.items.ItemDescriptor
import com.sspirit.nadiiaspaceassistant.models.items.LootGroup

data class LootGroupInstance(
    val lootGroup: LootGroup,
    val item: ItemDescriptor,
    val amount: Int
)