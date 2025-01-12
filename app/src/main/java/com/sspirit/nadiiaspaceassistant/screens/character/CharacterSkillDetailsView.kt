package com.sspirit.nadiiaspaceassistant.screens.character

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.models.character.CharacterSkill
import com.sspirit.nadiiaspaceassistant.models.character.CharacterSkillType
import com.sspirit.nadiiaspaceassistant.navigation.Routes
import com.sspirit.nadiiaspaceassistant.services.dataproviders.CharacterDataProvider
import com.sspirit.nadiiaspaceassistant.ui.AutosizeStyledButton
import com.sspirit.nadiiaspaceassistant.ui.HeaderText
import com.sspirit.nadiiaspaceassistant.ui.LocalSWLoadingState
import com.sspirit.nadiiaspaceassistant.ui.LocalSWUpdater
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper
import com.sspirit.nadiiaspaceassistant.ui.StyledButton
import com.sspirit.nadiiaspaceassistant.utils.Updater
import com.sspirit.nadiiaspaceassistant.utils.navigateTo
import com.sspirit.nadiiaspaceassistant.utils.simpleCoroutineLaunch
import com.sspirit.nadiiaspaceassistant.utils.update
import com.sspirit.nadiiaspaceassistant.utils.updaterState

@Composable
fun CharacterSkillDetailsView(skillId: String, navigator: NavHostController) {
    val type = CharacterSkillType.byId(skillId)
    val skill = CharacterDataProvider.character.skill(type)
    val isLoading = rememberSaveable { mutableStateOf(false) }
    val updater = updaterState()

    ScreenWrapper(navigator, "Детали навыка", isLoading, updater) {
        Column(Modifier.padding(horizontal = 16.dp)) {
            InfoCard(skill)
            Spacer(Modifier.weight(1f))
            ValueEditPanel(skill)
            Spacer(Modifier.height(8.dp))
            RoutineButton(skill, navigator)
        }
    }
}

@Composable
private fun InfoCard(skill: CharacterSkill) {
    Card {
        Column(Modifier.padding(16.dp)) {
            HeaderText(skill.title)
            Spacer(Modifier.height(8.dp))
            CharacterSkillProgressReport(skill.type)
        }
    }
}

@Composable
private fun ValueEditPanel(skill: CharacterSkill) {
    val loadingState = LocalSWLoadingState.current ?: return
    val updater = LocalSWUpdater.current ?: return

    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        StyledButton (
            title = "+",
            modifier = Modifier.weight(1f)
        ) {
            simpleCoroutineLaunch(loadingState) {
                CharacterDataProvider.updateSkillProgress(skill, skill.progress + 1)
                updater.update()
            }
        }

        Spacer(Modifier.width(8.dp))

        StyledButton (
            title = "-",
            modifier = Modifier.weight(1f)
        ) {
            simpleCoroutineLaunch(loadingState) {
                CharacterDataProvider.updateSkillProgress(skill, skill.progress - 1)
                updater.update()
            }
        }
    }
}

@Composable
private fun RoutineButton(skill: CharacterSkill, navigator: NavHostController) {
    val routine = CharacterDataProvider.character.routines[skill.type]
    AutosizeStyledButton(
        title = "Рутина",
        enabled = routine != null
    ) {
        navigator.navigateTo(Routes.CharacterRoutine, skill.type.toId())
    }
}