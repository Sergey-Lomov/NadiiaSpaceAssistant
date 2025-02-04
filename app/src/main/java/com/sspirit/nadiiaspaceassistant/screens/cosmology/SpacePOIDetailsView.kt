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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.R
import com.sspirit.nadiiaspaceassistant.models.CosmonavigationTask
import com.sspirit.nadiiaspaceassistant.models.character.CharacterSkillType
import com.sspirit.nadiiaspaceassistant.utils.navigateTo
import com.sspirit.nadiiaspaceassistant.models.cosmology.SpacePOI
import com.sspirit.nadiiaspaceassistant.models.cosmology.SpacePOIOffice
import com.sspirit.nadiiaspaceassistant.models.cosmology.SpacePOIPlace
import com.sspirit.nadiiaspaceassistant.navigation.Routes
import com.sspirit.nadiiaspaceassistant.screens.cosmology.ui.SpacePOICard
import com.sspirit.nadiiaspaceassistant.services.dataproviders.CharacterDataProvider
import com.sspirit.nadiiaspaceassistant.services.dataproviders.CosmologyDataProvider
import com.sspirit.nadiiaspaceassistant.ui.ElementsList
import com.sspirit.nadiiaspaceassistant.ui.RegularText
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper
import com.sspirit.nadiiaspaceassistant.ui.ScrollableColumn
import com.sspirit.nadiiaspaceassistant.ui.SpacedHorizontalDivider
import com.sspirit.nadiiaspaceassistant.ui.utils.humanReadable
import com.sspirit.nadiiaspaceassistant.ui.utils.poiAccessColor
import com.sspirit.nadiiaspaceassistant.ui.utils.poiLandingColor
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

enum class SpacePOILandingStatus {
    UNAVAIABLE,
    UNALLOWED,
    ALLOWED;

    companion object {
        fun statusFor(poi: SpacePOI): SpacePOILandingStatus {
            return if (poi.isLandable) {
                val skill = CharacterDataProvider.character.level(CharacterSkillType.PILOTING)
                val allowed = CosmonavigationTask.isAllowedForSkill(skill, poi.navigationLengthMultiplier, poi.navigationTimeMultiplier)
                if (allowed) ALLOWED else UNALLOWED
            } else
                UNAVAIABLE
        }
    }
}

@Composable
fun SpacePOIDetailsView(poi: SpacePOI, navigator: NavHostController) {
    ScreenWrapper(navigator, "Детали POI") {
        ScrollableColumn {
            InfoCard(poi)
            Spacer(Modifier.height(8.dp))
            LandingCard(poi)
            Spacer(Modifier.height(8.dp))
            StatusCard(poi)
            ConnectionsList(poi.connectedPOIs, navigator)
            SpacedHorizontalDivider()
            ElementsList(poi.places) { PlaceCard(it, navigator) }
            SpacedHorizontalDivider()
            ElementsList(poi.offices) { OfficeCard(it) }
        }
    }
}

@Composable
private  fun OfficeCard(office: SpacePOIOffice) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = office.string,
            fontSize = 18.sp,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Composable
private  fun PlaceCard(place: SpacePOIPlace, navigator: NavHostController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val indices = CosmologyDataProvider.sectorMap.indicesOf(place)
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
}

@Composable
private fun LandingCard(poi: SpacePOI) {
    val status = SpacePOILandingStatus.statusFor(poi)
    val colors = CardDefaults.cardColors(containerColor = poiLandingColor(status))
    Card (
        colors = colors,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            RegularText(humanReadable(status))
        }
    }
}

@Composable
private fun StatusCard(poi: SpacePOI) {
    Card (
        colors = CardDefaults.cardColors(containerColor = poiAccessColor(poi.accessStatus)),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            RegularText(humanReadable(poi.accessStatus))

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

@Composable
private fun ConnectionsList(pois: Array<SpacePOI>, navigator: NavHostController) {
    if (pois.isEmpty()) return
    SpacedHorizontalDivider()
    ElementsList(pois) {
        SpacePOICard(it) {
            val indices = CosmologyDataProvider.sectorMap.indicesOf(it)
            val json = Json.encodeToString(indices)
            navigator.navigateTo(Routes.SpacePOIDetails, json)
        }
    }
}