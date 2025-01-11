package com.sspirit.nadiiaspaceassistant.screens.character

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.models.character.CharacterTraitTag
import com.sspirit.nadiiaspaceassistant.models.character.Drug
import com.sspirit.nadiiaspaceassistant.navigation.Routes
import com.sspirit.nadiiaspaceassistant.screens.building.TimeManager
import com.sspirit.nadiiaspaceassistant.services.dataproviders.CharacterDataProvider
import com.sspirit.nadiiaspaceassistant.ui.IterableListWithSpacer
import com.sspirit.nadiiaspaceassistant.ui.AutosizeStyledButton
import com.sspirit.nadiiaspaceassistant.ui.ColoredCircle
import com.sspirit.nadiiaspaceassistant.ui.HeaderText
import com.sspirit.nadiiaspaceassistant.ui.LocalSWLoadingState
import com.sspirit.nadiiaspaceassistant.ui.RegularText
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper
import com.sspirit.nadiiaspaceassistant.ui.ScrollableColumn
import com.sspirit.nadiiaspaceassistant.ui.SpacedHorizontalDivider
import com.sspirit.nadiiaspaceassistant.ui.StyledIconButton
import com.sspirit.nadiiaspaceassistant.ui.TitleValueRow
import com.sspirit.nadiiaspaceassistant.ui.utils.humanTime
import com.sspirit.nadiiaspaceassistant.ui.utils.stringsToList
import com.sspirit.nadiiaspaceassistant.utils.coroutineLaunch
import com.sspirit.nadiiaspaceassistant.utils.navigateWithModel
import com.sspirit.nadiiaspaceassistant.utils.simpleCoroutineLaunch
import com.sspirit.nadiiaspaceassistant.utils.update
import com.sspirit.nadiiaspaceassistant.viewmodels.InfoDialogViewModel

private val LocalUpdater = compositionLocalOf<MutableIntState?> { null }

@Composable
fun DrugsView(navigator: NavHostController) {
    val isLoading = remember { mutableStateOf(false) }
    val updater = remember { mutableIntStateOf(0) }
    val active = CharacterDataProvider.character.drugs
    val available = Drug.all.filter { it !in active }

    LaunchedEffect(Unit) {
        TimeManager.customTimersObservers.add(updater)
    }

    DisposableEffect(Unit) {
        onDispose {
            TimeManager.customTimersObservers.remove(updater)
        }
    }

    ScreenWrapper(navigator, "Препараты", isLoading) {
        CompositionLocalProvider(LocalUpdater provides updater) {
            ScrollableColumn {
                if (active.isNotEmpty()) {
                    HeaderText("Активные")
                    Spacer(Modifier.height(8.dp))
                    key(updater.intValue) {
                        IterableListWithSpacer(active) {
                            DrugCard(it, true, navigator)
                        }
                    }
                    SpacedHorizontalDivider()
                }

                HeaderText("Доступные")
                Spacer(Modifier.height(8.dp))
                key(updater.intValue) {
                    IterableListWithSpacer(available) {
                        DrugCard(it, false, navigator)
                    }
                }
            }
        }
    }
}

@Composable
private fun DrugCard(drug: Drug, isActive: Boolean, navigator: NavHostController) {
    Box {
        if (isActive)
            ActiveDrugCard(drug)
        else
            AvailableDrugCard(drug, navigator)

        ColoredCircle(Color(drug.color.red(), drug.color.green(), drug.color.blue()), 30, IntOffset(12,12))
    }
}

@Composable
private fun ActiveDrugCard(drug: Drug) {
    val timer = TimeManager.getCustomTimer(drug.id)
    val timeLeftValue = timer?.timeLeft?.value ?: 0.0
    val timeLeft = remember { mutableDoubleStateOf(timeLeftValue) }
    val updater = LocalUpdater.current ?: return

    if (timer != null) {
        LaunchedEffect(timer) {
            timer.addObserver(timeLeft)
        }

        DisposableEffect(timer) {
            onDispose {
                timer.removeObserver(timeLeft)
            }
        }
    }

    Card {
        Column(Modifier.padding(16.dp)) {
            HeaderText(drug.title)
            RegularText(drug.description)
            Spacer(Modifier.height(8.dp))

            if (timer != null) {
                val timeLeftString = humanTime(timeLeft.doubleValue.toInt(), true)
                TitleValueRow("Время действия", timeLeftString)
                Spacer(Modifier.height(8.dp))
            }

            Row {
                StyledIconButton(
                    icon = Icons.Default.Delete,
                    description = "Delete",
                    modifier = Modifier.weight(1f)
                ) {
                    CharacterDataProvider.character.removeDrug(drug)
                    updater.update()
                }
                Spacer(Modifier.width(8.dp))
                StyledIconButton(
                    icon = Icons.Filled.Refresh,
                    description = "Refresh",
                    modifier = Modifier.weight(1f)
                ) {
                    CharacterDataProvider.character.applyDrug(drug)
                    updater.update()
                }
            }
        }
    }
}

@Composable
private fun AvailableDrugCard(drug: Drug, navigator: NavHostController) {
    val updater = LocalUpdater.current ?: return
    val loadingState = LocalSWLoadingState.current ?: return

    Card {
        Column(Modifier.padding(16.dp)) {
            HeaderText(drug.title)
            RegularText(drug.description)
            Spacer(Modifier.height(8.dp))

            val duration = if (drug.duration > 0) humanTime(drug.duration, true) else "Мгновенно"
            TitleValueRow("Время действия", duration)

            val overlaps = drug.overlaps
                .filter { it in CharacterDataProvider.character.drugs }
                .map { it.title }
            if (overlaps.isNotEmpty()) {
                val overlapsList = stringsToList(overlaps)
                Spacer(Modifier.height(8.dp))
                RegularText("Перекрывает: \n$overlapsList")
            }

            Spacer(Modifier.height(8.dp))
            AutosizeStyledButton("Принять") {
                applyDrug(drug, loadingState, navigator)
                updater.update()
            }
        }
    }
}

private fun applyDrug(
    drug: Drug,
    loadingState: MutableState<Boolean>,
    navigator: NavHostController
) {
    when (drug) {
        Drug.Regeneron -> applyRegeneron(drug, loadingState, navigator)
        else -> CharacterDataProvider.character.applyDrug(drug)
    }
}

private fun applyRegeneron(
    drug: Drug,
    loadingState: MutableState<Boolean>,
    navigator: NavHostController
) {
    val character = CharacterDataProvider.character
    val oldTraumas = character.traitsByTag(CharacterTraitTag.TRAUMA)
    simpleCoroutineLaunch(
        state = loadingState,
        task = {
            character.applyDrug(drug) completion@{ success ->
                if (!success) return@completion

                val newTraumas = character.traitsByTag(CharacterTraitTag.TRAUMA)
                val trauma = oldTraumas.firstOrNull { it !in newTraumas }

                var info = "Излечимых травм не обнаружено"
                if (trauma != null) {
                    CharacterDataProvider.character.traits.remove(trauma)
                    info = "Регенерон исцелил травму: ${trauma.type.title}"
                }

                val model = InfoDialogViewModel(
                    title = "Исцеление",
                    info = info
                )
                model.actions["Подтвердить"] = { navigator.popBackStack() }
                navigator.navigateWithModel(Routes.InfoDialog, model)
            }
        },
    )
}