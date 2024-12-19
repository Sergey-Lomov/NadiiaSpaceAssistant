package com.sspirit.nadiiaspaceassistant.screens.missions

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sspirit.nadiiaspaceassistant.models.missions.MedsTests
import com.sspirit.nadiiaspaceassistant.ui.TitlesValuesList
import java.util.Locale

@Composable
fun <T> MissionDetailsCard(mission: T) {
    when (mission) {
        is MedsTests -> MedsTestsCard(mission)
        else -> Log.e("UI", "Unsupported mission type in mission details card")
    }
}

@Composable
fun MedsTestsCard(mission: MedsTests) {
    val difficult = String.format(Locale.US, "%.2f", mission.difficult)

    Card {
        Box(modifier = Modifier.padding(16.dp)) {
            TitlesValuesList(
                mapOf(
                    "Наниматель" to mission.client,
                    "Испытание" to mission.trial,
                    "Награда" to "Кредиты: ${mission.reward}",
                    "Сложность" to difficult,
                    "Опасность" to mission.danger.toString(),
                    "Доп. награда" to mission.additionalReward.toString(),
                    "Срок" to mission.expiration.toString(),
                    "Требования" to mission.requirements
                )
            )
        }
    }
}