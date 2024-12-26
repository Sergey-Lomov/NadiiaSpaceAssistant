package com.sspirit.nadiiaspaceassistant.screens.missions.proprtyevacuation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.extensions.navigateTo
import com.sspirit.nadiiaspaceassistant.navigation.Routes
import com.sspirit.nadiiaspaceassistant.screens.missions.MissionDetailsView
import com.sspirit.nadiiaspaceassistant.services.dataproviders.missions.propertyevacuation.PropertyEvacuationDataProvider
import com.sspirit.nadiiaspaceassistant.ui.StyledButton

@Composable
fun PropertyEvacuationDetailsView(id: String, navController: NavHostController) {
    Column() {
        MissionDetailsView(id, PropertyEvacuationDataProvider, navController,
            onStart = {
                //navController.navigateTo(Routes.MedsTestsExecution.route + "/$id")
            },
            additions = {
                Spacer(Modifier.height(16.dp))

                StyledButton(
                    title = "Анализ",
                    modifier = Modifier.fillMaxWidth()
                ) {
                    navController.navigateTo(Routes.PropertyEvacuationAnalyzing, id)
                }

                StyledButton(
                    title = "Изучить объект",
                    modifier = Modifier.fillMaxWidth()
                ) {
                    navController.navigateTo(Routes.BuildingDetails, id)
                }
            }
        )
    }
}