package com.sspirit.nadiiaspaceassistant.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.models.spacemap.SpaceObject
import com.sspirit.nadiiaspaceassistant.models.spacemap.SpacePOI
import com.sspirit.nadiiaspaceassistant.models.spacemap.SpacePOIStatus
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
                Box {
                    SingleTextCard(text = poi.title) {
                        val systemIndex = CosmologyDataProvider.spaceMap.indexOf(spaceObject.parent)
                        val objectIndex = spaceObject.parent.objects.indexOf(spaceObject)
                        val poiIndex = spaceObject.pois.indexOf(poi)
                        val indices = arrayOf(systemIndex, objectIndex, poiIndex)
                        val json = Json.encodeToString(indices)
                        routesFlowStep(json, nextRoutes, navController)
                    }
                    Box(
                        modifier = Modifier
                            .size(15.dp)
                            .offset(6.dp, 6.dp)
                            .clip(CircleShape)
                            .background(statusColor(poi.status))
                    )
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

private fun statusColor(status: SpacePOIStatus) : Color {
    return when (status) {
        SpacePOIStatus.AVAILABLE -> Color.Green
        SpacePOIStatus.UNAVAILABLE -> Color.Red
        SpacePOIStatus.HIDDEN -> Color.Yellow
        SpacePOIStatus.INVALID -> Color.Black
    }
}