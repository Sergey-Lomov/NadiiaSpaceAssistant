package com.sspirit.nadiiaspaceassistant.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.services.ValuesRegister
import com.sspirit.nadiiaspaceassistant.ui.AutosizeStyledButton
import com.sspirit.nadiiaspaceassistant.ui.HeaderText
import com.sspirit.nadiiaspaceassistant.ui.LoadingOverlay
import com.sspirit.nadiiaspaceassistant.ui.RegularText
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper
import com.sspirit.nadiiaspaceassistant.ui.ScrollableColumn
import com.sspirit.nadiiaspaceassistant.viewmodels.InfoDialogViewModel

@Composable
fun InfoDialogView(modelId: String, navController: NavHostController) {
    val viewModel: InfoDialogViewModel = ValuesRegister.get(modelId) ?: return
    val isLoading = remember { mutableStateOf(false) }

    ScreenWrapper(navController) {
        LoadingOverlay(isLoading) {
            Column(Modifier.padding(horizontal = 16.dp)) {
                Spacer(Modifier.weight(1f))
                HeaderText(viewModel.title)
                Spacer(Modifier.height(16.dp))
                RegularText(viewModel.info)
                Spacer(Modifier.weight(1f))
                ActionsPanel(viewModel, isLoading)
            }
        }
    }
}

@Composable
private fun ActionsPanel(model: InfoDialogViewModel, loadingState: MutableState<Boolean>) {
    for (action in model.actions) {
        Spacer(Modifier.height(8.dp))
        AutosizeStyledButton(
            title = action.key,
            onClick = { action.value(loadingState) }
        )
    }
}