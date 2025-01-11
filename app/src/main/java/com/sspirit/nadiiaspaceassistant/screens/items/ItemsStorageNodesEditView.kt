package com.sspirit.nadiiaspaceassistant.screens.items

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.models.ItemsStorageNode
import com.sspirit.nadiiaspaceassistant.models.items.ItemDescriptor
import com.sspirit.nadiiaspaceassistant.navigation.Routes
import com.sspirit.nadiiaspaceassistant.services.ViewModelsRegister
import com.sspirit.nadiiaspaceassistant.ui.AutosizeStyledButton
import com.sspirit.nadiiaspaceassistant.ui.CenteredRegularText
import com.sspirit.nadiiaspaceassistant.ui.HeaderText
import com.sspirit.nadiiaspaceassistant.ui.LocalSWLoadingState
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper
import com.sspirit.nadiiaspaceassistant.ui.ScrollableColumn
import com.sspirit.nadiiaspaceassistant.ui.SpacedHorizontalDivider
import com.sspirit.nadiiaspaceassistant.ui.StyledButton
import com.sspirit.nadiiaspaceassistant.ui.TitleValueRow
import com.sspirit.nadiiaspaceassistant.utils.navigateWithModel
import com.sspirit.nadiiaspaceassistant.utils.update
import com.sspirit.nadiiaspaceassistant.viewmodels.ItemSelectorViewModel
import kotlin.math.max

private val nodesSaver = Saver<MutableList<ItemsStorageNode>, String>(
    save = { list ->
        ViewModelsRegister.register(list)
    },
    restore = { data ->
        val list = ViewModelsRegister.get<MutableList<ItemsStorageNode>>(data)
        ViewModelsRegister.unregister(data)
        list
    }
)

@Composable
fun <T> ItemsStorageNodesEditView(
    header: String,
    info: String? = null,
    sourceNodes: Array<ItemsStorageNode>,
    navigator: NavHostController,
    selectorRoute: Routes,
    itemsGrouper: ((ItemDescriptor) -> T)? = null,
    itemsGroupsOrder: Array<T>? = null,
    nodeStatusBuilder: (@Composable (ItemsStorageNode) -> Unit)? = null,
    onSave: (Array<ItemsStorageNode>, MutableState<Boolean>) -> Unit
) {
    val isLoading = remember { mutableStateOf(false) }
    val updater = remember { mutableIntStateOf(0) }
    val nodes = rememberSaveable (saver = nodesSaver) { mutableStateListOf(*sourceNodes) }

    LaunchedEffect(updater.intValue) {  }

    ScreenWrapper(navigator, "Лист предметов", isLoading) {
        ScrollableColumn {
            HeaderText(header)
            if (info != null) {
                Spacer(Modifier.height(8.dp))
                CenteredRegularText(info)
            }

            SpacedHorizontalDivider()
            for (node in nodes) {
                LootItemCard(nodes, node, updater, nodeStatusBuilder)
                if (node !== nodes.last())
                    Spacer(Modifier.height(8.dp))
            }
            SpacedHorizontalDivider()

            val selectorModel = ItemSelectorViewModel(
                unavailable = nodes.map { it.item }.toTypedArray(),
                onSelect = { item -> nodes.add(ItemsStorageNode(item, 1)) },
                grouper = itemsGrouper,
                order = itemsGroupsOrder
            )
            AutosizeStyledButton("Добавить предмет") {
                navigator.navigateWithModel(selectorRoute, selectorModel)
            }

            Spacer(Modifier.height(8.dp))
            SaveButton(nodes, onSave)
        }
    }
}

@Composable
private fun LootItemCard(
    nodes: MutableList<ItemsStorageNode>,
    node: ItemsStorageNode,
    updater: MutableIntState,
    statusBuilder: @Composable ((ItemsStorageNode) -> Unit)? = null
) {
    Card {
        Column(Modifier.padding(16.dp)) {
            CenteredRegularText(node.item.title)

            if (node.item.isLocked) {
                CenteredRegularText(
                    text = "Заблокировано",
                    color = Color.Red,
                    weight = FontWeight.Bold
                )
            }

            if (statusBuilder != null) {
                Spacer(Modifier.height(8.dp))
                statusBuilder(node)
            }

            Spacer(Modifier.height(8.dp))
            TitleValueRow("Количесво", value = node.amount)

            Spacer(Modifier.height(8.dp))
            Row {
                StyledButton("+") {
                    node.amount += 1
                    updater.update()
                }
                Spacer(Modifier.width(8.dp))
                StyledButton("-") {
                    node.amount = max(0, node.amount - 1)
                    updater.update()
                }
                Spacer(Modifier.width(8.dp))
                AutosizeStyledButton("Удалить") {
                    nodes.remove(node)
                    updater.update()
                }
            }
        }
    }
}

@Composable
private fun SaveButton(
    nodes: MutableList<ItemsStorageNode>,
    handler: (Array<ItemsStorageNode>, MutableState<Boolean>) -> Unit
) {
    val state = LocalSWLoadingState.current ?: return
    AutosizeStyledButton("Сохранить") {
        val nodesArray = nodes.toTypedArray()
        handler(nodesArray, state)
    }
}