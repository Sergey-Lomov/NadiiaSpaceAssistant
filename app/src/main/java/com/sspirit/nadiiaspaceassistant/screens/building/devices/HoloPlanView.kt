package com.sspirit.nadiiaspaceassistant.screens.building.devices

import android.health.connect.datatypes.ExerciseRoute.Location
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingLocation
import com.sspirit.nadiiaspaceassistant.ui.CenteredRegularText
import com.sspirit.nadiiaspaceassistant.ui.HeaderText
import com.sspirit.nadiiaspaceassistant.ui.TitlesValuesList
import com.sspirit.nadiiaspaceassistant.ui.utils.stringsToList
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.models.missions.building.devices.BuildingDevice
import com.sspirit.nadiiaspaceassistant.screens.building.DataProvider
import com.sspirit.nadiiaspaceassistant.screens.building.TimeManager
import com.sspirit.nadiiaspaceassistant.ui.RegularText
import com.sspirit.nadiiaspaceassistant.ui.TitleValueRow
import com.sspirit.nadiiaspaceassistant.utils.flatArrayMap

private val commonDevices = arrayOf(BuildingDevice.HoloPlan::class, BuildingDevice.EnergyNode::class)

@Composable
fun HoloPlanView(missionId: String, plan: BuildingDevice.HoloPlan, navigator: NavHostController) {
    val building = DataProvider.getBy(missionId)?.building ?: return

    LaunchedEffect(Unit) {
        TimeManager.holoPlanInvestigation()
    }

    BuildingDeviceView(plan, navigator) {
        CenteredRegularText("Изучив план вы получили следующую информацию")
        Spacer(Modifier.height(8.dp))
        val comparator = compareBy<BuildingLocation> { it.sector.title }
            .thenBy { it.level }
        val locations = plan.locations
            .mapNotNull { building.location(it) }
            .sortedWith(comparator)
        for (location in locations) {
            LocationCard(location)
            if (location !== locations.last()) {
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun LocationCard(location: BuildingLocation) {
    val devices = location.rooms
        .flatArrayMap { it.devices }
        .filter { it::class !in commonDevices }
        .map { it.title }
    Card {
        Column(Modifier.padding(16.dp)) {
            HeaderText(location.title)
            Spacer(Modifier.height(8.dp))
            TitlesValuesList(
                "Сектор" to location.sector.title,
                "Этаж" to location.level,
            )
            Spacer(Modifier.height(8.dp))
            if (devices.isEmpty())
                CenteredRegularText("Устройств нет")
            else
                RegularText("Устройства:\n" + stringsToList(devices))
        }
    }
}