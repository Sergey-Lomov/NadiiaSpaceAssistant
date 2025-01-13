package com.sspirit.nadiiaspaceassistant.screens.building.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingLootContainer
import com.sspirit.nadiiaspaceassistant.ui.CenteredRegularText
import com.sspirit.nadiiaspaceassistant.ui.RegularText
import com.sspirit.nadiiaspaceassistant.ui.StorageContentList
import com.sspirit.nadiiaspaceassistant.ui.TitleValueRow
import com.sspirit.nadiiaspaceassistant.ui.TitlesValuesList
import com.sspirit.nadiiaspaceassistant.ui.utils.humanReadable
import com.sspirit.nadiiaspaceassistant.ui.utils.storageNodeDescription
import com.sspirit.nadiiaspaceassistant.ui.utils.stringsToList
import com.sspirit.nadiiaspaceassistant.utils.flatArrayMap

@Composable
fun BuildingLootContainerCard(loot: BuildingLootContainer, onClick: (() -> Unit)? = null) {
    Card(
        onClick = { onClick?.invoke() }
    ) {
        Column(Modifier.padding(16.dp)) {
            val nodes = loot.quantumStorages
                .flatArrayMap { it.nodes }
                .plus(loot.nodes)
            val price = nodes.sumOf { it.item.sellPrice * it.amount }

            TitlesValuesList(
                "Id" to loot.id,
                "Группа" to "${loot.group.title}(${loot.group.id})",
                "Цена" to price,
                "Архивирован" to humanReadable(loot.quantumStorages.isNotEmpty())
            )

            Spacer(Modifier.height(8.dp))
            StorageContentList(nodes.toTypedArray())
        }
    }
}