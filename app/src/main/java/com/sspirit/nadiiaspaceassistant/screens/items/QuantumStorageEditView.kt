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

    ItemsStorageNodesEditView<Unit>(
        header = "Квант. хранилище $storageId",
        sourceNodes = storage.nodes,
        navigator = navigator,
        selectorRoute = Routes.NoGroupItemsSelector,
        onSave = { nodes, state ->
            storage.nodes = nodes
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
