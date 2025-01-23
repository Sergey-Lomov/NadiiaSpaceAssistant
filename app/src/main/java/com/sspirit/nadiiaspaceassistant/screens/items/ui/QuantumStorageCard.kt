package com.sspirit.nadiiaspaceassistant.screens.items.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import com.sspirit.nadiiaspaceassistant.models.items.QuantumStorage
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoveToInbox
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import com.sspirit.nadiiaspaceassistant.navigation.Routes
import com.sspirit.nadiiaspaceassistant.services.dataproviders.QuantumStorageDataProvider
import com.sspirit.nadiiaspaceassistant.ui.ElementsList
import com.sspirit.nadiiaspaceassistant.ui.HeaderText
import com.sspirit.nadiiaspaceassistant.ui.LocalSWLoadingState
import com.sspirit.nadiiaspaceassistant.ui.RegularText
import com.sspirit.nadiiaspaceassistant.ui.StyledIconButton
import com.sspirit.nadiiaspaceassistant.ui.StorageContentList
import com.sspirit.nadiiaspaceassistant.ui.TitleValueRow
import com.sspirit.nadiiaspaceassistant.utils.navigateTo
import com.sspirit.nadiiaspaceassistant.utils.simpleCoroutineLaunch

enum class QuantumStorageTool(val icon: ImageVector, val description: String) {
    DELETE(Icons.Default.Delete, "Delete"),
    EDIT(Icons.Default.Edit, "Edit"),
    TO_WAREHOUSE(Icons.Default.MoveToInbox, "To warehouse"),
}

@Composable
fun QuantumStorageCard(
    storage: QuantumStorage,
    tools: Array<QuantumStorageTool>,
    navigator: NavHostController,
    onClick: (() -> Unit)? = null,
) {
    val isWarehouse = storage == QuantumStorageDataProvider.warehouse
    val header = if (isWarehouse) "Склад" else "Хранилище ${storage.id}"

    Card(
        onClick = { onClick?.invoke() }
    ) {
        Column(Modifier.padding(16.dp)) {
            HeaderText(header)

            if (!isWarehouse) {
                Spacer(Modifier.height(8.dp))
                IdRow(storage.id)
                Spacer(Modifier.height(8.dp))
                TitleValueRow("Цена продажи", storage.sellPrice)
            }

            Spacer(Modifier.height(8.dp))
            StorageContentList(storage.nodes)
            Spacer(Modifier.height(8.dp))
            ToolsRow(storage, tools, navigator)
        }
    }
}

@Composable
private fun IdRow(id: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        RegularText("Код", autofill = false)
        Spacer(Modifier.weight(1f))
        QuantumStorageIdRow(id)
    }
}

@Composable
private fun ToolsRow(
    storage: QuantumStorage,
    tools: Array<QuantumStorageTool>,
    navigator: NavHostController
) {
    val state = LocalSWLoadingState.current ?: return

    Row(Modifier.fillMaxWidth()) {
        ElementsList(tools, 16, false) {
            StyledIconButton(it.icon, it.description, modifier = Modifier.weight(1f)) {
                toolAction(storage, it, state, navigator)
            }
        }
    }
}

private fun toolAction(
    storage: QuantumStorage,
    tool: QuantumStorageTool,
    loadingState: MutableState<Boolean>,
    navigator: NavHostController
) {
    when (tool) {
        QuantumStorageTool.DELETE ->
            simpleCoroutineLaunch(loadingState) {
                QuantumStorageDataProvider.remove(storage)
            }

        QuantumStorageTool.EDIT ->
            navigator.navigateTo(Routes.ItemsQuantumStorageEdit, storage.id)

        QuantumStorageTool.TO_WAREHOUSE ->
            simpleCoroutineLaunch(loadingState) {
                QuantumStorageDataProvider.moveToWarehouse(storage)
            }
    }
}