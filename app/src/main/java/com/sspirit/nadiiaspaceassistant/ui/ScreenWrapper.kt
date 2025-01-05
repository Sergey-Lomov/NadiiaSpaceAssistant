package com.sspirit.nadiiaspaceassistant.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController

val LocalSWLoadingState = compositionLocalOf<MutableState<Boolean>?> { null }

@Composable
fun ScreenWrapper(
    navigator: NavHostController,
    title: String? = null,
    loadingState: MutableState<Boolean>? = null,
    content: @Composable () -> Unit) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopBar(navigator, title)
        },
    ) {  innerPadding ->
        Scaffold(
            modifier = Modifier.padding(innerPadding)
        ) { innerPadding ->
            if (loadingState != null)
                LoadingOverlay(loadingState) {
                    CompositionLocalProvider(LocalSWLoadingState provides loadingState) {
                        content()
                    }
                }
            else
                content()
        }
    }
}