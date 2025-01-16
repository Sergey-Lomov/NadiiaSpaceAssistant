package com.sspirit.nadiiaspaceassistant.screens.missions.medstests

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.models.character.CharacterTrait
import com.sspirit.nadiiaspaceassistant.navigation.Routes
import com.sspirit.nadiiaspaceassistant.screens.missions.MissionStepControlPanel
import com.sspirit.nadiiaspaceassistant.services.SkillChecksManager
import com.sspirit.nadiiaspaceassistant.services.dataproviders.CharacterDataProvider
import com.sspirit.nadiiaspaceassistant.services.dataproviders.missions.MedsTestsDataProvider
import com.sspirit.nadiiaspaceassistant.services.fabrics.CharacterTraitsGenerator
import com.sspirit.nadiiaspaceassistant.ui.AutosizeStyledButton
import com.sspirit.nadiiaspaceassistant.ui.HeaderTextCard
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper
import com.sspirit.nadiiaspaceassistant.ui.ScrollableColumn
import com.sspirit.nadiiaspaceassistant.ui.SpacedHorizontalDivider
import com.sspirit.nadiiaspaceassistant.utils.navigateWithModel
import com.sspirit.nadiiaspaceassistant.utils.simpleCoroutineLaunch
import com.sspirit.nadiiaspaceassistant.viewmodels.CharacterSkillCheckViewModel
import com.sspirit.nadiiaspaceassistant.viewmodels.InfoDialogViewModel

@Composable
fun MedsTestsExecutionView(id: String, navigator: NavHostController) {
    val step = remember { mutableIntStateOf(0) }

    ScreenWrapper(navigator, "Миссия") {
        val mission = MedsTestsDataProvider.getBy(id) ?: return@ScreenWrapper

        ScrollableColumn {
            when (step.intValue) {
                0 -> HeaderTextCard("Брифинг", "Для испытания нового препарата от ${mission.client} вам потербуется выполнить испытание: ${mission.trial}. Для участия в исптании небоходимо ${mission.requirements}")
                1 -> HeaderTextCard("Дорога на место испытаний", mission.place)
                2 -> HeaderTextCard("Чистый замер", mission.trial)
                3 -> HeaderTextCard("Выдача препарата", "Выдача тестового препарата испытуемым")
                4 -> HeaderTextCard("Замер под перпаратом", mission.trial)
                5 -> HeaderTextCard("Побочные эффекты", "Неожиданная проверка физиологии: сложность ${mission.danger} строгость 6.")
                6 -> HeaderTextCard("Результаты", "Были ли испытания успешными? Если были и заказчик XenoPharm надо это отметить в прогрессе развития препаратов.")
                7 -> HeaderTextCard("Награда", "- Игрок получает основную награду ${mission.reward} кредитов\n\n- Игрок получает доп. компенсацию ${mission.additionalReward} кредитов если пострадал от побочки\n\n- Игроку предалагают соответствующий препарат, если побочки небыло и испытания успешны\n\n- Если заказчик XenoPharm то улучшаяются отношения с компанией (максимум 5)")
            }

            SpacedHorizontalDivider()
            StepControls(step.intValue, navigator)
            MissionStepControlPanel(step, 7, mission.id, navigator)
        }
    }
}

@Composable
fun StepControls(step: Int, navigator: NavHostController) {
    if (step != 5) return

    AutosizeStyledButton("Пройти проверку") {
        val model = CharacterSkillCheckViewModel(
            check = SkillChecksManager.medsTestSideEffect(),
            onSuccess = { onSideEffectCheckSuccess(navigator) },
            onFail = { onSideEffectCheckFail(navigator) }
        )
        navigator.navigateWithModel(Routes.CharacterSkillCheck, model)
    }
    Spacer(Modifier.height(8.dp))
}

private fun onSideEffectCheckSuccess(navigator: NavHostController) {
    val model = InfoDialogViewModel(
        title = "Успех",
        info = "Мощное телосложение пзоволило избежать любых побочных эффектов!"
    )
    model.actions["Принять"] = {
        navigator.popBackStack()
        navigator.popBackStack()
    }
    navigator.navigateWithModel(Routes.InfoDialog, model)
}

private fun onSideEffectCheckFail(navigator: NavHostController) {
    val model = InfoDialogViewModel(
        title = "Провал",
        info = "Из-за недоработанности препаратов проявляются побочные эффекты, зависящие от того какой навык препараты должны были улучшать."
    )

    model.actions["Слабость (сила)"] = { loadingState ->
        val trait = CharacterTraitsGenerator.sideEffectWeakness()
        applyTrait(trait, loadingState, navigator)
    }

    model.actions["Вялость (ловкость)"] = { loadingState ->
        val trait = CharacterTraitsGenerator.sideEffectLethargic()
        applyTrait(trait, loadingState, navigator)
    }

    model.actions["Мигрень (разум)"] = { loadingState ->
        val trait = CharacterTraitsGenerator.sideEffectMigraine()
        applyTrait(trait, loadingState, navigator)
    }

    navigator.navigateWithModel(Routes.InfoDialog, model)
}

private fun applyTrait(
    trait: CharacterTrait,
    loadingState:MutableState<Boolean>,
    navigator: NavHostController
) {
    simpleCoroutineLaunch(loadingState) {
        CharacterDataProvider.addTrait(trait) { success ->
            if (!success) return@addTrait

            val model = InfoDialogViewModel.newTrait(trait)
            model.actions["Принять"] = {
                navigator.popBackStack()
                navigator.popBackStack()
                navigator.popBackStack()
            }
            navigator.navigateWithModel(Routes.InfoDialog, model)
        }
    }
}