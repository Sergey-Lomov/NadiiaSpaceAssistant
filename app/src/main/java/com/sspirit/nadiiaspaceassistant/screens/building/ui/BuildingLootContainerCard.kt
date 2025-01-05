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
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingLootContainerItem
import com.sspirit.nadiiaspaceassistant.ui.CenteredRegularText
import com.sspirit.nadiiaspaceassistant.ui.RegularText
import com.sspirit.nadiiaspaceassistant.ui.TitleValueRow
import com.sspirit.nadiiaspaceassistant.ui.TitlesValuesList
import com.sspirit.nadiiaspaceassistant.ui.utils.stringsToList

@Composable
fun BuildingLootContainerCard(loot: BuildingLootContainer, onClick: (() -> Unit)? = null) {
    fun lootItemDescription(item: BuildingLootContainerItem): String =
        "${item.item.title} x${item.amount}"

    Card(
        onClick = { onClick?.invoke() }
    ) {
        Column(Modifier.padding(16.dp)) {
            val price = loot.items.sumOf { it.item.sellPrice * it.amount }
            TitlesValuesList(
                "Группа" to "${loot.group.title}(${loot.group.id})",
                "Цена" to price,
            )

            Spacer(Modifier.height(8.dp))

            when (loot.items.size) {
                0 ->
                    CenteredRegularText("Пустой")
                1 ->
                    TitleValueRow("Содержит", lootItemDescription(loot.items.first()))
                else -> {
                    val listItems = loot.items.map { lootItemDescription(it) }
                    val list = stringsToList(listItems)
                    RegularText("Содержит: \n$list")
                }
            }
        }
    }
}