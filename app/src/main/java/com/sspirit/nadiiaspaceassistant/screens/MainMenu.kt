package com.sspirit.nadiiaspaceassistant.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.navigation.Routes
import com.sspirit.nadiiaspaceassistant.ui.PlainMenuItem
import com.sspirit.nadiiaspaceassistant.ui.PlainNavigationMenu

private val items: Array<PlainMenuItem>
    get() = arrayOf(
        PlainMenuItem("Космонавигация", Routes.Cosmonavigation),
        PlainMenuItem("Гипернавигация", Routes.Hypernavigation)
    )

@Composable
fun MainMenu(navController: NavHostController) {
    PlainNavigationMenu(items, navController)
}