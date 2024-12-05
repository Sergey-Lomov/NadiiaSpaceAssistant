package com.sspirit.nadiiaspaceassistant.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.generators.CosmonavigationTaskGenerationRequest
import com.sspirit.nadiiaspaceassistant.generators.generateCosmonavigationTask
import com.sspirit.nadiiaspaceassistant.navigation.Routes
import com.sspirit.nadiiaspaceassistant.ui.PlainMenuItem
import com.sspirit.nadiiaspaceassistant.ui.PlainNavigationMenu
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val items: Array<PlainMenuItem>
    get() = arrayOf(
        PlainMenuItem("Космический полет", Routes.CosmonavigationTaskByRequest) {
            // TODO: Add support for player current piloting skill
            val request = CosmonavigationTaskGenerationRequest.commonTravel(1.0f)
            Json.encodeToString(request)
        },

        PlainMenuItem("Приземление / стыковка", Routes.StarSystemSelection) {
            val flow = arrayOf(
                Routes.SpaceObjectSelection.route,
                Routes.SpacePOISelection.route,
                Routes.CosmonavigationTaskByPOI.route
            )
            Json.encodeToString(flow)
        },

        PlainMenuItem("Заказная задача", Routes.CosmonavigationTaskRequest)
    )

@Composable
fun CosmonavigationMenu(navController: NavHostController) {
    PlainNavigationMenu(items, navController)
}