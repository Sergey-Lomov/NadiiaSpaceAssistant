package com.sspirit.nadiiaspaceassistant.screens.building

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.extensions.navigateTo
import com.sspirit.nadiiaspaceassistant.models.missions.building.Building
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingSector
import com.sspirit.nadiiaspaceassistant.models.missions.building.transport.BuildingTransport
import com.sspirit.nadiiaspaceassistant.navigation.Routes
import com.sspirit.nadiiaspaceassistant.ui.ColoredCircle
import com.sspirit.nadiiaspaceassistant.ui.HeaderText
import com.sspirit.nadiiaspaceassistant.ui.RegularText
import com.sspirit.nadiiaspaceassistant.ui.TitleValueRow

@Composable
fun BuildingSectorsList(building: Building, missionId: String, navController: NavHostController) {
    if (building.sectors.isEmpty()) return

    Column {
        for(sector in building.sectors) {
            BuildingSectorCard(sector, building.transports) {
                val index = building.sectors.indexOf(sector)
                navController.navigateTo(Routes.BuildingSectorDetails, missionId, index)
            }
            if (sector != building.sectors.last()) {
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}