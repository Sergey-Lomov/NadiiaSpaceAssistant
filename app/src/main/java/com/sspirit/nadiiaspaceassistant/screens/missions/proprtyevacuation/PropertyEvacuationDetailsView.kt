package com.sspirit.nadiiaspaceassistant.screens.missions.proprtyevacuation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.utils.navigateTo
import com.sspirit.nadiiaspaceassistant.navigation.Routes
import com.sspirit.nadiiaspaceassistant.screens.missions.MissionDetailsView
import com.sspirit.nadiiaspaceassistant.services.dataproviders.missions.propertyevacuation.PropertyEvacuationDataProvider
import com.sspirit.nadiiaspaceassistant.ui.AutosizeStyledButton

@Composable
fun PropertyEvacuationDetailsView(id: String, navigator: NavHostController) {
    Column() {
        MissionDetailsView(id, PropertyEvacuationDataProvider, navigator,
            onStart = {
                //navigator.navigateTo(Routes.MedsTestsExecution.route + "/$id")
            },
            additions = {
                Spacer(Modifier.height(16.dp))

                AutosizeStyledButton("Анализ") {
                    navigator.navigateTo(Routes.PropertyEvacuationAnalyzing, id)
                }

                AutosizeStyledButton("Изучить объект") {
                    navigator.navigateTo(Routes.BuildingDetails, id)
                }
            }
        )
    }
}