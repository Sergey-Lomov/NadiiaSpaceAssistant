package com.sspirit.nadiiaspaceassistant.screens.items

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.navigation.Routes
import com.sspirit.nadiiaspaceassistant.services.dataproviders.QuantumStorageDataProvider
import com.sspirit.nadiiaspaceassistant.utils.mainLaunch
import com.sspirit.nadiiaspaceassistant.utils.simpleCoroutineLaunch

@Composable
fun QuantumStorageEditView(storageId: String, navigator: NavHostController) {
    val storage = QuantumStorageDataProvider.getBy(storageId.toInt()) ?: return
    val isWarehouse = storage == QuantumStorageDataProvider.warehouse
    val header = if (isWarehouse) "Квант. склад" else "Квант. хранилище ${storage.id}"

    ItemsStorageNodesEditView<Unit>(
        header = header,
        sourceNodes = storage.nodes.toTypedArray(),
        navigator = navigator,
        selectorRoute = Routes.NoGroupItemsSelector,
        onSave = { nodes, state ->
            storage.nodes = nodes.toMutableList()
            simpleCoroutineLaunch(state) {
                QuantumStorageDataProvider.replace(storage) {
                    mainLaunch {
                        navigator.popBackStack()
                    }
                }
            }
        }
    )
}
