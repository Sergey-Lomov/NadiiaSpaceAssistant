package com.sspirit.nadiiaspaceassistant.screens.character

import android.content.res.Resources.Theme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.R
import com.sspirit.nadiiaspaceassistant.models.character.CharacterRoutine
import com.sspirit.nadiiaspaceassistant.models.character.CharacterRoutineItem
import com.sspirit.nadiiaspaceassistant.models.character.CharacterRoutineItemStatus
import com.sspirit.nadiiaspaceassistant.models.character.CharacterSkillType
import com.sspirit.nadiiaspaceassistant.services.dataproviders.CharacterDataProvider
import com.sspirit.nadiiaspaceassistant.ui.ColoredCircle
import com.sspirit.nadiiaspaceassistant.ui.CoroutineButton
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper
import com.sspirit.nadiiaspaceassistant.ui.ScrollableColumn
import java.time.LocalDate

@Composable
fun CharacterRoutineView(skill: CharacterSkillType, navigator: NavHostController) {
    val routine = CharacterDataProvider.character.routines[skill]

    ScreenWrapper(navigator, "Рутина") {
        if (routine == null) {
            NoRoutineMessage()
        } else {
            MainContent(skill, routine)
        }
    }
}

@Composable
private fun MainContent(skillType: CharacterSkillType, routine: CharacterRoutine) {
    ScrollableColumn {
        for(item in routine) {
            Card {
                Column(
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Text(
                        text = item.title,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .wrapContentHeight()
                            .fillMaxWidth()
                            .wrapContentHeight(align = CenterVertically),
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(8.dp))
                    ItemControlPanel(skillType, item)
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ItemControlPanel(skillType: CharacterSkillType, item: CharacterRoutineItem) {
    val changeableStatus = remember { mutableStateOf(item.todayStatus()) }

    Box {
        Row(
            verticalAlignment = CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(Modifier.width(8.dp))
            ItemStatus(item.yesterdayStatus())
            Spacer(Modifier.width(8.dp))
            ItemStatus(changeableStatus.value)
            Spacer(Modifier.weight(1f))
            StatusSetButton(skillType, item, CharacterRoutineItemStatus.DONE, changeableStatus)
            Spacer(Modifier.width(8.dp))
            StatusSetButton(skillType, item, CharacterRoutineItemStatus.UNDONE, changeableStatus)
            Spacer(Modifier.width(8.dp))
            StatusSetButton(skillType, item, CharacterRoutineItemStatus.INACTIVE, changeableStatus)

        }
    }
}

@Composable
private fun StatusSetButton(
    skillType: CharacterSkillType,
    item: CharacterRoutineItem,
    status: CharacterRoutineItemStatus,
    statusState: MutableState<CharacterRoutineItemStatus>
) {
    CoroutineButton(
        title = status.toString(),
        routine = {
            CharacterDataProvider.updateRoutineItemStatus(
                skillType = skillType,
                item = item,
                date = LocalDate.now(),
                status = status
            )
        },
        completion = {
            statusState.value = item.todayStatus()
        }
    )
}

@Composable
private fun ItemStatus(status: CharacterRoutineItemStatus) {
    val color = when (status) {
        CharacterRoutineItemStatus.DONE -> colorResource(id = R.color.soft_green)
        CharacterRoutineItemStatus.UNDONE -> colorResource(id = R.color.soft_red)
        CharacterRoutineItemStatus.INACTIVE -> colorResource(id = R.color.soft_yellow)
        CharacterRoutineItemStatus.UNDEFINED -> Color.LightGray
    }

    ColoredCircle(color, 44) {
        Text(
            text = status.toString(),
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxSize()
                .wrapContentHeight(align = CenterVertically)
        )
    }
}

@Composable
private fun NoRoutineMessage() {
    Text(
        text = "Routine for skill missed",
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        color = Color.DarkGray,
        modifier = Modifier
            .fillMaxSize()
            .wrapContentHeight(Alignment.CenterVertically)
    )
}