package com.sspirit.nadiiaspaceassistant.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SpacedHorizontalDivider(space: Int = 16) {
    Spacer(Modifier.height(space.dp))
    HorizontalDivider(color = Color.LightGray)
    Spacer(Modifier.height(space.dp))
}