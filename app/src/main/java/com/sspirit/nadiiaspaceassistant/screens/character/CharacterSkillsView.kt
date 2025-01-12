package com.sspirit.nadiiaspaceassistant.screens.character

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.R
import com.sspirit.nadiiaspaceassistant.utils.navigateTo
import com.sspirit.nadiiaspaceassistant.models.character.CharacterSkill
import com.sspirit.nadiiaspaceassistant.navigation.Routes
import com.sspirit.nadiiaspaceassistant.services.dataproviders.CharacterDataProvider
import com.sspirit.nadiiaspaceassistant.ui.ColoredCircle
import com.sspirit.nadiiaspaceassistant.ui.CoroutineButton
import com.sspirit.nadiiaspaceassistant.ui.CoroutineLaunchedEffect
import com.sspirit.nadiiaspaceassistant.ui.IterableListWithSpacer
import com.sspirit.nadiiaspaceassistant.ui.LoadingIndicator
import com.sspirit.nadiiaspaceassistant.ui.LocalSWUpdater
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper
import com.sspirit.nadiiaspaceassistant.ui.ScrollableColumn
import com.sspirit.nadiiaspaceassistant.utils.update
import com.sspirit.nadiiaspaceassistant.utils.updaterState


@Composable
fun CharacterSkillsView(navigator: NavHostController) {
    val isLoading = remember { mutableStateOf(true) }
    val updater = updaterState()

    CoroutineLaunchedEffect(loadingState = isLoading) {
        CharacterDataProvider.getCharacter()
    }

    ScreenWrapper(navigator, "Навыки", isLoading, updater) {
        ScrollableColumn {
            val skills = CharacterDataProvider.character.skills
            IterableListWithSpacer(skills, 16) {
                SkillCard(it,navigator)
            }
        }
    }
}

@Composable
private fun SkillCard(skill: CharacterSkill, navigator: NavHostController) {
    Card(
        Modifier.clickable {
            navigator.navigateTo(Routes.CharacterSkillDetails, skill.type.toId())
        }
    ) {
        val haveRoutine = CharacterDataProvider.character.routines[skill.type] != null
        val haveRestriction = CharacterDataProvider.character.restrictor(skill.type) != null

        Box {
            if (haveRoutine) {
                RoutineIndicator(skill, navigator)
            }

            if (haveRestriction) {
                RestrictionIndicator()
            }

            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Text(
                    text = skill.title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                        .wrapContentHeight(align = CenterVertically),
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(8.dp))
                ControlPanel(skill)
            }
        }
    }
}

@Composable
private fun ControlPanel(skill: CharacterSkill) {
    val updater = LocalSWUpdater.current ?: return

    Box {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            CoroutineButton(
                title = "+",
                routine = {
                    CharacterDataProvider.updateSkillProgress(skill,skill.progress + 1)
                },
                completion = {
                    updater.update()
                }
            )

            Spacer(Modifier.width(8.dp))

            Text(
                text = skill.progress.toString(),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight(align = CenterVertically),
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.width(8.dp))

            CoroutineButton(
                title = "-",
                routine = {
                    CharacterDataProvider.updateSkillProgress(skill,skill.progress - 1)
                },
                completion = {
                    updater.update()
                }
            )
        }
    }
}

@Composable
fun RoutineIndicator(skill: CharacterSkill, navigator: NavHostController) {
    Text(
        text = "Ⓡ",
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Gray,
        modifier = Modifier
            .offset(6.dp, 6.dp)
            .clickable {
                navigator.navigateTo(Routes.CharacterSkillDetails, skill.type.toId())
        }
    )
}

@Composable
fun RestrictionIndicator() {
    Row(
        horizontalArrangement = Arrangement.End,
        modifier = Modifier
            .padding(6.dp)
            .fillMaxWidth()
    ) {
        ColoredCircle(colorResource(R.color.soft_yellow), 20)
    }
}