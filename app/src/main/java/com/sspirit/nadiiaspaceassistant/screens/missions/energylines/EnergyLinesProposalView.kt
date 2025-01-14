package com.sspirit.nadiiaspaceassistant.screens.missions.energylines

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper

@Composable
fun EnergyLinesProposalView(navigator: NavHostController) {
    ScreenWrapper(navigator, "Предложение миссии") {
        Text(
            text = "Generation impossible. Energy lines mission should be created manually.",
            fontSize = 18.sp,
            modifier = Modifier.fillMaxWidth()
        )
    }
}