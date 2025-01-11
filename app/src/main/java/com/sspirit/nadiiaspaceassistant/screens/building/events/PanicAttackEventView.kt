package com.sspirit.nadiiaspaceassistant.screens.building.events

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.navigation.Routes
import com.sspirit.nadiiaspaceassistant.screens.building.DataProvider
import com.sspirit.nadiiaspaceassistant.screens.building.TimeManager
import com.sspirit.nadiiaspaceassistant.services.SkillChecksManager
import com.sspirit.nadiiaspaceassistant.ui.AutosizeStyledButton
import com.sspirit.nadiiaspaceassistant.ui.CenteredRegularText
import com.sspirit.nadiiaspaceassistant.ui.HeaderText
import com.sspirit.nadiiaspaceassistant.ui.LoadingOverlay
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper
import com.sspirit.nadiiaspaceassistant.utils.mainLaunch
import com.sspirit.nadiiaspaceassistant.utils.navigateWithModel
import com.sspirit.nadiiaspaceassistant.utils.simpleCoroutineLaunch
import com.sspirit.nadiiaspaceassistant.viewmodels.CharacterSkillCheckViewModel
import com.sspirit.nadiiaspaceassistant.viewmodels.building.BuildingEventViewModel

private const val RETREAT_LIMIT = 120

private enum class PanicState {
    BEGINNING,
    PANIC,
    TRY_TO_STABILIZE,
    STABILIZED,
    FORCED_RETREAT
}

@Composable
fun PanicAttackEventView(navigator: NavHostController) {
    val model = LocalModel.current ?: return
    val state = rememberSaveable { mutableStateOf(PanicState.BEGINNING) }
    val isLoading = rememberSaveable { mutableStateOf(false) }

    ScreenWrapper(navigator, "Событие") {
        LoadingOverlay(isLoading) {
            Column(Modifier.padding(16.dp)) {
                HeaderText("Паническая атака")
                Spacer(Modifier.height(16.dp))
                StateText(state.value)
                Spacer(Modifier.weight(1f))
                StateButton(model, state, isLoading, navigator)
            }
        }
    }
}

@Composable
private fun StateText(state: PanicState) {
    val text = when (state) {
        PanicState.BEGINNING -> "Вы чувствуете себя незначительным муравьем, внутри пустого и давно вымершего комплекса. Лабиринт ярко освещенных и абсолютно темных комнат сжимается вокруг вас. В нем только вы и незибежаная катострофа. Чувство усиливается. Похоже у вас начинается паническая атака!"
        PanicState.PANIC -> "Паника захлестывает вас! Забившись в ближайший угол вы отдаетесь отчаянию в течении ${TimeManager.panicAttackFail} сек."
        PanicState.TRY_TO_STABILIZE -> "Кажется вам стало полегче, пора попробовать взять себя в руки"
        PanicState.STABILIZED -> "Вы справились! Паника отступает!"
        PanicState.FORCED_RETREAT -> "Похоже комплекс вот вот будет разрушен! Вы в панике бежите к своему кораблю!"
    }
    CenteredRegularText(text)
}

@Composable
private fun StateButton(
    model: BuildingEventViewModel,
    state: MutableState<PanicState>,
    loadingState: MutableState<Boolean>,
    navigator: NavHostController
) {
    when (state.value) {
        PanicState.BEGINNING ->
            CheckButton(state, navigator)
        PanicState.PANIC ->
            AutosizeStyledButton("Паниковать") {
                TimeManager.panicAttackFail()
                if (TimeManager.timeLeft.value > RETREAT_LIMIT)
                    state.value = PanicState.TRY_TO_STABILIZE
                else
                    state.value = PanicState.FORCED_RETREAT
            }
        PanicState.TRY_TO_STABILIZE ->
            CheckButton(state, navigator)
        PanicState.STABILIZED ->
            FinishButton("Прийти в себя", model, loadingState, navigator)
        PanicState.FORCED_RETREAT ->
            FinishButton("Бежать", model, loadingState, navigator)
    }
}

@Composable
private fun FinishButton(
    title: String,
    model: BuildingEventViewModel,
    loadingState: MutableState<Boolean>,
    navigator: NavHostController
) {
    AutosizeStyledButton(title) {
        simpleCoroutineLaunch(loadingState) {
            DataProvider.removeEvent(model.missionId, model.event, model.room) {
                if (it) mainLaunch {
                    navigator.popBackStack()
                }
            }
        }
    }
}

@Composable
private fun CheckButton(state: MutableState<PanicState>, navigator: NavHostController) {
    AutosizeStyledButton("Начать проверку") {
        val model = CharacterSkillCheckViewModel(
            check = SkillChecksManager.panicAttackEvent(),
            onSuccess = {
                mainLaunch {
                    state.value = PanicState.STABILIZED
                    navigator.popBackStack()
                }
            },
            onFail = {
                mainLaunch {
                    state.value = PanicState.PANIC
                    navigator.popBackStack()
                }
            }
        )
        navigator.navigateWithModel(Routes.CharacterSkillCheck, model)
    }
}