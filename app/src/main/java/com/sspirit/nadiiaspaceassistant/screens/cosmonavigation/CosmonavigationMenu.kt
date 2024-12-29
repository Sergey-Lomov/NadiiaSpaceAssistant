package com.sspirit.nadiiaspaceassistant.screens.cosmonavigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.models.character.CharacterSkillType
import com.sspirit.nadiiaspaceassistant.services.fabrics.CosmonavigationTaskGenerationRequest
import com.sspirit.nadiiaspaceassistant.navigation.Routes
import com.sspirit.nadiiaspaceassistant.services.dataproviders.CharacterDataProvider
import com.sspirit.nadiiaspaceassistant.ui.PlainMenuItem
import com.sspirit.nadiiaspaceassistant.ui.PlainNavigationMenu
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val items: Array<PlainMenuItem>
    get() = arrayOf(
        PlainMenuItem("Космический полет", Routes.CosmonavigationTaskByRequest) {
            val adaptive = CharacterDataProvider.character.level(CharacterSkillType.PILOTING)
            val request = CosmonavigationTaskGenerationRequest.commonTravel(adaptive)
            Json.encodeToString(request)
        },

        PlainMenuItem("Приземление / стыковка", Routes.SpaceSystemSelection) {
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