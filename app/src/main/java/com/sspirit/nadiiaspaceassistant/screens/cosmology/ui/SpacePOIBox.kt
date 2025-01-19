package com.sspirit.nadiiaspaceassistant.screens.cosmology.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.IntOffset
import com.sspirit.nadiiaspaceassistant.models.cosmology.SpacePOI
import com.sspirit.nadiiaspaceassistant.ui.CenteredInfoTextCard
import com.sspirit.nadiiaspaceassistant.ui.ColoredCircle
import com.sspirit.nadiiaspaceassistant.ui.utils.poiStatusColor

//@Composable
//fun SpacePOISelector(spaceObject: SpaceObject, hPadding: Int = 0, onSelection: (SpacePOI) -> Unit) {
//    Column (Modifier.padding(horizontal = hPadding.dp)) {
//        ElementsList(spaceObject.pois) { POIBox(it, onSelection) }
//    }
//}

@Composable
fun SpacePOIBox(poi: SpacePOI, onSelection: () -> Unit) {
    Box {
        CenteredInfoTextCard(
            primary = poi.title,
            secondary = poi.subtitle
        ) {
            onSelection()
        }
        ColoredCircle(poiStatusColor(poi.status), 15, IntOffset(6,6))
    }
}