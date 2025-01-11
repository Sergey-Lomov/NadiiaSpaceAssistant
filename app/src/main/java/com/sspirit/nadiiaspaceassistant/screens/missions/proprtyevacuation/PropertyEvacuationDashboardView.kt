package com.sspirit.nadiiaspaceassistant.screens.missions.proprtyevacuation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
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
import com.sspirit.nadiiaspaceassistant.screens.building.TimeManager
import com.sspirit.nadiiaspaceassistant.ui.AutosizeStyledButton
import com.sspirit.nadiiaspaceassistant.ui.HeaderText
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper
import com.sspirit.nadiiaspaceassistant.ui.ScrollableColumn
import com.sspirit.nadiiaspaceassistant.ui.SpacedHorizontalDivider
import com.sspirit.nadiiaspaceassistant.ui.StyledIconButton
import com.sspirit.nadiiaspaceassistant.ui.utils.humanTime
import com.sspirit.nadiiaspaceassistant.utils.navigateTo

@Composable
fun PropertyEvacuationDashboardView(navigator: NavHostController) {
    ScreenWrapper(navigator, "Управление") {
        ScrollableColumn {
            TimersPanel()
            SpacedHorizontalDivider()
            MedsButton(navigator)
        }
    }
}

@Composable
fun TimersPanel() {
    val timeLeft = remember { mutableDoubleStateOf(0.0) }
    val isTimerActive = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        TimeManager.timeLeft.addObserver(timeLeft)
        TimeManager.isActive.addObserver(isTimerActive)
    }

    DisposableEffect(Unit) {
        onDispose {
            TimeManager.timeLeft.removeObserver(timeLeft)
            TimeManager.isActive.removeObserver(isTimerActive)
        }
    }

    val timer = humanTime(timeLeft.doubleValue.toInt(), true)
    Column(Modifier.fillMaxWidth()) {
        HeaderText(timer,TextAlign.Left)
        Spacer(Modifier.height(8.dp))
        if (isTimerActive.value) {
            AutosizeStyledButton("Пауза") {
                TimeManager.pause()
            }
        } else {
            AutosizeStyledButton("Продолжить") {
                TimeManager.play()
            }
        }
    }
}

@Composable
private fun MedsButton(navigator: NavHostController) {
    AutosizeStyledButton("Препараты") {
        navigator.navigateTo(Routes.CharacterDrugs)
    }
}