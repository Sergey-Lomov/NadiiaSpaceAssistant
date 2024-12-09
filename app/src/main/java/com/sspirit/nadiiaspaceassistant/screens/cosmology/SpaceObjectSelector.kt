package com.sspirit.nadiiaspaceassistant.screens.cosmology

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sspirit.nadiiaspaceassistant.models.cosmology.SpaceObject
import com.sspirit.nadiiaspaceassistant.models.cosmology.SpaceSystem
import com.sspirit.nadiiaspaceassistant.ui.CenteredInfoTextCard

@Composable
fun SpaceObjectSelector(system: SpaceSystem, hPadding: Int = 0, onSelection: (SpaceObject) -> Unit) {
    Column (
        modifier = Modifier.padding(horizontal = hPadding.dp)
    ) {
        for (spaceObject: SpaceObject in system.objects) {
            CenteredInfoTextCard (spaceObject.title) {
                onSelection(spaceObject)
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}