package com.sspirit.nadiiaspaceassistant.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController

@Composable
fun ScreenWrapper(navController: NavHostController, title: String? = null, content: @Composable () -> Unit) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopBar(navController, title)
        },
    ) {  innerPadding ->
        Scaffold(
            modifier = Modifier.padding(innerPadding)
        ) { innerPadding ->
            content()
        }
    }
}