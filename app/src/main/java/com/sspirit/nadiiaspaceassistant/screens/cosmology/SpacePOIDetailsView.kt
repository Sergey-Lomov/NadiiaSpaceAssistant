package com.sspirit.nadiiaspaceassistant.screens.cosmology

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.utils.navigateTo
import com.sspirit.nadiiaspaceassistant.models.cosmology.SpacePOI
import com.sspirit.nadiiaspaceassistant.navigation.Routes
import com.sspirit.nadiiaspaceassistant.services.dataproviders.CosmologyDataProvider
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper
import com.sspirit.nadiiaspaceassistant.ui.ScrollableColumn
import com.sspirit.nadiiaspaceassistant.ui.SpacedHorizontalDivider
import com.sspirit.nadiiaspaceassistant.ui.utils.humanReadable
import com.sspirit.nadiiaspaceassistant.ui.utils.poiStatusColor
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Composable
fun SpacePOIDetailsView(poi: SpacePOI, navigator: NavHostController) {
    ScreenWrapper(navigator, "Детали POI") {
        ScrollableColumn {
            InfoCard(poi)
            Spacer(Modifier.height(16.dp))
            StatusCard(poi)
            SpacedHorizontalDivider()
            PlacesList(poi, navigator)
            SpacedHorizontalDivider()
            OfficesList(poi)
        }
    }
}

@Composable
private  fun OfficesList(poi: SpacePOI) {
    for(office in poi.offices) {
        Card (
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = office.string,
                fontSize = 18.sp,
                modifier = Modifier.padding(8.dp)
            )
        }

        if (office !== poi.offices.last()) {
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private  fun PlacesList(poi: SpacePOI, navigator: NavHostController) {

    for(place in poi.places) {
        Card (
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    val indices = CosmologyDataProvider.indicesOf(place)
                    val json = Json.encodeToString(indices)
                    navigator.navigateTo(Routes.SpacePOIPlaceDetails, json)
                }
        ) {
            Row(
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = place.type.title,
                    fontSize = 18.sp,
                )
                if (place.type.isStore) {
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = "->",
                        fontSize = 18.sp,
                        color = Color.Gray
                    )
                }
            }
        }

        if (place !== poi.places.last()) {
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun StatusCard(poi: SpacePOI) {
    Card (
        colors = CardDefaults.cardColors(containerColor = poiStatusColor(poi.status)),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = humanReadable(poi.status),
                fontSize = 18.sp,
            )

            if (poi.visitRequirements.isNotEmpty()) {
                Text(
                    text = poi.visitRequirements,
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun InfoCard(poi: SpacePOI) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = poi.info,
            fontSize = 18.sp,
            modifier = Modifier.padding(8.dp)
        )
    }
}