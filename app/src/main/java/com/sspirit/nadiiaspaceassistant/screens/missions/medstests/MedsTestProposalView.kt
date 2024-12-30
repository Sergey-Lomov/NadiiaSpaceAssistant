package com.sspirit.nadiiaspaceassistant.screens.missions.medstests

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.screens.missions.MissionProposalView
import com.sspirit.nadiiaspaceassistant.services.dataproviders.missions.MedsTestsDataProvider

@Composable
fun MedsTestProposalView(navigator: NavHostController) {
    MissionProposalView(MedsTestsDataProvider, navigator)
}