package com.sspirit.nadiiaspaceassistant.screens.missions.medstests

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.screens.missions.MissionDetailsView
import com.sspirit.nadiiaspaceassistant.services.dataproviders.missions.MedsTestsDataProvider
import com.sspirit.nadiiaspaceassistant.services.dataproviders.missions.MissionsPreviewsDataProvider
import com.sspirit.nadiiaspaceassistant.ui.HeaderTextCard
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper
import com.sspirit.nadiiaspaceassistant.ui.StyledButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun MedsTestsExecutionView(id: String, navController: NavHostController) {
    val step = remember { mutableIntStateOf(0) }

    ScreenWrapper(navController) {
        val mission = MedsTestsDataProvider.getBy(id) ?: return@ScreenWrapper

        Column(modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
        ) {
            when (step.intValue) {
                0 -> HeaderTextCard("Брифинг", "Для испытания нового препарата от ${mission.client} вам потербуется выполнить испытание: ${mission.trial}. Для участия в исптании небоходимо ${mission.requirements}")
                1 -> HeaderTextCard("Дорога на место испытаний", mission.place)
                2 -> HeaderTextCard("Чистый замер", mission.trial)
                3 -> HeaderTextCard("Выдача препарата", "Выдача тестового препарата испытуемым")
                4 -> HeaderTextCard("Замер под перпаратом", mission.trial)
                5 -> HeaderTextCard("Проверка побочки", "Неожиданная проверка физиологии: сложность ${mission.danger} строгость 6.")
                6 -> HeaderTextCard("Результаты", "Были ли испытания успешными? Если были и заказчик XenoPharm надо это отметить в прогрессе развития препаратов.")
                7 -> HeaderTextCard("Награда", "- Игрок получает основную награду ${mission.reward} кредитов\n- Игрок получает доп. компенсацию ${mission.additionalReward} кредитов если пострадал от побочки\n- Игроку предалагают соответствующий препарат, если побочки небыло и испытания успешны")
            }

            Spacer(Modifier.height(16.dp))
            ControlPanel(step, mission.id, navController)
        }
    }
}

@Composable
fun ControlPanel(step: MutableIntState, id: String, navController: NavHostController) {
    if (step.intValue < 7)
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            StyledButton(
                title = "<-",
                modifier = Modifier.weight(1f)
            ) {
                step.intValue--
            }
            Spacer(Modifier.width(16.dp))
            StyledButton(
                title = "->",
                modifier = Modifier.weight(1f)
            ) {
                step.intValue++
            }
        }

    else
        StyledButton(
            title = "Завершить",
            modifier = Modifier.fillMaxWidth()
        ) {
            CoroutineScope(Dispatchers.IO).launch {
                MissionsPreviewsDataProvider.complete(id)
            }
            navController.popBackStack(navController.graph.startDestinationId, inclusive = false)
            navController.navigate(navController.graph.startDestinationId)
        }
}