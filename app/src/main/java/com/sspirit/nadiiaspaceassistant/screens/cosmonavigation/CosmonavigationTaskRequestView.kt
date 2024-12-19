package com.sspirit.nadiiaspaceassistant.screens.cosmonavigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.models.character.CharacterSkillType
import com.sspirit.nadiiaspaceassistant.services.generators.CosmonavigationTaskGenerationRequest
import com.sspirit.nadiiaspaceassistant.services.generators.CosmonavigationTaskGenerationType
import com.sspirit.nadiiaspaceassistant.navigation.Routes
import com.sspirit.nadiiaspaceassistant.services.dataproviders.CharacterDataProvider
import com.sspirit.nadiiaspaceassistant.ui.EditableTitleValueCard
import com.sspirit.nadiiaspaceassistant.ui.OptionPicker
import com.sspirit.nadiiaspaceassistant.ui.OptionsPickerItem
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper
import com.sspirit.nadiiaspaceassistant.ui.StyledButton
import com.sspirit.nadiiaspaceassistant.ui.TitleValueCard
import com.sspirit.nadiiaspaceassistant.ui.utils.humanReadable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.Locale

@Composable
fun CosmonavigationTaskRequestView(navController: NavHostController) {
    val showTypePicker = remember { mutableStateOf(false) }
    val request = remember {
        val adaptive = CharacterDataProvider.character.level(CharacterSkillType.PILOTING)
        mutableStateOf(CosmonavigationTaskGenerationRequest.commonTravel(adaptive))
    }

    ScreenWrapper(navController) {
        Box {
            MainContent(navController, request, showTypePicker)

            if (showTypePicker.value) {
                val options = CosmonavigationTaskGenerationType.entries
                    .map { OptionsPickerItem(it, humanReadable(it)) }
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
                value?.let {
                    request.value = request.value.copy(sequenceLengthMultiplier = it)
                }
            }
        )

        EditableTitleValueCard(
            title = "Коэф. времени",
            initialValue = request.value.stepDurationMultiplier.toString(),
            keyboardType = KeyboardType.Decimal,
            fieldWidth = 75,
            onChange = { text ->
                val value = text.toFloatOrNull()
                value?.let {
                    request.value = request.value.copy(stepDurationMultiplier = it)
                }
            }
        )

        EditableTitleValueCard(
            title = "Адап. сложность",
            initialValue = request.value.adaptiveDifficult.toString(),
            keyboardType = KeyboardType.Decimal,
            fieldWidth = 75,
            onChange = { text ->
                val value = text.toFloatOrNull()
                value?.let {
                    request.value = request.value.copy(adaptiveDifficult = it)
                }
            }
        )

        TitleValueCard(
            title = "Сложность",
            value = String.format(Locale.US, "%.1f", request.value.difficult),
        )

        Spacer(modifier = Modifier.weight(1f))

        StyledButton(
            title = "Сгенерировать",
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
                val json = Json.encodeToString(request.value)
                navController.navigate(Routes.CosmonavigationTaskByRequest.route + "/${json}")
        }
    }
}