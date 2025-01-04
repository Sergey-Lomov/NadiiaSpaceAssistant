package com.sspirit.nadiiaspaceassistant.screens.building.devices

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.models.missions.building.devices.BuildingDevice
import com.sspirit.nadiiaspaceassistant.ui.HeaderText
import com.sspirit.nadiiaspaceassistant.ui.LoadingOverlay
import com.sspirit.nadiiaspaceassistant.ui.RegularText
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper
import com.sspirit.nadiiaspaceassistant.ui.ScrollableColumn
import com.sspirit.nadiiaspaceassistant.ui.SpacedHorizontalDivider
import com.sspirit.nadiiaspaceassistant.ui.TitlesValuesList

@Composable
fun BuildingDeviceView(
    device: BuildingDevice,
    navigator: NavHostController,
    info: Map<String, () -> Any> = mapOf(),
    bottomBuilder: @Composable (MutableState<Boolean>) -> Unit
) {
    val isLoading = remember { mutableStateOf(false) }

    ScreenWrapper(navigator, "Устройство") {
        LoadingOverlay(isLoading) {
            ScrollableColumn {
                InfoCard(device, info)
                SpacedHorizontalDivider()
                bottomBuilder(isLoading)
            }
        }
    }
}

@Composable
private fun InfoCard(device: BuildingDevice, info: Map<String, () -> Any>) {
    Card {
        Column(Modifier.padding(16.dp)) {
            HeaderText(device.title)
            Spacer(Modifier.height(8.dp))
            RegularText(device.details)
            if (info.isNotEmpty()) {
                val actualMap = info.entries.associate { it.key to it.value() }
                SpacedHorizontalDivider(8)
                TitlesValuesList(actualMap)
            }
        }
    }
}