package com.sspirit.nadiiaspaceassistant.screens.cosmology

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.sspirit.nadiiaspaceassistant.R
import com.sspirit.nadiiaspaceassistant.models.cosmology.SpaceObject
import com.sspirit.nadiiaspaceassistant.models.cosmology.SpacePOI
import com.sspirit.nadiiaspaceassistant.models.cosmology.SpacePOIStatus
import com.sspirit.nadiiaspaceassistant.ui.CenteredInfoTextCard
import com.sspirit.nadiiaspaceassistant.ui.utils.poiStatusColor

@Composable
fun SpacePOISelector(spaceObject: SpaceObject, hPadding: Int = 0, onSelection: (SpacePOI) -> Unit) {
    Column (
        modifier = Modifier.padding(horizontal = hPadding.dp)
    ) {
        for (poi: SpacePOI in spaceObject.pois) {
            Box {
                CenteredInfoTextCard(
                    primary = poi.title,
                    secondary = poi.subtitle
                ) {
                    onSelection(poi)
                }
                Box(
                    modifier = Modifier
                        .size(15.dp)
                        .offset(6.dp, 6.dp)
                        .clip(CircleShape)
                        .background(poiStatusColor(poi.status))
                )
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}