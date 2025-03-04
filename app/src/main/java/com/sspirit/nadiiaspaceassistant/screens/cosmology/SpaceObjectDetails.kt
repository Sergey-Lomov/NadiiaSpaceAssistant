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
import com.sspirit.nadiiaspaceassistant.screens.cosmology.ui.SpacePOICard
import com.sspirit.nadiiaspaceassistant.services.dataproviders.CosmologyDataProvider
import com.sspirit.nadiiaspaceassistant.services.external_monitor.ExternalMonitorManager
import com.sspirit.nadiiaspaceassistant.ui.AutosizeStyledButton
import com.sspirit.nadiiaspaceassistant.ui.CenteredRegularText
import com.sspirit.nadiiaspaceassistant.ui.ElementsList
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
            Spacer(Modifier.height(16.dp))
            ExternalMonitorButton(spaceObject)
            SpacedHorizontalDivider()
            POIsList(spaceObject, navigator)
        }
    }
}

@Composable
private fun InfoCard(obj: SpaceObject) {
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
private fun OrbitCard(obj: SpaceObject) {
    val position = CosmologyDataProvider.currentPosition(obj).toInt().toString()
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column (
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            if (obj.isOuter) {
                CenteredRegularText("Внешний объект")
            } else {
                OrbitDescriptionRow("Орбита", obj.orbit)
                Spacer(Modifier.height(8.dp))
                OrbitDescriptionRow("Положение", position)
                Spacer(Modifier.height(8.dp))
                OrbitDescriptionRow("Начальный угол", obj.initialAngle.toString())
                Spacer(Modifier.height(8.dp))
                OrbitDescriptionRow("Период обращения", obj.orbitPeriod.toString())
            }
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

@Composable
private fun POIsList(spaceObject: SpaceObject, navigator: NavHostController) {
    ElementsList(spaceObject.pois) {
        SpacePOICard(it) {
            val indices = CosmologyDataProvider.sectorMap.indicesOf(it)
            val json = Json.encodeToString(indices)
            navigator.navigateTo(Routes.SpacePOIDetails, json)
        }
    }
}

@Composable
private fun ExternalMonitorButton(obj: SpaceObject) {
    AutosizeStyledButton("Показать изображение") {
        ExternalMonitorManager.showSpaceObject(obj)
    }
}