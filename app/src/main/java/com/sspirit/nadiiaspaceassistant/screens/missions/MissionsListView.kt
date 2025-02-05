package com.sspirit.nadiiaspaceassistant.screens.missions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.utils.navigateTo
import com.sspirit.nadiiaspaceassistant.models.missions.MissionPreview
import com.sspirit.nadiiaspaceassistant.models.missions.MissionType
import com.sspirit.nadiiaspaceassistant.navigation.Routes
import com.sspirit.nadiiaspaceassistant.services.dataproviders.CharacterDataProvider
import com.sspirit.nadiiaspaceassistant.services.dataproviders.missions.MedsTestsDataProvider
import com.sspirit.nadiiaspaceassistant.services.dataproviders.missions.MissionsListDataProvider
import com.sspirit.nadiiaspaceassistant.ui.AutosizeStyledButton
import com.sspirit.nadiiaspaceassistant.ui.CoroutineLaunchedEffect
import com.sspirit.nadiiaspaceassistant.ui.OptionPicker
import com.sspirit.nadiiaspaceassistant.ui.OptionsPickerItem
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper
import com.sspirit.nadiiaspaceassistant.ui.ScrollableColumn
import com.sspirit.nadiiaspaceassistant.ui.utils.humanReadable
import com.sspirit.nadiiaspaceassistant.ui.utils.humanReadableDifficult

@Composable
fun MissionsListView(navigator: NavHostController) {
    val isLoading = remember { mutableStateOf(true) }
    val showTypePicker = remember { mutableStateOf(false) }

    CoroutineLaunchedEffect(loadingState = isLoading) {
        CharacterDataProvider.downloadCharacter()
    }

    ScreenWrapper(navigator, "Список миссий", isLoading) {
        ScrollableColumn {
            val missions = MissionsListDataProvider.activePreviews
            for (mission in missions) {
                MissionCard(mission, navigator)
                Spacer(Modifier.height(16.dp))
            }

            HorizontalDivider(color = Color.LightGray)
            Spacer(Modifier.height(16.dp))
            AutosizeStyledButton("Добавить") {
                showTypePicker.value = true
            }
        }

        if (showTypePicker.value) {
            val options = MissionType.entries
                .filter { it != MissionType.UNDEFINED }
                .map { OptionsPickerItem(it, humanReadable(it)) }
                .toTypedArray()
            OptionPicker(options, showTypePicker) { option ->
                when (option) {
                    MissionType.MEDS_TEST -> {
                        MedsTestsDataProvider.regenerateProposal()
                        navigator.navigateTo(Routes.MedsTestsProposal)
                    }
                    MissionType.ENERGY_LINES -> {
                        navigator.navigateTo(Routes.EnergyLinesProposal)
                    }
                    else -> Unit
                }
            }
        }
    }
}

@Composable
private fun MissionCard(mission: MissionPreview, navigator: NavHostController) {
    Card(modifier = Modifier
        .clickable {
            val route = when (mission.type) {
                MissionType.MEDS_TEST -> Routes.MedsTestsDetails
                MissionType.ENERGY_LINES -> Routes.EnergyLinesDetails
                MissionType.PROPERTY_EVACUATION -> Routes.PropertyEvacuationDetails
                else -> null
            }

            if (route != null) {
                navigator.navigateTo(route, mission.id)
            }
        }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = humanReadable(mission.type),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .wrapContentHeight(align = CenterVertically),
            )
            Text(
                text = mission.description,
                fontSize = 18.sp,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Срок ожидания: " + mission.expiration.toString(),
                fontSize = 18.sp,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Награда: " + mission.reward,
                fontSize = 18.sp,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Локация: " + mission.place,
                fontSize = 18.sp,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Сложность: ${humanReadableDifficult(mission.difficult)}",
                fontSize = 18.sp,
            )
        }
    }
}