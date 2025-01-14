package com.sspirit.nadiiaspaceassistant.screens.character

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.models.character.CharacterTrait
import com.sspirit.nadiiaspaceassistant.models.character.CharacterTraitType
import com.sspirit.nadiiaspaceassistant.navigation.Routes
import com.sspirit.nadiiaspaceassistant.services.dataproviders.CharacterDataProvider
import com.sspirit.nadiiaspaceassistant.services.fabrics.CharacterTraitsGenerator
import com.sspirit.nadiiaspaceassistant.ui.AutosizeStyledButton
import com.sspirit.nadiiaspaceassistant.ui.HeaderText
import com.sspirit.nadiiaspaceassistant.ui.ElementsList
import com.sspirit.nadiiaspaceassistant.ui.LocalSWLoadingState
import com.sspirit.nadiiaspaceassistant.ui.LocalSWUpdater
import com.sspirit.nadiiaspaceassistant.ui.RegularText
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper
import com.sspirit.nadiiaspaceassistant.ui.ScrollableColumn
import com.sspirit.nadiiaspaceassistant.ui.SpacedHorizontalDivider
import com.sspirit.nadiiaspaceassistant.ui.StyledIconButton
import com.sspirit.nadiiaspaceassistant.ui.TitleValueRow
import com.sspirit.nadiiaspaceassistant.utils.daysToNow
import com.sspirit.nadiiaspaceassistant.utils.navigateWithModel
import com.sspirit.nadiiaspaceassistant.utils.simpleCoroutineLaunch
import com.sspirit.nadiiaspaceassistant.utils.update
import com.sspirit.nadiiaspaceassistant.utils.updaterState
import com.sspirit.nadiiaspaceassistant.viewmodels.InfoDialogViewModel

@Composable
fun CharacterTraitsView(navigator: NavHostController) {
    val isLoading = rememberSaveable { mutableStateOf(false) }
    val updater = updaterState()

    ScreenWrapper(navigator, "Особенности", isLoading, updater) {
        val active = CharacterDataProvider.character.traits
        val activeTypes = active.map { it.type }
        val available = CharacterTraitType.entries
            .minus(CharacterTraitType.UNDEFINED)
            .filter { it !in activeTypes }

        ScrollableColumn {
            if (active.isNotEmpty()) {
                HeaderText("Активные")
                Spacer(Modifier.height(8.dp))
                ElementsList(active) {
                    ActiveTraitCard(it, navigator)
                }
                SpacedHorizontalDivider()
            }

            HeaderText("Доступные")
            Spacer(Modifier.height(8.dp))
            ElementsList(available) {
                AvailableTraitCard(it, navigator)
            }
        }
    }
}

@Composable
fun ActiveTraitCard(trait: CharacterTrait, navigator: NavHostController) {
    val loadingState = LocalSWLoadingState.current ?: return
    val updater = LocalSWUpdater.current ?: return

    Card {
        Column(Modifier.padding(16.dp)) {
            HeaderText(trait.type.title)
            RegularText(trait.type.info)
            Spacer(Modifier.height(8.dp))
            if (trait.expiration != null) {
                val days = trait.expiration.daysToNow()
                TitleValueRow("Действует", "$days дн.")
            } else {
                RegularText("Время действия не ограничено")
            }
            Spacer(Modifier.height(8.dp))
            TitleValueRow("Тэги", trait.type.tags.joinToString(", "))
            Spacer(Modifier.height(8.dp))

            Row {
                StyledIconButton(
                    icon = Icons.Default.Delete,
                    description = "Delete",
                    modifier = Modifier.weight(1f)
                ) {
                    simpleCoroutineLaunch(loadingState) {
                        CharacterDataProvider.removeTrait(trait)
                        updater.update()
                    }
                }
                Spacer(Modifier.width(8.dp))
                StyledIconButton(
                    icon = Icons.Filled.Refresh,
                    description = "Refresh",
                    modifier = Modifier.weight(1f)
                ) {
                    showDurationDialog(navigator) { days ->
                        navigator.popBackStack()
                        val newTrait = CharacterTraitsGenerator.newTrait(trait.type, days)
                        simpleCoroutineLaunch(loadingState) {
                            CharacterDataProvider.addTrait(newTrait)
                            updater.update()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AvailableTraitCard(type: CharacterTraitType, navigator: NavHostController) {
    val loadingState = LocalSWLoadingState.current ?: return
    val updater = LocalSWUpdater.current ?: return

    Card {
        Column(Modifier.padding(16.dp)) {
            HeaderText(type.title)
            RegularText(type.info)
            Spacer(Modifier.height(8.dp))
            TitleValueRow("Тэги", type.tags.joinToString(", "))
            Spacer(Modifier.height(8.dp))
            AutosizeStyledButton("Применить") {
                showDurationDialog(navigator) { days ->
                    navigator.popBackStack()
                    val trait = CharacterTraitsGenerator.newTrait(type, days)
                    simpleCoroutineLaunch(loadingState) {
                        CharacterDataProvider.addTrait(trait)
                        updater.update()
                    }
                }
            }
        }
    }
}

private fun showDurationDialog(
    navigator: NavHostController,
    completion: (days: Int) -> Unit
) {
    val model = InfoDialogViewModel(
        title = "Настройка черты",
        info = "Выберите время действия"
    )

    model.actions["До конца дня"] = { completion(0) }
    model.actions["1 день"] = { completion(1) }
    model.actions["2 дня"] = { completion(2) }
    model.actions["3 дня"] = { completion(3) }
    model.actions["Неограничено"] = { completion(Int.MAX_VALUE) }

    navigator.navigateWithModel(Routes.InfoDialog, model)
}