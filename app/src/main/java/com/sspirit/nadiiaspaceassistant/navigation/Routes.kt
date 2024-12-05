package com.sspirit.nadiiaspaceassistant.navigation

sealed class Routes(val route: String) {
    data object Main : Routes("MainMenu")
    data object Cosmonavigation : Routes("Cosmonavigation.Menu")
    data object CosmonavigationTaskByRequest : Routes("Cosmonavigation.Task.ByRequest")
    data object CosmonavigationTaskByPOI : Routes("Cosmonavigation.Task.ByPOI")
    data object CosmonavigationTaskRequest : Routes("Cosmonavigation.Request")
    data object StarSystemSelection : Routes("StarSystemSelection")
    data object SpaceObjectSelection : Routes("SpaceObjectSelection")
    data object SpacePOISelection : Routes("SpacePOISelection")
}