package com.sspirit.nadiiaspaceassistant.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.sspirit.nadiiaspaceassistant.R
import com.sspirit.nadiiaspaceassistant.models.cosmology.SpacePOI
import com.sspirit.nadiiaspaceassistant.models.cosmology.SpacePOIStatus

@Composable
fun poiAccessColor(poi: SpacePOI) : Color {
    return when (poi.accessStatus) {
        SpacePOIStatus.AVAILABLE -> colorResource(R.color.soft_green)
        SpacePOIStatus.RESTRICTED -> colorResource(R.color.soft_yellow)
        SpacePOIStatus.HIDDEN -> colorResource(R.color.soft_red)
        SpacePOIStatus.UNAVAILABLE -> Color.DarkGray
        SpacePOIStatus.UNDEFINED -> Color.Gray
    }
}

@Composable
fun poiLandableColor(poi: SpacePOI) : Color
    = if (poi.isLandable) colorResource(R.color.soft_green) else colorResource(R.color.soft_red)