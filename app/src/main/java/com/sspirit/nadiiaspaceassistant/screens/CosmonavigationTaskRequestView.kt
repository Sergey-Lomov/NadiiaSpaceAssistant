package com.sspirit.nadiiaspaceassistant.screens

import android.widget.Space
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.generators.CosmonavigationTaskGenerationRequest
import com.sspirit.nadiiaspaceassistant.generators.CosmonavigationTaskGenerationType
import com.sspirit.nadiiaspaceassistant.generators.generateCosmonavigationTask
import com.sspirit.nadiiaspaceassistant.navigation.Routes
import com.sspirit.nadiiaspaceassistant.ui.EditableTitleValueCard
import com.sspirit.nadiiaspaceassistant.ui.OptionPicker
import com.sspirit.nadiiaspaceassistant.ui.OptionsPickerItem
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper
import com.sspirit.nadiiaspaceassistant.ui.TitleValueCard
import com.sspirit.nadiiaspaceassistant.ui.utils.humanReadable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Composable
fun CosmonavigationTaskRequestView(navController: NavHostController) {
    val showTypePicker = remember { mutableStateOf(false) }
    // TODO: Add support for player current piloting skill
    val request = remember {
        mutableStateOf(CosmonavigationTaskGenerationRequest.commonTravel(1.0f))
    }

    ScreenWrapper(navController) {
        Box() {
            MainContent(navController, request, showTypePicker)

            if (showTypePicker.value) {
                val options = CosmonavigationTaskGenerationType
                    .entries
                    .map {
                        OptionsPickerItem(it, humanReadable(it))
                    }
                    .toTypedArray()
                OptionPicker(options, showTypePicker) {
                    request.value.type = it
                }
            }
        }
    }
}

@Composable
private fun MainContent(
    navController: NavHostController,
    request: MutableState<CosmonavigationTaskGenerationRequest>,
    showTypePicker: MutableState<Boolean>
) {
    Column {
        TitleValueCard(
            title = "Тип",
            value = humanReadable(request.value.type),
            modifier = Modifier.clickable { showTypePicker.value = true }
        )

        EditableTitleValueCard(
            title = "Коэф. длины",
            initialValue = request.value.sequenceLengthMultiplier.toString(),
            keyboardType = KeyboardType.Decimal,
            fieldWidth = 75,
            onChange = { text ->
                val value = text.toFloatOrNull()
                value?.let { request.value.sequenceLengthMultiplier = it}
            }
        )

        EditableTitleValueCard(
            title = "Коэф. времени",
            initialValue = request.value.stepDurationMultiplier.toString(),
            keyboardType = KeyboardType.Decimal,
            fieldWidth = 75,
            onChange = { text ->
                val value = text.toFloatOrNull()
                value?.let { request.value.stepDurationMultiplier = it}
            }
        )

        TitleValueCard(
            title = "Сложность",
            value = request.value.difficult.toString(),
        )

        TitleValueCard(
            title = "Адаптив. сложность",
            value = request.value.adaptiveDifficult.toString(),
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            shape = CircleShape,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            onClick = {
                val task = generateCosmonavigationTask(request.value)
                val json = Json.encodeToString(task)
                navController.navigate(Routes.CosmonavigationTask.route + "/${json}")
            }
        ) {
            Text(
                text = "Сгенерировать",
                fontSize = 24.sp,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}