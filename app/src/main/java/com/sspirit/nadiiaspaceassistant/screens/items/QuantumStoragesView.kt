package com.sspirit.nadiiaspaceassistant.screens.items

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.screens.items.ui.QuantumStorageCard
import com.sspirit.nadiiaspaceassistant.services.ViewModelsRegister
import com.sspirit.nadiiaspaceassistant.services.dataproviders.QuantumStorageDataProvider
import com.sspirit.nadiiaspaceassistant.ui.ElementsList
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper
import com.sspirit.nadiiaspaceassistant.ui.ScrollableColumn
import com.sspirit.nadiiaspaceassistant.utils.updaterState
import com.sspirit.nadiiaspaceassistant.viewmodels.QuantumStoragesViewModel

@Composable
fun QuantumStoragesView(modelId: String, navigator: NavHostController) {
    val model = ViewModelsRegister.get<QuantumStoragesViewModel>(modelId) ?: return
    val isLoading = remember { mutableStateOf(false) }

    ScreenWrapper(navigator, "Квант. хранилища", isLoading) {
        ScrollableColumn {
            val storages = model.storagesProvider().sortedBy { it.id }
            ElementsList(storages) { QuantumStorageCard(it, model.tools, navigator) }
        }
    }
}