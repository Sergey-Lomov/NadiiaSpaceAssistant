package com.sspirit.nadiiaspaceassistant.screens.missions.proprtyevacuation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.navigation.Routes
import com.sspirit.nadiiaspaceassistant.screens.building.DataProvider
import com.sspirit.nadiiaspaceassistant.screens.building.TimeManager
import com.sspirit.nadiiaspaceassistant.ui.HeaderText
import com.sspirit.nadiiaspaceassistant.ui.StyledIconButton
import com.sspirit.nadiiaspaceassistant.ui.utils.humanTime
import com.sspirit.nadiiaspaceassistant.utils.navigateTo
import com.sspirit.nadiiaspaceassistant.utils.navigateWithModel
import com.sspirit.nadiiaspaceassistant.viewmodels.PropertyEvacuationDashboardViewModel

@Composable
fun PropertyEvacuationStatusPanel(missionId: String, navigator: NavHostController) {
    val timeLeft = rememberSaveable { mutableDoubleStateOf(TimeManager.timeLeft.value) }
    val isTimerActive = rememberSaveable { mutableStateOf(TimeManager.isActive.value) }

    LaunchedEffect(missionId) {
        TimeManager.timeLeft.addObserver(timeLeft)
        TimeManager.isActive.addObserver(isTimerActive)
    }

    DisposableEffect(missionId) {
        onDispose {
            TimeManager.timeLeft.removeObserver(timeLeft)
            TimeManager.isActive.removeObserver(isTimerActive)
        }
    }

    Column (
        modifier = Modifier
            .height(64.dp)
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clickable {
                val building = DataProvider.getBy(missionId)?.building ?: return@clickable
                val model = PropertyEvacuationDashboardViewModel(building)
                navigator.navigateWithModel(Routes.PropertyEvacuationDashboard, model) }
    ) {
        Spacer(Modifier.height(8.dp))
        HorizontalDivider()
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            val timer = humanTime(timeLeft.doubleValue.toInt(), true)
            HeaderText(
                text = timer,
                align = TextAlign.Left,
                autofill = false
            )
            Spacer(Modifier.width(8.dp))

            if (isTimerActive.value) {
                StyledIconButton(Icons.Filled.Pause, "Pause") {
                    TimeManager.pause(false)
                }
            } else {
                StyledIconButton(Icons.Filled.PlayArrow, "Continue") {
                    TimeManager.play(false)
                }
            }
        }
    }
}