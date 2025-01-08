package com.sspirit.nadiiaspaceassistant.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.navigation.Routes
import com.sspirit.nadiiaspaceassistant.services.dataproviders.CacheableDataLoader
import com.sspirit.nadiiaspaceassistant.ui.CoroutineLaunchedEffect
import com.sspirit.nadiiaspaceassistant.ui.LoadingIndicator
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
        PlainMenuItem("Персонаж", Routes.CharacterSkills),
        PlainMenuItem("Миссии", Routes.MissionsList),
        PlainMenuItem("Предметы", Routes.ItemsMenu)
    )

@Composable
fun MainMenu(navigator: NavHostController) {

    val loadingState = remember { mutableStateOf(false) }

    CoroutineLaunchedEffect(loadingState = loadingState) {
        CacheableDataLoader.reloadMain()
    }

    if (loadingState.value)
        LoadingIndicator()
    else
        PlainNavigationMenu(items, navigator)
}