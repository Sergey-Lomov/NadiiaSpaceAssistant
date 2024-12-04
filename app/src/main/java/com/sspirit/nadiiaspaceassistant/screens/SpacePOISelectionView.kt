package com.sspirit.nadiiaspaceassistant.screens

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.models.spacemap.SpaceObject
import com.sspirit.nadiiaspaceassistant.models.spacemap.SpacePOI
import com.sspirit.nadiiaspaceassistant.services.dataproviders.CosmologyDataProvider
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper
import com.sspirit.nadiiaspaceassistant.ui.SingleTextCard
import com.sspirit.nadiiaspaceassistant.ui.utils.routesFlowStep
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Composable
fun SpacePOISelectionView(spaceObject: SpaceObject, nextRoutes: Array<String>, navController: NavHostController) {
    ScreenWrapper(navController) {
        Column (
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(16.dp))
            for (poi: SpacePOI in spaceObject.pois) {
                SingleTextCard (poi.title) {
                    val systemIndex = CosmologyDataProvider.spaceMap.indexOf(spaceObject.parent)
                    val objectIndex = spaceObject.parent.objects.indexOf(spaceObject)
                    val poiIndex = spaceObject.pois.indexOf(poi)
                    val indices = arrayOf(systemIndex, objectIndex, poiIndex)
                    val json = Json.encodeToString(indices)
                    routesFlowStep(json, nextRoutes, navController)
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}