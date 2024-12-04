package com.sspirit.nadiiaspaceassistant.navigation

import kotlinx.serialization.Serializable
import java.io.Serial

sealed class Routes(val route: String) {
    data object Main : Routes("MainMenu")
    data object Cosmonavigation : Routes("Cosmonavigation.Menu")
    data object CosmonavigationTask : Routes("Cosmonavigation.Task")
    data object CosmonavigationTaskRequest : Routes("Cosmonavigation.Request")
    data object Hypernavigation : Routes("Hypernavigation")
}