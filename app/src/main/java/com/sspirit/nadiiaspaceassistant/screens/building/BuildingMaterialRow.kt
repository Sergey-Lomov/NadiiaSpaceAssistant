package com.sspirit.nadiiaspaceassistant.screens.building

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingMaterial
import com.sspirit.nadiiaspaceassistant.ui.ColoredCircle
import com.sspirit.nadiiaspaceassistant.ui.RegularText

@Composable
fun BuildingMaterialRow(material: BuildingMaterial) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        RegularText("Материал", false)
        Spacer(Modifier.weight(1f))
        RegularText(material.lucidity.string, false)

        if(material.heatImmune) {
            Spacer(Modifier.width(4.dp))
            ColoredCircle(BuildingElementsColors.HEAT_IMMUNE, 20, "Т", 14)
        }

        if(material.acidImmune) {
            Spacer(Modifier.width(4.dp))
            ColoredCircle(BuildingElementsColors.ACID_IMMUNE, 20, "К", 14)
        }

        if(material.explosionImmune) {
            Spacer(Modifier.width(4.dp))
            ColoredCircle(BuildingElementsColors.EXPLOSION_IMMUNE, 20, "В", 14)
        }
    }
}