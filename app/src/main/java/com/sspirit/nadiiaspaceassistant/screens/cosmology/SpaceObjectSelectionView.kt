package com.sspirit.nadiiaspaceassistant.screens.cosmology

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.models.cosmology.SpaceSystem
import com.sspirit.nadiiaspaceassistant.services.dataproviders.CosmologyDataProvider
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper
import com.sspirit.nadiiaspaceassistant.ui.utils.routesFlowStep
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Composable
fun SpaceObjectSelectionView(spaceSystem: SpaceSystem, nextRoutes: Array<String>, navigator: NavHostController) {
    ScreenWrapper(navigator) {
        Column {
            Spacer(Modifier.height(16.dp))
            SpaceObjectSelector(
                system = spaceSystem,
                hPadding = 16
            ) { spaceObject ->
                val indices = CosmologyDataProvider.indicesOf(spaceObject)
                val json = Json.encodeToString(indices)
                routesFlowStep(json, nextRoutes, navigator)
            }
        }
    }
}