package com.sspirit.nadiiaspaceassistant.screens.cosmology

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.models.cosmology.SpaceSystem
import com.sspirit.nadiiaspaceassistant.services.dataproviders.CosmologyDataProvider
import com.sspirit.nadiiaspaceassistant.ui.CoroutineLaunchedEffect
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper
import com.sspirit.nadiiaspaceassistant.ui.utils.routesFlowStep
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private const val cardInRow = 2

@Composable
fun SpaceSystemSelectionView(nextRoutes: Array<String>, navigator: NavHostController) {
    val isLoading = remember { mutableStateOf(true) }

    CoroutineLaunchedEffect(loadingState = isLoading) {
        CosmologyDataProvider.downloadSpaceMap()
    }

    ScreenWrapper(navigator, "Выбор системы", isLoading) {
        Column(Modifier.verticalScroll(rememberScrollState())) {
            Spacer(Modifier.height(16.dp))

            val systems = CosmologyDataProvider.sectorMap.systems
            val rowsCount = (systems.size + cardInRow - 1) / cardInRow
            for (row: Int in 0 until rowsCount) {
                Row(
                    modifier = Modifier
                        .height(90.dp)
                ) {
                    Spacer(Modifier.width(16.dp))
                    for (index: Int in 0 until cardInRow) {
                        val system = systems.elementAtOrNull(row * cardInRow + index)
                        if (system != null) {
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clickable {
                                        val indices = CosmologyDataProvider.sectorMap.indicesOf(system)
                                        val json = Json.encodeToString(indices)
                                        routesFlowStep(json, nextRoutes, navigator)
                                    }
                            ) {
                                SystemColumn(system)
                            }
                            Spacer(Modifier.width(16.dp))
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun SystemColumn(system: SpaceSystem) {
    Column {
        Spacer(Modifier.weight(1f))
        Text(
            text = system.id,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(CenterVertically)
                .wrapContentWidth(Alignment.CenterHorizontally)
        )
        Text(
            text = system.title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(CenterVertically)
                .wrapContentWidth(Alignment.CenterHorizontally)
        )
        Spacer(Modifier.weight(1f))
    }
}