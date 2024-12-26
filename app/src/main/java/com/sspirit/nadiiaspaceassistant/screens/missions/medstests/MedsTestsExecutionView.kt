package com.sspirit.nadiiaspaceassistant.screens.missions.medstests

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.screens.missions.MissionStepControlPanel
import com.sspirit.nadiiaspaceassistant.services.dataproviders.missions.MedsTestsDataProvider
import com.sspirit.nadiiaspaceassistant.ui.HeaderTextCard
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper
import com.sspirit.nadiiaspaceassistant.ui.ScrollableColumn

@Composable
fun MedsTestsExecutionView(id: String, navController: NavHostController) {
    val step = remember { mutableIntStateOf(0) }

    ScreenWrapper(navController) {
        val mission = MedsTestsDataProvider.getBy(id) ?: return@ScreenWrapper

        ScrollableColumn {
            when (step.intValue) {
                0 -> HeaderTextCard("Брифинг", "Для испытания нового препарата от ${mission.client} вам потербуется выполнить испытание: ${mission.trial}. Для участия в исптании небоходимо ${mission.requirements}")
                1 -> HeaderTextCard("Дорога на место испытаний", mission.place)
                2 -> HeaderTextCard("Чистый замер", mission.trial)
                3 -> HeaderTextCard("Выдача препарата", "Выдача тестового препарата испытуемым")
                4 -> HeaderTextCard("Замер под перпаратом", mission.trial)
                5 -> HeaderTextCard("Проверка побочки", "Неожиданная проверка физиологии: сложность ${mission.danger} строгость 6.")
                6 -> HeaderTextCard("Результаты", "Были ли испытания успешными? Если были и заказчик XenoPharm надо это отметить в прогрессе развития препаратов.")
                7 -> HeaderTextCard("Награда", "- Игрок получает основную награду ${mission.reward} кредитов\n\n- Игрок получает доп. компенсацию ${mission.additionalReward} кредитов если пострадал от побочки\n\n- Игроку предалагают соответствующий препарат, если побочки небыло и испытания успешны\n\n- Если заказчик XenoPharm то улучшаяются отношения с компанией (максимум 5)")
            }

            Spacer(Modifier.height(16.dp))
            MissionStepControlPanel(step, 7, mission.id, navController)
        }
    }
}