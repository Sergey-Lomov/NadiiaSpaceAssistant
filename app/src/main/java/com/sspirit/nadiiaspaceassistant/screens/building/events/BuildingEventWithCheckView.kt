package com.sspirit.nadiiaspaceassistant.screens.building.events

import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingEvent
import com.sspirit.nadiiaspaceassistant.navigation.Routes
import com.sspirit.nadiiaspaceassistant.ui.TitleValueRow
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.models.character.CharacterSkillCheck
import com.sspirit.nadiiaspaceassistant.screens.building.DataProvider
import com.sspirit.nadiiaspaceassistant.ui.AutosizeStyledButton
import com.sspirit.nadiiaspaceassistant.ui.HeaderText
import com.sspirit.nadiiaspaceassistant.ui.LoadingOverlay
import com.sspirit.nadiiaspaceassistant.ui.RegularText
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper
import com.sspirit.nadiiaspaceassistant.ui.ScrollableColumn
import com.sspirit.nadiiaspaceassistant.ui.SpacedHorizontalDivider
import com.sspirit.nadiiaspaceassistant.utils.coroutineLaunch
import com.sspirit.nadiiaspaceassistant.utils.mainLaunch
import com.sspirit.nadiiaspaceassistant.utils.navigateWithModel
import com.sspirit.nadiiaspaceassistant.viewmodels.CharacterSkillCheckViewModel
import com.sspirit.nadiiaspaceassistant.viewmodels.building.BuildingEventViewModel

private enum class State {
    UNDETERMINED,
    SUCCESS,
    FAILED
}

@Composable
fun BuildingEventWithCheckView(
    navigator: NavHostController,
    description: String,
    check: CharacterSkillCheck,
    failDescription: String,
    successDescription: String,
    onAnyCase: ((MutableState<Boolean>) -> Unit)? = null,
    onFail: ((MutableState<Boolean>) -> Unit)? = null,
    onSuccess: ((MutableState<Boolean>) -> Unit)? = null,
    customNavigation: (() -> Unit)? = null
) {
    val model = LocalModel.current ?: return
    val isLoading = remember { mutableStateOf(false) }
    val state = rememberSaveable { mutableStateOf(State.UNDETERMINED) }

    @Composable
    fun ApproveButton(handler: ((MutableState<Boolean>) -> Unit)?) =
        ResultApproveButton(model, isLoading, navigator, customNavigation) {
            onAnyCase?.invoke(isLoading)
            handler?.invoke(isLoading)
        }

    LaunchedEffect(state.value) {  }

    ScreenWrapper(navigator, "Событие") {
        LoadingOverlay(isLoading) {
            Column(Modifier.padding(16.dp)) {
                InfoCard(model.event, description)
                SpacedHorizontalDivider()

                when (state.value) {
                    State.UNDETERMINED -> {
                        Spacer(Modifier.weight(1f))
                        CheckButton(check, state, navigator)
                    }

                    State.SUCCESS -> {
                        HeaderText("Успех")
                        Spacer(Modifier.height(8.dp))
                        ScrollableColumn {  }
                        Column (Modifier.verticalScroll(rememberScrollState())) {
                            RegularText(successDescription)
                        }
                        Spacer(Modifier.weight(1f))
                        ApproveButton(onSuccess)
                    }

                    State.FAILED -> {
                        HeaderText("Провал")
                        Spacer(Modifier.height(8.dp))
                        Column (Modifier.verticalScroll(rememberScrollState())) {
                            RegularText(failDescription)
                        }
                        Spacer(Modifier.weight(1f))
                        ApproveButton(onFail)
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoCard(event: BuildingEvent, description: String) {
    Card {
        Column(Modifier.padding(16.dp)) {
            HeaderText(event.title)
            Spacer(Modifier.height(8.dp))
            RegularText(description)
            SpacedHorizontalDivider(8)
            TitleValueRow("Триггер", event.trigger)
        }
    }
}

@Composable
private fun CheckButton(check: CharacterSkillCheck, state: MutableState<State>, navigator: NavHostController) {
    AutosizeStyledButton("Пройти проверку") {
        val model = CharacterSkillCheckViewModel(
            check = check,
            onSuccess = {
                mainLaunch {
                    state.value = State.SUCCESS
                    navigator.popBackStack()
                }
            },
            onFail = {
                mainLaunch {
                    state.value = State.FAILED
                    navigator.popBackStack()
                }
            }
        )
        navigator.navigateWithModel(Routes.CharacterSkillCheck, model)
    }
}

@Composable
private fun ResultApproveButton(
    model: BuildingEventViewModel,
    loadingState: MutableState<Boolean>,
    navigator: NavHostController,
    customNavigation: (() -> Unit)?,
    handler: ((MutableState<Boolean>) -> Unit),
) {
    AutosizeStyledButton("Принять") {
        handler(loadingState)
        coroutineLaunch(
            state = loadingState,
            task = { DataProvider.removeEvent(model.missionId, model.event, model.room) },
            completion = {
                (customNavigation ?: navigator::popBackStack).invoke()
            }
        )
    }
}