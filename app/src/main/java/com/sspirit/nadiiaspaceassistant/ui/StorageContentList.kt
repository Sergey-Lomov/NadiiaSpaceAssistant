package com.sspirit.nadiiaspaceassistant.ui

import androidx.compose.runtime.Composable
import com.sspirit.nadiiaspaceassistant.models.ItemsStorageNode
import com.sspirit.nadiiaspaceassistant.ui.utils.storageNodeDescription
import com.sspirit.nadiiaspaceassistant.ui.utils.stringsToList

@Composable
fun StorageContentList(nodes: Array<ItemsStorageNode>) {
    when (nodes.size) {
        0 ->
            CenteredRegularText("Пусто")

        1 ->
            TitleValueRow("Содержит", storageNodeDescription(nodes.first()))

        else -> {
            val listItems = nodes.map { storageNodeDescription(it) }
            val list = stringsToList(listItems)
            RegularText("Содержит: \n$list")
        }
    }
}