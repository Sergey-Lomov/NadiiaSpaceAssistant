package com.sspirit.nadiiaspaceassistant.screens.missions.proprtyevacuation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.models.missions.building.Building
import com.sspirit.nadiiaspaceassistant.navigation.Routes
import com.sspirit.nadiiaspaceassistant.screens.building.TimeManager
import com.sspirit.nadiiaspaceassistant.services.CustomTimer
import com.sspirit.nadiiaspaceassistant.services.ViewModelsRegister
import com.sspirit.nadiiaspaceassistant.ui.AutosizeStyledButton
import com.sspirit.nadiiaspaceassistant.ui.HeaderText
import com.sspirit.nadiiaspaceassistant.ui.ElementsList
import com.sspirit.nadiiaspaceassistant.ui.RegularText
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper
import com.sspirit.nadiiaspaceassistant.ui.ScrollableColumn
import com.sspirit.nadiiaspaceassistant.ui.SpacedHorizontalDivider
import com.sspirit.nadiiaspaceassistant.ui.StyledButton
import com.sspirit.nadiiaspaceassistant.ui.TitleValueRow
import com.sspirit.nadiiaspaceassistant.ui.utils.humanTime
import com.sspirit.nadiiaspaceassistant.utils.navigateTo
import com.sspirit.nadiiaspaceassistant.viewmodels.PropertyEvacuationDashboardViewModel

@Composable
fun PropertyEvacuationDashboardView(modelId: String, navigator: NavHostController) {
    ScreenWrapper(navigator, "Управление") {
        val model = ViewModelsRegister.get<PropertyEvacuationDashboardViewModel>(modelId) ?: return@ScreenWrapper
        ScrollableColumn {
            TimersPanel()
            SpecialLootPanel(model.building)
            SpacedHorizontalDivider()
            MedsButton(navigator)
            Spacer(Modifier.height(8.dp))
            TraitsButton(navigator)
        }
    }
}

@Composable
fun TimersPanel() {
    val timeLeft = rememberSaveable { mutableDoubleStateOf(TimeManager.timeLeft.value) }
    val isTimerActive = rememberSaveable { mutableStateOf(TimeManager.isActive.value) }

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
        HeaderText(timer)
        Spacer(Modifier.height(8.dp))
        ElementsList(TimeManager.customTimers.values) { CustomTimerRow(it) }
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
        Spacer(Modifier.height(8.dp))

        Row {
            StyledButton("+5 сек", modifier = Modifier.weight(1f)) {
                TimeManager.changeTimeLeft(5.0)
            }
            Spacer(Modifier.width(16.dp))
            StyledButton("-5 сек", modifier = Modifier.weight(1f)) {
                TimeManager.changeTimeLeft(-5.0)
            }
        }
        Spacer(Modifier.height(8.dp))

        Row {
            StyledButton("+1 мин", modifier = Modifier.weight(1f)) {
                TimeManager.changeTimeLeft(60.0)
            }
            Spacer(Modifier.width(16.dp))
            StyledButton("-1 мин", modifier = Modifier.weight(1f)) {
                TimeManager.changeTimeLeft(-60.0)
            }
        }
    }
}

@Composable
private fun SpecialLootPanel(building: Building) {
    val specialLoot = building.specLoot.filter { it.room == null }
    if (specialLoot.isEmpty()) return
    SpacedHorizontalDivider()
    HeaderText("Спец. лут")
    Spacer(Modifier.height(8.dp))
    ElementsList(specialLoot) {
        RegularText("- ${it.loot.title}")
    }
}

@Composable
private fun CustomTimerRow(timer: CustomTimer) {
    val timeLeft = remember { mutableDoubleStateOf(timer.timeLeft.value) }

    LaunchedEffect(timer) {
        timer.addObserver(timeLeft)
    }
    DisposableEffect(timer) {
        onDispose {
            timer.removeObserver(timeLeft)
        }
    }

    val timeLeftString = humanTime(timeLeft.doubleValue.toInt(), true)
    TitleValueRow(timer.title, timeLeftString)
}

@Composable
private fun MedsButton(navigator: NavHostController) {
    AutosizeStyledButton("Препараты") {
        navigator.navigateTo(Routes.CharacterDrugs)
    }
}

@Composable
private fun TraitsButton(navigator: NavHostController) {
    AutosizeStyledButton("Особенности") {
        navigator.navigateTo(Routes.CharacterTraits)
    }
}