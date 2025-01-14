package com.sspirit.nadiiaspaceassistant.screens.cosmology

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.utils.navigateTo
import com.sspirit.nadiiaspaceassistant.models.cosmology.SpaceObject
import com.sspirit.nadiiaspaceassistant.navigation.Routes
import com.sspirit.nadiiaspaceassistant.services.dataproviders.CosmologyDataProvider
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper
import com.sspirit.nadiiaspaceassistant.ui.ScrollableColumn
import com.sspirit.nadiiaspaceassistant.ui.SpacedHorizontalDivider
import com.sspirit.nadiiaspaceassistant.ui.TitleValueRow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Composable
fun SpaceObjectDetailsView(spaceObject: SpaceObject, navigator: NavHostController) {
    ScreenWrapper(navigator, "Косм. объект") {
        ScrollableColumn {
            InfoCard(spaceObject)
            Spacer(Modifier.height(16.dp))
            OrbitCard(spaceObject)
            SpacedHorizontalDivider()
            SpacePOISelector(
                spaceObject = spaceObject,
            ) { poi ->
                val indices = CosmologyDataProvider.indicesOf(poi)
                val json = Json.encodeToString(indices)
                navigator.navigateTo(Routes.SpacePOIDetails, json)
            }
        }
    }
}

@Composable
fun InfoCard(obj: SpaceObject) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = obj.info,
            fontSize = 18.sp,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Composable
fun OrbitCard(obj: SpaceObject) {
    val position = CosmologyDataProvider.currentPosition(obj).toInt().toString()
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column (
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(8.dp))
            OrbitDescriptionRow("Орбита", obj.orbit)
            Spacer(Modifier.height(8.dp))
            OrbitDescriptionRow("Положение", position)
            Spacer(Modifier.height(8.dp))
            OrbitDescriptionRow("Начальный угол", obj.initalAngle.toString())
            Spacer(Modifier.height(8.dp))
            OrbitDescriptionRow("Период обращения", obj.orbitPeriod.toString())
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun OrbitDescriptionRow(title: String, value: String) {
    TitleValueRow(
        title = title,
        value = value,
        fontSize = 18,
        fontWeight = FontWeight.Normal
    )
}