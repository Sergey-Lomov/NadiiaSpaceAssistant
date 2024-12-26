package com.sspirit.nadiiaspaceassistant.screens.cosmology

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.extensions.navigateTo
import com.sspirit.nadiiaspaceassistant.models.cosmology.SpaceSystem
import com.sspirit.nadiiaspaceassistant.navigation.Routes
import com.sspirit.nadiiaspaceassistant.services.dataproviders.CosmologyDataProvider
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper
import com.sspirit.nadiiaspaceassistant.ui.ScrollableColumn
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Composable
fun SpaceSystemDetailsView(system: SpaceSystem, navController: NavHostController) {
    ScreenWrapper(navController) {
        ScrollableColumn {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = system.info,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(8.dp)
                )
            }
            Spacer(Modifier.height(16.dp))
            HorizontalDivider(
                color = Color.LightGray
            )
            Spacer(Modifier.height(16.dp))
            SpaceObjectSelector(system = system) { obj ->
                val indices = CosmologyDataProvider.indicesOf(obj)
                val json = Json.encodeToString(indices)
                navController.navigateTo(Routes.SpaceObjectDetails, json)
            }
        }
    }
}