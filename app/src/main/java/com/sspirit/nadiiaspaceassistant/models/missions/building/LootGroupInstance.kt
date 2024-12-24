package com.sspirit.nadiiaspaceassistant.models.missions.building

import android.media.audiofx.AudioEffect.Descriptor
import com.sspirit.nadiiaspaceassistant.models.items.LootGroup

data class LootGroupInstance(
    val lootGroup: LootGroup,
    val item: Descriptor,
    val amount: Int
)