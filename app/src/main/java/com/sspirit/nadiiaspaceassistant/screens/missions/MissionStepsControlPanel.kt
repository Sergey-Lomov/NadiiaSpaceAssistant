package com.sspirit.nadiiaspaceassistant.screens.missions

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.extensions.navigateTo
import com.sspirit.nadiiaspaceassistant.services.dataproviders.missions.MissionsListDataProvider
import com.sspirit.nadiiaspaceassistant.ui.StyledButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun MissionStepControlPanel(
    step: MutableIntState,
    maxStep: Int,
    id: String,
    navController: NavHostController
) {
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
        if (step.intValue < maxStep)
            StyledButton(
                title = "->",
                modifier = Modifier.weight(1f)
            ) {
                step.intValue++
            }
        else
            StyledButton(
                title = "Завершить",
                modifier = Modifier.fillMaxWidth()
            ) {
                CoroutineScope(Dispatchers.IO).launch {
                    MissionsListDataProvider.complete(id)
                }
                navController.popBackStack(
                    navController.graph.startDestinationId,
                    inclusive = false
                )
                navController.navigate(navController.graph.startDestinationId)
            }
    }
}