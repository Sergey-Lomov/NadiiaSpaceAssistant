package com.sspirit.nadiiaspaceassistant.screens.missions.proprtyevacuation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.navigation.BuildingRoutes
import com.sspirit.nadiiaspaceassistant.utils.navigateTo
import com.sspirit.nadiiaspaceassistant.navigation.Routes
import com.sspirit.nadiiaspaceassistant.screens.building.DataProvider
import com.sspirit.nadiiaspaceassistant.screens.building.TimeManager
import com.sspirit.nadiiaspaceassistant.screens.missions.MissionDetailsView
import com.sspirit.nadiiaspaceassistant.services.dataproviders.missions.propertyevacuation.PropertyEvacuationDataProvider
import com.sspirit.nadiiaspaceassistant.ui.AutosizeStyledButton

@Composable
fun PropertyEvacuationDetailsView(id: String, navigator: NavHostController) {
    Column {
        MissionDetailsView(id, PropertyEvacuationDataProvider, navigator,
            onStart = {
                val mission = DataProvider.getBy(id)
                TimeManager.setupTimeLeft(mission?.time ?: 0)
                navigator.navigateTo(Routes.PropertyEvacuationExecution, id)
            },
            additions = {
                Spacer(Modifier.height(16.dp))

                AutosizeStyledButton("Продолжить") {
                    navigator.navigateTo(Routes.PropertyEvacuationExecution, id)
                }

                AutosizeStyledButton("Анализ") {
                    navigator.navigateTo(Routes.PropertyEvacuationAnalyzing, id)
                }

                AutosizeStyledButton("Изучить объект") {
                    navigator.navigateTo(BuildingRoutes.Details, id)
                }
            }
        )
    }
}