package com.sspirit.nadiiaspaceassistant.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.navigation.Routes
import com.sspirit.nadiiaspaceassistant.ui.PlainMenuItem
import com.sspirit.nadiiaspaceassistant.ui.PlainNavigationMenu
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val items: Array<PlainMenuItem>
    get() = arrayOf(
        PlainMenuItem("Космонавигация", Routes.Cosmonavigation),
        PlainMenuItem("Космология", Routes.SpaceSystemSelection)  {
            val flow = arrayOf(Routes.SpaceSystemDetails.route)
            Json.encodeToString(flow)
        },
        PlainMenuItem("Персонаж", Routes.CharacterSkills)
    )

@Composable
fun MainMenu(navController: NavHostController) {
    PlainNavigationMenu(items, navController)
}