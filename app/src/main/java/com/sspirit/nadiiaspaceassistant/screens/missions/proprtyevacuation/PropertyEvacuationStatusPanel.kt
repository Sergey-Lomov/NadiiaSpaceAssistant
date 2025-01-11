package com.sspirit.nadiiaspaceassistant.screens.missions.proprtyevacuation

import androidx.compose.foundation.clickable
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

@Composable
fun PropertyEvacuationStatusPanel(missionId: String, navigator: NavHostController) {
    val mission = DataProvider.getBy(missionId) ?: return
    val timeLeft = remember { mutableDoubleStateOf(0.0) }
    val isTimerActive = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        TimeManager.timeLeft.addObserver(timeLeft)
        TimeManager.isActive.addObserver(isTimerActive)
        TimeManager.setupTimeLeft(mission.time)
    }

    DisposableEffect(Unit) {
        onDispose {
            TimeManager.timeLeft.removeObserver(timeLeft)
            TimeManager.isActive.removeObserver(isTimerActive)
        }
    }

    Column (
        Modifier
            .height(64.dp)
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clickable { navigator.navigateTo(Routes.PropertyEvacuationDashboard) }
    ) {
        HorizontalDivider()
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
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
                    TimeManager.pause()
                }
            } else {
                StyledIconButton(Icons.Filled.PlayArrow, "Continue") {
                    TimeManager.play()
                }
            }
        }
    }
}