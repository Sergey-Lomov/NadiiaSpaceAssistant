package com.sspirit.nadiiaspaceassistant.screens.cosmonavigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.utils.navigateTo
import com.sspirit.nadiiaspaceassistant.utils.toString
import com.sspirit.nadiiaspaceassistant.services.generators.CosmonavigationTaskGenerationRequest
import com.sspirit.nadiiaspaceassistant.services.generators.generateCosmonavigationTask
import com.sspirit.nadiiaspaceassistant.models.CosmonavigationTaskSequence
import com.sspirit.nadiiaspaceassistant.models.CosmonavigationTaskSequenceElement
import com.sspirit.nadiiaspaceassistant.models.CosmonavigationTaskSequenceElementColor
import com.sspirit.nadiiaspaceassistant.models.CosmonavigationTaskSequenceElementForm
import com.sspirit.nadiiaspaceassistant.models.getLength
import com.sspirit.nadiiaspaceassistant.navigation.Routes
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper
import com.sspirit.nadiiaspaceassistant.ui.StyledButton
import com.sspirit.nadiiaspaceassistant.ui.TitleValueCard
import com.sspirit.nadiiaspaceassistant.ui.utils.humanReadable

@Composable
fun CosmonavigationTaskView (request: CosmonavigationTaskGenerationRequest, navController: NavHostController) {

    val task by remember(request) { derivedStateOf { generateCosmonavigationTask(request) } }

    ScreenWrapper(navController) {
        Column (Modifier.verticalScroll(rememberScrollState()),) {
            TitleValueCard("Тип", humanReadable(task.type))
            TitleValueCard(
                title = "Сложность",
                value = task.difficult.toString(1)
            )
            TitleValueCard("Длина", task.sequence.getLength().toString())
            TitleValueCard("Время", task.timeLimit.toString())
            Spacer(Modifier.height(16.dp))
            TaskSequenceView(task.sequence)

            StyledButton(
                title = "Начать",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp,)
            ) {
                navController.navigateTo(Routes.CosmonavigationTaskExecution, task.timeLimit.toString())
            }
        }
    }
}

@Composable
private fun TaskSequenceView(sequence: CosmonavigationTaskSequence) {
    for (stepIndex: Int in 0 until sequence.getLength()) {
        Row(
            modifier = Modifier
                .padding(vertical = 4.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            for (lineIndex: Int in sequence.indices) {
                val step = try {
                    sequence[lineIndex].elementAt(stepIndex)
                } catch (e: Exception) {
                    null
                }

                val elements = step ?: arrayOf()

                Card(
                    Modifier
                        .weight(1f)
                        .height(70.dp)
                ) {
                    Row (
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                       for (element: CosmonavigationTaskSequenceElement in elements) {
                           TaskElementView(element)
                           if (element !== elements.last()) {
                               Spacer(Modifier.width(4.dp))
                           }
                       }
                    }
                }

                if (lineIndex < sequence.size - 1) {
                    Spacer(Modifier.width(16.dp))
                }
            }
        }
    }
}

@Composable
private fun TaskElementView(element: CosmonavigationTaskSequenceElement, modifier: Modifier = Modifier) {
    val title = when (element.form) {
        CosmonavigationTaskSequenceElementForm.FIGURE_CIRCLE -> "Ф0"
        CosmonavigationTaskSequenceElementForm.FIGURE_TRIANGLE -> "Ф3"
        CosmonavigationTaskSequenceElementForm.FIGURE_SQUARE -> "Ф4"
        CosmonavigationTaskSequenceElementForm.FIGURE_PENTAGON -> "Ф5"
        CosmonavigationTaskSequenceElementForm.FIGURE_STAR -> "Зв"
        CosmonavigationTaskSequenceElementForm.GESTURE_FIST -> "Ж0"
        CosmonavigationTaskSequenceElementForm.GESTURE_FINGER1 -> "Ж1"
        CosmonavigationTaskSequenceElementForm.GESTURE_FINGER2 -> "Ж2"
        CosmonavigationTaskSequenceElementForm.GESTURE_FINGER3 -> "Ж3"
        CosmonavigationTaskSequenceElementForm.GESTURE_FINGER4 -> "Ж4"
    }

    val color = when (element.color) {
        CosmonavigationTaskSequenceElementColor.YELLOW -> Color.Yellow
        CosmonavigationTaskSequenceElementColor.MAGENTA -> Color.Magenta
        CosmonavigationTaskSequenceElementColor.ORANGE -> Color(1.0f, 0.5f, 0.0f)
        CosmonavigationTaskSequenceElementColor.GREEN -> Color.Green
        CosmonavigationTaskSequenceElementColor.BLUE -> Color.Blue
    }

    Text(
        text = title,
        fontSize = 24.sp,
        color = color,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        modifier = modifier
    )
}