package com.sspirit.nadiiaspaceassistant.screens.items

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.R
import com.sspirit.nadiiaspaceassistant.services.ViewModelsRegister
import com.sspirit.nadiiaspaceassistant.services.dataproviders.QuantumStorageDataProvider
import com.sspirit.nadiiaspaceassistant.ui.AutosizeStyledButton
import com.sspirit.nadiiaspaceassistant.ui.CenteredRegularText
import com.sspirit.nadiiaspaceassistant.ui.ColoredCircle
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper
import com.sspirit.nadiiaspaceassistant.viewmodels.QuantumStorageIdEditViewModel

@Composable
fun QuantumStorageIdEditView(modelId: String, navigator: NavHostController) {
    val model = ViewModelsRegister.get<QuantumStorageIdEditViewModel>(modelId) ?: return
    val initial = (1..7).map { false }.toTypedArray()
    val values = remember { mutableStateListOf(*initial) }

    val id = values
        .mapIndexed { i, v -> if (v) 1 shl i else 0 }
        .sum()

    ScreenWrapper(navigator, "Id хранилища") {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            IdEditPanel(values)
            Spacer(Modifier.height(16.dp))
            StatusText(id)
            Spacer(Modifier.weight(1f))
            DoneButton(id, navigator, model.onDone)
        }
    }
}

@Composable
private fun IdEditPanel(values: SnapshotStateList<Boolean>) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        for (i in values.indices.reversed()) {
            val color = if (values[i]) Color.Red else Color.Black
            Box(
                Modifier.clickable { values[i] = !values[i] }
            ) {
                ColoredCircle(color, 42,)
            }
            if (i != 0)
                Spacer(Modifier.width(8.dp))
        }
    }
}

@Composable
private fun StatusText(id: Int) {
    val available = QuantumStorageDataProvider.getBy(id) == null
    if (available)
        CenteredRegularText(
            text = "Идентификатор $id доступен",
            color = colorResource(R.color.soft_green)
        )
    else
        CenteredRegularText(
            text = "Идентификатор $id занят",
            color = colorResource(R.color.soft_red)
        )
}

@Composable
private fun DoneButton(id: Int, navigator: NavHostController, onDone: (Int) -> Unit) {
    AutosizeStyledButton(
        title = "Подтвердить",
        enabled = QuantumStorageDataProvider.getBy(id) == null
    ) {
        onDone(id)
        navigator.popBackStack()
    }
}