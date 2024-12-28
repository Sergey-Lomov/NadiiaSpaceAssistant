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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.utils.navigateTo
import com.sspirit.nadiiaspaceassistant.models.character.CharacterSkill
import com.sspirit.nadiiaspaceassistant.navigation.Routes
import com.sspirit.nadiiaspaceassistant.services.dataproviders.CharacterDataProvider
import com.sspirit.nadiiaspaceassistant.ui.CoroutineButton
import com.sspirit.nadiiaspaceassistant.ui.CoroutineLaunchedEffect
import com.sspirit.nadiiaspaceassistant.ui.LoadingIndicator
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper
import com.sspirit.nadiiaspaceassistant.ui.ScrollableColumn


@Composable
fun CharacterSkillsView(navController: NavHostController) {
    val loading = remember { mutableStateOf(true) }

    CoroutineLaunchedEffect(loadingState = loading) {
        CharacterDataProvider.getCharacter()
    }

    ScreenWrapper(navController) {
        if (loading.value) {
            LoadingIndicator()
        } else {
            MainContent(navController)
        }
    }
}

@Composable
private fun MainContent(navController: NavHostController) {
    ScrollableColumn {
        val skills = CharacterDataProvider.character.skills
        for (skill in skills) {
            SkillCard(skill, navController)
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SkillCard(skill: CharacterSkill, navController: NavHostController) {
    Card(
        modifier = Modifier
            .clickable {
                navController.navigateTo(Routes.CharacterRoutine, skill.type.toId())
            }
    ) {
        val haveRoutine = CharacterDataProvider.character.routines[skill.type] != null

        Box {
            if (haveRoutine) {
                RoutineIndicator()
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
    var progress by remember { mutableIntStateOf(skill.progress) }

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
                    progress = skill.progress
                }
            )

            Spacer(Modifier.width(8.dp))

            Text(
                text = progress.toString(),
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
                    progress = skill.progress
                }
            )
        }
    }
}

@Composable
fun RoutineIndicator() {
    Text(
        text = "Ⓡ",
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Gray,
        modifier = Modifier
            .offset(6.dp, 6.dp)
    )
}