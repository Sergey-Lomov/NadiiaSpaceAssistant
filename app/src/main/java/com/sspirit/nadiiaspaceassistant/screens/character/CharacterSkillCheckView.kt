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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.R
import com.sspirit.nadiiaspaceassistant.utils.toSignedString
import com.sspirit.nadiiaspaceassistant.utils.toString
import com.sspirit.nadiiaspaceassistant.models.character.CharacterSkillCheck
import com.sspirit.nadiiaspaceassistant.services.ViewModelsRegister
import com.sspirit.nadiiaspaceassistant.services.dataproviders.CharacterDataProvider
import com.sspirit.nadiiaspaceassistant.ui.CoroutineButton
import com.sspirit.nadiiaspaceassistant.ui.HeaderText
import com.sspirit.nadiiaspaceassistant.ui.RegularText
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper
import com.sspirit.nadiiaspaceassistant.ui.ScrollableColumn
import com.sspirit.nadiiaspaceassistant.ui.SpacedHorizontalDivider
import com.sspirit.nadiiaspaceassistant.ui.TitlesValuesList
import com.sspirit.nadiiaspaceassistant.ui.utils.humanReadable
import com.sspirit.nadiiaspaceassistant.viewmodels.CharacterSkillCheckViewModel

@Composable
fun CharacterSkillCheckView(modelId: String, navigator: NavHostController) {
    val model = ViewModelsRegister.get<CharacterSkillCheckViewModel>(modelId) ?: return
    val check = model.check
    val skill = CharacterDataProvider.character.skill(check.skill)

    ScreenWrapper(navigator, "Проверка навыка") {
        ScrollableColumn {
            HeaderText(skill.title)
            Spacer(Modifier.height(8.dp))
            InfoCard(check)
            Spacer(Modifier.height(8.dp))
            ChancesInfo(check)
            SpacedHorizontalDivider()
            ResultsPanel(model)
        }
    }
}

@Composable
private fun InfoCard(check: CharacterSkillCheck) {
    val traits = CharacterDataProvider.character.traitsBySkill(check.skill)
    val drugs = CharacterDataProvider.character.drugsBySkill(check.skill)

    Card {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            TitlesValuesList(
                "Неожиданость" to humanReadable(check.isUnexpected),
                "Точность" to check.accuracy,
                "Требование" to check.requirement,
                "Значение навыка" to CharacterDataProvider.character.progress(check.skill)
            )

            if (traits.isNotEmpty() || drugs.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                RegularText("Эффекты:")
                Spacer(Modifier.height(8.dp))
                Column(modifier = Modifier.padding(start = 16.dp)) {
                    val traitsMap = traits.associate {
                        val effect = it.effectOn(check.skill).toSignedString()
                        it.type.title to effect
                    }
                    val drugsMap = drugs.associate {
                        val effect = it.effectOn(check.skill).toSignedString()
                        it.title to effect
                    }
                    TitlesValuesList(traitsMap.plus(drugsMap))
                }
            }
        }
    }
}

@Composable
private fun ChancesInfo(check: CharacterSkillCheck) {
    val progress = CharacterDataProvider.character.progress(check.skill)
    val success = progress + check.accuracy - check.requirement
    val chance = success.toFloat() / check.accuracy.toFloat()

    when {
        chance <= 0 -> RegularText(
            text = "Гарантированный провал",
            color =colorResource(R.color.soft_red),
            align = TextAlign.Center
        )

        chance > 0 && chance < 1f -> RegularText(
            text = "Шанс ${chance.toString(2)}%",
            align = TextAlign.Center
        )

        chance >= 1 -> RegularText(
            text = "Гарантированный успех",
            color = colorResource(R.color.soft_green),
            align = TextAlign.Center
        )
    }
}

@Composable
private fun ResultsPanel(model: CharacterSkillCheckViewModel) {

    Row(modifier = Modifier.fillMaxWidth()) {
        CoroutineButton(
            title = "Провал",
            modifier = Modifier.weight(1f),
            routine = { model.onFail.invoke() },
        )
        Spacer(Modifier.width(16.dp))
        CoroutineButton(
            title = "Успех",
            modifier = Modifier.weight(1f),
            routine = { model.onSuccess.invoke() },
        )
    }
}