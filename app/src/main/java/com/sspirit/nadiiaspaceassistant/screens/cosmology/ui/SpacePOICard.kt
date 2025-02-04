package com.sspirit.nadiiaspaceassistant.screens.cosmology.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.IntOffset
import com.sspirit.nadiiaspaceassistant.models.cosmology.SpacePOI
import com.sspirit.nadiiaspaceassistant.screens.cosmology.SpacePOILandingStatus
import com.sspirit.nadiiaspaceassistant.ui.CenteredInfoTextCard
import com.sspirit.nadiiaspaceassistant.ui.ColoredCircle
import com.sspirit.nadiiaspaceassistant.ui.utils.poiAccessColor
import com.sspirit.nadiiaspaceassistant.ui.utils.poiLandingColor

@Composable
fun SpacePOICard(poi: SpacePOI, onSelection: () -> Unit) {
    Box {
        CenteredInfoTextCard(
            primary = poi.title,
            secondary = poi.subtitle
        ) {
            onSelection()
        }
        val landingStatus = SpacePOILandingStatus.statusFor(poi)
        ColoredCircle(poiLandingColor(landingStatus), 15, IntOffset(6,6))
        ColoredCircle(poiAccessColor(poi.accessStatus), 15, IntOffset(6,27))
    }
}