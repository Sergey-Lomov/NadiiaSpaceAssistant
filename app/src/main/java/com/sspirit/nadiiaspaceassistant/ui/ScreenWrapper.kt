package com.sspirit.nadiiaspaceassistant.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.utils.Updater

val LocalSWLoadingState = compositionLocalOf<MutableState<Boolean>?> { null }
val LocalSWUpdater = compositionLocalOf<Updater?> { null }

@Composable
fun ScreenWrapper(
    navigator: NavHostController,
    title: String? = null,
    loadingState: MutableState<Boolean>? = null,
    updater: Updater? = null,
    content: @Composable () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopBar(navigator, title)
        },
    ) {  innerPadding ->
        Scaffold(
            modifier = Modifier.padding(innerPadding)
        ) { innerPadding ->
            if (updater != null) {
                key(updater.intValue) {
                    CompositionLocalProvider(LocalSWUpdater provides updater) {
                        LoadingWrapper(loadingState, content)
                    }
                }
            }
            else {
                LoadingWrapper(loadingState, content)
            }
        }
    }
}

@Composable
private fun LoadingWrapper(
    loadingState: MutableState<Boolean>?,
    content: @Composable () -> Unit
) {
    if (loadingState != null)
        LoadingOverlay(loadingState) {
            CompositionLocalProvider(LocalSWLoadingState provides loadingState) {
                content()
            }
        }
    else
        content()
}