package com.sspirit.nadiiaspaceassistant.screens.cosmology

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.models.cosmology.SpaceObject
import com.sspirit.nadiiaspaceassistant.screens.cosmology.ui.SpacePOIBox
import com.sspirit.nadiiaspaceassistant.services.dataproviders.CosmologyDataProvider
import com.sspirit.nadiiaspaceassistant.ui.ElementsList
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper
import com.sspirit.nadiiaspaceassistant.ui.ScrollableColumn
import com.sspirit.nadiiaspaceassistant.ui.utils.routesFlowStep
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Composable
fun SpacePOISelectionView(spaceObject: SpaceObject, nextRoutes: Array<String>, navigator: NavHostController) {
    ScreenWrapper(navigator, "Выбор POI") {
        ScrollableColumn {
            Spacer(Modifier.height(8.dp))
            ElementsList(spaceObject.pois) {
                SpacePOIBox(it) {
                    val indices = CosmologyDataProvider.sectorMap.indicesOf(it)
                    val json = Json.encodeToString(indices)
                    routesFlowStep(json, nextRoutes, navigator)
                }
            }
        }
    }
}