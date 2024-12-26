package com.sspirit.nadiiaspaceassistant.screens.building

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.sspirit.nadiiaspaceassistant.R

enum class BuildingElementsColors(private val backInt: Int, private val infoInt: Int) {
    ELEVATOR(R.color.teal_200, R.color.black),
    TELEPORT(R.color.soft_green, R.color.black),
    SHUTTLE(R.color.purple_200, R.color.black);

    @Composable fun back(): Color = colorResource(backInt)
    @Composable fun info(): Color = colorResource(infoInt)
}