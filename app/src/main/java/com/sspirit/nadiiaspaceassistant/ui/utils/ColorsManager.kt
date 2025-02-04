package com.sspirit.nadiiaspaceassistant.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.sspirit.nadiiaspaceassistant.R
import com.sspirit.nadiiaspaceassistant.models.cosmology.SpacePOIAccessStatus
import com.sspirit.nadiiaspaceassistant.screens.cosmology.SpacePOILandingStatus

@Composable
fun poiAccessColor(status: SpacePOIAccessStatus) : Color =
    when (status) {
        SpacePOIAccessStatus.AVAILABLE -> colorResource(R.color.soft_green)
        SpacePOIAccessStatus.RESTRICTED -> colorResource(R.color.soft_yellow)
        SpacePOIAccessStatus.HIDDEN -> colorResource(R.color.soft_red)
        SpacePOIAccessStatus.UNDEFINED -> Color.Gray
    }

@Composable
fun poiLandingColor(status: SpacePOILandingStatus) : Color =
    when (status) {
        SpacePOILandingStatus.UNAVAIABLE -> colorResource(R.color.soft_red)
        SpacePOILandingStatus.UNALLOWED -> colorResource(R.color.soft_yellow)
        SpacePOILandingStatus.ALLOWED -> colorResource(R.color.soft_green)
    }