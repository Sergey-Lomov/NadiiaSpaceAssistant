package com.sspirit.nadiiaspaceassistant.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.sspirit.nadiiaspaceassistant.R
import com.sspirit.nadiiaspaceassistant.models.cosmology.SpacePOIStatus

@Composable
fun poiStatusColor(status: SpacePOIStatus) : Color {
    return when (status) {
        SpacePOIStatus.AVAILABLE -> colorResource(R.color.soft_green)
        SpacePOIStatus.RESTRICTED -> colorResource(R.color.soft_yellow)
        SpacePOIStatus.HIDDEN -> colorResource(R.color.soft_red)
        SpacePOIStatus.UNDEFINED -> Color.Gray
    }
}