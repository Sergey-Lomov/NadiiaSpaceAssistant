package com.sspirit.nadiiaspaceassistant.screens.missions

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.services.dataproviders.missions.MissionsDataProvider
import com.sspirit.nadiiaspaceassistant.ui.CoroutineLaunchedEffect
import com.sspirit.nadiiaspaceassistant.ui.LoadingIndicator
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper
import com.sspirit.nadiiaspaceassistant.ui.StyledButton

@Composable
fun <T> MissionDetailsView(id: String, dataProvide: MissionsDataProvider<T>, navController: NavHostController) {
    val isLoading = remember { mutableStateOf(false) }

    CoroutineLaunchedEffect(loadingState = isLoading) {
        dataProvide.download(id)
    }

    @Composable
    fun MainContent() {
        Column(modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
        ) {
            val mission = dataProvide.getBy(id)
            if (mission != null) {
                MissionDetailsCard(mission)
            }

            Spacer(Modifier.height(16.dp))

            StyledButton(
                title = "Начать",
                modifier = Modifier.fillMaxWidth()
            ) {

            }
        }
    }

    ScreenWrapper(navController) {
        if (isLoading.value)
            LoadingIndicator()
        else
            MainContent()
    }
}