package com.sspirit.nadiiaspaceassistant.screens.missions

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sspirit.nadiiaspaceassistant.extensions.toString
import com.sspirit.nadiiaspaceassistant.models.missions.EnergyLines
import com.sspirit.nadiiaspaceassistant.models.missions.MedsTests
import com.sspirit.nadiiaspaceassistant.ui.TitlesValuesList
import com.sspirit.nadiiaspaceassistant.ui.utils.humanReadableDifficult
import java.util.Locale

@Composable
fun <T> MissionDetailsCard(mission: T) {
    when (mission) {
        is MedsTests -> MedsTestsCard(mission)
        is EnergyLines -> EnergyLinesCard(mission)
        else -> Log.e("UI", "Unsupported mission type in mission details card")
    }
}

@Composable
fun MedsTestsCard(mission: MedsTests) {
    Card {
        Box(modifier = Modifier.padding(16.dp)) {
            TitlesValuesList(
                mapOf(
                    "Наниматель" to mission.client,
                    "Награда" to "Кредиты: ${mission.reward}",
                    "Сложность" to humanReadableDifficult(mission.difficult),
                    "Срок" to mission.expiration.toString(),
                    "Требования" to mission.requirements,
                    "Локация" to mission.place,
                    "Испытание" to mission.trial,
                    "Опасность" to mission.danger.toString(),
                    "Доп. награда" to mission.additionalReward.toString(),
                )
            )
        }
    }
}

@Composable
fun EnergyLinesCard(mission: EnergyLines) {
    Card {
        Box(modifier = Modifier.padding(16.dp)) {
            TitlesValuesList(
                mapOf(
                    "Наниматель" to mission.client,
                    "Награда" to "Кредиты: ${mission.reward}",
                    "Сложность" to humanReadableDifficult(mission.difficult),
                    "Срок" to mission.expiration.toString(),
                    "Требования" to mission.requirements,
                    "Локация" to mission.place,
                    "Длина маневра" to mission.landingLengthMult.toString(2),
                    "Скорость маневра" to mission.landingTimeMult.toString(2),
                    "Сложные места" to if (mission.hardPlaces) "да" else "нет",
                    "Освещение" to if (mission.light) "да" else "нет",
                )
            )
        }
    }
}