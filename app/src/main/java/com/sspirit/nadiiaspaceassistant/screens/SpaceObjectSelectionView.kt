package com.sspirit.nadiiaspaceassistant.screens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.gson.Gson
import com.sspirit.nadiiaspaceassistant.models.spacemap.SpaceObject
import com.sspirit.nadiiaspaceassistant.models.spacemap.SpaceSystem
import com.sspirit.nadiiaspaceassistant.services.dataproviders.CosmologyDataProvider
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper
import com.sspirit.nadiiaspaceassistant.ui.SingleTextCard
import com.sspirit.nadiiaspaceassistant.ui.utils.routesFlowStep
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Composable
fun SpaceObjectSelectionView(spaceSystem: SpaceSystem, nextRoutes: Array<String>, navController: NavHostController) {
    ScreenWrapper(navController) {
        Column (
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(16.dp))
            for (spaceObject: SpaceObject in spaceSystem.objects) {
                SingleTextCard (spaceObject.title) {
                    val systemIndex = CosmologyDataProvider.spaceMap.indexOf(spaceSystem)
                    val objectIndex = spaceSystem.objects.indexOf(spaceObject)
                    val indices = arrayOf(systemIndex, objectIndex)
                    val json = Json.encodeToString(indices)
                    routesFlowStep(json, nextRoutes, navController)
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}