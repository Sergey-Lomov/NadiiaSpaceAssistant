package com.sspirit.nadiiaspaceassistant.screens.items

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.navigation.Routes
import com.sspirit.nadiiaspaceassistant.services.ViewModelsRegister
import com.sspirit.nadiiaspaceassistant.services.dataproviders.QuantumStorageDataProvider
import com.sspirit.nadiiaspaceassistant.ui.PlainMenuItem
import com.sspirit.nadiiaspaceassistant.ui.PlainNavigationMenu
import com.sspirit.nadiiaspaceassistant.viewmodels.QuantumStoragesViewModel

private val items: Array<PlainMenuItem>
    get() = arrayOf(
        PlainMenuItem("Квантовые хранилища", Routes.ItemsQuantumStorages) {
            val storages = QuantumStorageDataProvider.storages.toTypedArray()
            val model = QuantumStoragesViewModel(storages)
            return@PlainMenuItem ViewModelsRegister.register(model)
        }
    )

@Composable
fun ItemsMenuView(navigator: NavHostController) {
    PlainNavigationMenu(items, navigator)
}