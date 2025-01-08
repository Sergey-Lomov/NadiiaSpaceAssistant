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
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import com.sspirit.nadiiaspaceassistant.navigation.Routes
import com.sspirit.nadiiaspaceassistant.services.dataproviders.QuantumStorageDataProvider
import com.sspirit.nadiiaspaceassistant.ui.HeaderText
import com.sspirit.nadiiaspaceassistant.ui.RegularText
import com.sspirit.nadiiaspaceassistant.ui.StyledIconButton
import com.sspirit.nadiiaspaceassistant.ui.StorageContentList
import com.sspirit.nadiiaspaceassistant.utils.navigateTo
import com.sspirit.nadiiaspaceassistant.utils.simpleCoroutineLaunch

@Composable
fun QuantumStorageCard(
    storage: QuantumStorage,
    state: MutableState<Boolean>,
    navigator: NavHostController,
    onClick: (() -> Unit)? = null,
) {
    Card(
        onClick = { onClick?.invoke() }
    ) {
        Column(Modifier.padding(16.dp)) {
            HeaderText("#${storage.id}")
            Spacer(Modifier.height(8.dp))
            IdRow(storage.id)
            Spacer(Modifier.height(8.dp))
            StorageContentList(storage.nodes)
            Spacer(Modifier.height(8.dp))
            ControlsRow(storage, state, navigator)
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
private fun ControlsRow(
    storage: QuantumStorage,
    state: MutableState<Boolean>,
    navigator: NavHostController
) {
    Row(Modifier.fillMaxWidth()) {
        StyledIconButton(
            icon = Icons.Default.Delete,
            description = "Delete",
            modifier = Modifier.weight(1f)
        ) {
            simpleCoroutineLaunch(state) {
                QuantumStorageDataProvider.remove(storage)
            }
        }

        Spacer(Modifier.width(16.dp))

        StyledIconButton(
            icon = Icons.Default.Edit,
            description = "Edit",
            modifier = Modifier.weight(1f)
        ) {
            navigator.navigateTo(Routes.ItemsQuantumStorageEdit, storage.id)
        }
    }
}