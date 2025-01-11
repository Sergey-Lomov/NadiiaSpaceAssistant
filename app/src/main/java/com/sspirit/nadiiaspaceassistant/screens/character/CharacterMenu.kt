package com.sspirit.nadiiaspaceassistant.screens.character

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.models.character.CharacterSkillType
import com.sspirit.nadiiaspaceassistant.services.fabrics.CosmonavigationTaskGenerationRequest
import com.sspirit.nadiiaspaceassistant.navigation.Routes
import com.sspirit.nadiiaspaceassistant.services.dataproviders.CharacterDataProvider
import com.sspirit.nadiiaspaceassistant.ui.PlainMenuItem
import com.sspirit.nadiiaspaceassistant.ui.PlainNavigationMenu
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val items: Array<PlainMenuItem>
    get() = arrayOf(
        PlainMenuItem("Навыки", Routes.CharacterSkills),
        PlainMenuItem("Черты", Routes.CharacterSkills),
        PlainMenuItem("Препараты", Routes.CharacterDrugs),
    )

@Composable
fun CharacterMenu(navigator: NavHostController) {
    PlainNavigationMenu(items, navigator)
}