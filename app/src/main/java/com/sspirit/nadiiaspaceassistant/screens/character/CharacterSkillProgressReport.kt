package com.sspirit.nadiiaspaceassistant.screens.character

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sspirit.nadiiaspaceassistant.models.character.CharacterSkillType
import com.sspirit.nadiiaspaceassistant.services.dataproviders.CharacterDataProvider
import com.sspirit.nadiiaspaceassistant.ui.RegularText
import com.sspirit.nadiiaspaceassistant.ui.TitleValueRow
import com.sspirit.nadiiaspaceassistant.ui.TitlesValuesList
import com.sspirit.nadiiaspaceassistant.utils.toSignedString

@Composable
fun CharacterSkillProgressReport(skill: CharacterSkillType) {
    val char = CharacterDataProvider.character
    val traits = char.traitsBySkill(skill)
    val drugs = char.drugsBySkill(skill)
    val restrictor = char.restrictor(skill)

    Column {
        TitleValueRow("Значение навыка", char.progress(skill))

        if (traits.isNotEmpty() || drugs.isNotEmpty() || restrictor != null) {
            Spacer(Modifier.height(8.dp))
            TitleValueRow("Чистый навык", char.pureProgress(skill))
            Spacer(Modifier.height(8.dp))
            RegularText("Эффекты:")
        }

        Column(modifier = Modifier.padding(start = 16.dp)) {
            if (restrictor != null) {
                Spacer(Modifier.height(8.dp))
                val delta = char.progress(restrictor) - char.pureProgress(skill)
                val restricted = char.restrictedProgress(skill)
                val title = char.skill(restrictor).title
                TitleValueRow(
                    title = title,
                    value = "$delta (до $restricted)",
                    balancedWeights = true
                )
            }

            if (traits.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                val traitsMap = traits
                    .map { it.type }
                    .associate { type ->
                        val amount = traits.count { it.type == type }
                        val key = if (amount == 1) type.title else "${type.title} x$amount"
                        val value = type.effectOn(skill) * amount
                        key to value.toSignedString()
                    }
                TitlesValuesList(traitsMap)
            }

            if (drugs.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                val drugsMap = drugs.associate {
                    it.title to it.effectOn(skill).toSignedString()
                }
                TitlesValuesList(drugsMap)
            }
        }
    }
}