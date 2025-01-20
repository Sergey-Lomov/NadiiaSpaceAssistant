package com.sspirit.nadiiaspaceassistant.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.navigation.Routes
import com.sspirit.nadiiaspaceassistant.services.dataproviders.CacheableDataLoader
import com.sspirit.nadiiaspaceassistant.services.dataproviders.CharacterDataProvider
import com.sspirit.nadiiaspaceassistant.ui.CoroutineLaunchedEffect
import com.sspirit.nadiiaspaceassistant.ui.LoadingIndicator
import com.sspirit.nadiiaspaceassistant.ui.PlainMenuItem
import com.sspirit.nadiiaspaceassistant.ui.PlainNavigationMenu
import com.sspirit.nadiiaspaceassistant.ui.utils.stringsToList
import com.sspirit.nadiiaspaceassistant.utils.navigateWithModel
import com.sspirit.nadiiaspaceassistant.viewmodels.InfoDialogViewModel
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val items: Array<PlainMenuItem>
    get() = arrayOf(
        PlainMenuItem("Космонавигация", Routes.Cosmonavigation),
        PlainMenuItem("Космология", Routes.SpaceSystemSelection)  {
            val flow = arrayOf(Routes.SpaceSystemDetails.route)
            Json.encodeToString(flow)
        },
        PlainMenuItem("Персонаж", Routes.CharacterMenu),
        PlainMenuItem("Миссии", Routes.MissionsList),
        PlainMenuItem("Предметы", Routes.ItemsMenu)
    )

@Composable
fun MainMenu(navigator: NavHostController) {
    val loadingState = rememberSaveable { mutableStateOf(false) }
    val expiredShowed = rememberSaveable { mutableStateOf(false) }

    CoroutineLaunchedEffect(loadingState = loadingState) {
        CacheableDataLoader.reloadMain()
    }

    LaunchedEffect(loadingState.value) {
        if (!loadingState.value && !expiredShowed.value)
            showExpiredTraitsInfo(expiredShowed, navigator)
    }

    if (loadingState.value)
        LoadingIndicator()
    else
        PlainNavigationMenu(items, "", navigator)
}

private fun showExpiredTraitsInfo(showed: MutableState<Boolean>, navigator: NavHostController) {
    val expiredTraits = CharacterDataProvider.expiredTraits
    if (expiredTraits.isEmpty()) return

    val expiredTitles = expiredTraits.map { it.type.title }
    val model = InfoDialogViewModel(
        title = "Недавно завершились черты",
        info = stringsToList(expiredTitles)
    )
    model.actions["Принять"] = {
        showed.value = true
        navigator.popBackStack()
    }
    navigator.navigateWithModel(Routes.InfoDialog, model)
}