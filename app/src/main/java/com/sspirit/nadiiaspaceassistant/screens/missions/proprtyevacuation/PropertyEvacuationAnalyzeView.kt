package com.sspirit.nadiiaspaceassistant.screens.missions.proprtyevacuation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.R
import com.sspirit.nadiiaspaceassistant.utils.toPercentage
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingMaterialLucidity
import com.sspirit.nadiiaspaceassistant.services.BuildingMaterialHolder
import com.sspirit.nadiiaspaceassistant.services.MaterialAnalyzingReport
import com.sspirit.nadiiaspaceassistant.services.PropertyEvacuationAnalyzer
import com.sspirit.nadiiaspaceassistant.services.PropertyEvacuationAnalyzingReport
import com.sspirit.nadiiaspaceassistant.services.dataproviders.missions.propertyevacuation.PropertyEvacuationDataProvider
import com.sspirit.nadiiaspaceassistant.ui.HeaderText
import com.sspirit.nadiiaspaceassistant.ui.RegularText
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper
import com.sspirit.nadiiaspaceassistant.ui.ScrollableColumn
import com.sspirit.nadiiaspaceassistant.ui.TitleValueRow

@Composable
fun PropertyEvacuationAnalyzeView(id: String, navController: NavHostController) {
    val mission = PropertyEvacuationDataProvider.getBy(id) ?: throw Exception("Missed PE mission for id $id")
    val report = PropertyEvacuationAnalyzer.analyze(mission)

    ScreenWrapper(navController) {
        ScrollableColumn {
            LootCard(report)
            Spacer(Modifier.height(8.dp))
            MaterialsReportCard("Материал дверей", report.materials[BuildingMaterialHolder.DOOR])
            MaterialsReportCard("Материал стен", report.materials[BuildingMaterialHolder.WALL])
            MaterialsReportCard("Материал перекрытий", report.materials[BuildingMaterialHolder.SLAB])
            UnreachableLocationsView(report)
            IssuesCard("Проблемы ключей", report.missedKeys.toTypedArray())
            IssuesCard("Другие проблемы", report.otherIssues.toTypedArray())
        }
    }
}

@Composable
private fun UnreachableLocationsView(report: PropertyEvacuationAnalyzingReport) {
    if (report.unreachableLocations.isEmpty()) return

    Card(colors = IssuesColors()) {
        Column(modifier = Modifier.padding(16.dp)) {
            HeaderText("Недостижимые локации")
            for (location in report.unreachableLocations) {
                RegularText("• ${location.title} (${location.id})")
                if (location !== report.unreachableLocations.last())
                    Spacer(Modifier.height(4.dp))
            }
        }
    }
    Spacer(Modifier.height(8.dp))
}

@Composable
private fun LootCard(report: PropertyEvacuationAnalyzingReport) {
    Card {
        Column(modifier = Modifier.padding(16.dp)) {
            HeaderText("Лут")
            Spacer(Modifier.height(8.dp))
            TitleValueRow("Общая стоимость", report.loot.totalPrice.toString(), 18)
            TitleValueRow("Малые стержни", report.loot.smallStabilizers.toString(), 18)
            TitleValueRow("Большие стержни", report.loot.bigStabilizers.toString(), 18)
        }
    }
}

@Composable
private fun MaterialsReportCard(title: String, report: MaterialAnalyzingReport?) {
    if (report == null) return
    Card {
        Column(modifier = Modifier.padding(16.dp)) {
            HeaderText(title)
            val lucidity = report.lucidity
                .filter { it.key != BuildingMaterialLucidity.UNDEFINED }
                .values
                .joinToString(" ") { it.toPercentage() }
            TitleValueRow("Очевидность", lucidity, 18)
            TitleValueRow("Теплоупорность", report.heatImmune.toPercentage(), 18)
            TitleValueRow("Кислотоупорност", report.acidImmune.toPercentage(), 18)
            TitleValueRow("Взрывоупорность", report.explosionImmune.toPercentage(), 18)
        }
    }
    Spacer(Modifier.height(8.dp))
}

@Composable
private fun IssuesCard(title: String, issues: Array<String>) {
    if (issues.isEmpty()) return

    Card(colors = IssuesColors()) {
        Column(modifier = Modifier.padding(16.dp)) {
            HeaderText(title)
            Spacer(Modifier.height(8.dp))
            for (issue in issues) {
                RegularText("• $issue")
                if (issue !== issues.last())
                    Spacer(Modifier.height(4.dp))
            }
        }
    }
    Spacer(Modifier.height(8.dp))
}

@Composable
private fun IssuesColors() : CardColors {
    val backColor = colorResource(R.color.soft_red)
    return CardDefaults.cardColors(containerColor = backColor)
}