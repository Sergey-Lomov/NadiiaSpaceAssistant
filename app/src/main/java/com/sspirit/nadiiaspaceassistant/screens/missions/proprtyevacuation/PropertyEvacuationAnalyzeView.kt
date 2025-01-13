package com.sspirit.nadiiaspaceassistant.screens.missions.proprtyevacuation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.R
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingLocation
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingMaterial
import com.sspirit.nadiiaspaceassistant.utils.toPercentage
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingMaterialLucidity
import com.sspirit.nadiiaspaceassistant.screens.building.DataProvider
import com.sspirit.nadiiaspaceassistant.services.analyzers.building.BuildingMaterialHolder
import com.sspirit.nadiiaspaceassistant.services.analyzers.building.BuildingIssuesType
import com.sspirit.nadiiaspaceassistant.services.analyzers.building.BuildingMaterialsAnalyzingReport
import com.sspirit.nadiiaspaceassistant.services.analyzers.building.BuildingAnalyzer
import com.sspirit.nadiiaspaceassistant.services.analyzers.building.BuildingAnalyzingReport
import com.sspirit.nadiiaspaceassistant.services.analyzers.building.BuildingFixingData
import com.sspirit.nadiiaspaceassistant.services.analyzers.building.BuildingFixingType
import com.sspirit.nadiiaspaceassistant.services.analyzers.building.OuterSlabMaterialFixing
import com.sspirit.nadiiaspaceassistant.services.analyzers.building.OuterWallMaterialFixing
import com.sspirit.nadiiaspaceassistant.services.dataproviders.missions.propertyevacuation.PropertyEvacuationDataProvider
import com.sspirit.nadiiaspaceassistant.ui.AutosizeStyledButton
import com.sspirit.nadiiaspaceassistant.ui.HeaderText
import com.sspirit.nadiiaspaceassistant.ui.ElementsList
import com.sspirit.nadiiaspaceassistant.ui.FailableElementsList
import com.sspirit.nadiiaspaceassistant.ui.LocalSWLoadingState
import com.sspirit.nadiiaspaceassistant.ui.RegularText
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper
import com.sspirit.nadiiaspaceassistant.ui.ScrollableColumn
import com.sspirit.nadiiaspaceassistant.ui.SpacedHorizontalDivider
import com.sspirit.nadiiaspaceassistant.ui.TitleValueRow
import com.sspirit.nadiiaspaceassistant.utils.simpleCoroutineLaunch

private val LocalMissionId = compositionLocalOf<String?> { null }

@Composable
fun PropertyEvacuationAnalyzeView(missionId: String, navigator: NavHostController) {
    val mission = PropertyEvacuationDataProvider.getBy(missionId) ?: return
    val isLoading = rememberSaveable { mutableStateOf(false) }

    ScreenWrapper(navigator, "Анализ объекта", isLoading) {
        val report = BuildingAnalyzer(mission.building).report
        CompositionLocalProvider(LocalMissionId provides missionId) {
            ScrollableColumn {
                HeaderText("Информация")
                LootCard(report)
                Spacer(Modifier.height(8.dp))
                MaterialsReportCard("Материал дверей", report.materials[BuildingMaterialHolder.DOOR])
                MaterialsReportCard("Материал стен", report.materials[BuildingMaterialHolder.WALL])
                MaterialsReportCard("Материал перекрытий", report.materials[BuildingMaterialHolder.SLAB])
                IssuesGroup(report)
                FixesGroup(report)
            }
        }
    }
}

@Composable
private fun LootCard(report: BuildingAnalyzingReport) {
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
private fun MaterialsReportCard(title: String, report: BuildingMaterialsAnalyzingReport?) {
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
private fun IssuesGroup(report: BuildingAnalyzingReport) {
    if (!report.hasIssues) return

    SpacedHorizontalDivider()
    HeaderText("Проблемы")
    Spacer(Modifier.height(8.dp))
    FailableElementsList(BuildingIssuesType.entries) { IssuesTypeCard(it, report) }
}

@Composable
private fun IssuesTypeCard(type: BuildingIssuesType, report: BuildingAnalyzingReport): Boolean {
    val issues = report.issues[type] ?: return false
    if (issues.isEmpty()) return false
    val title = issueTypeTitle(type) + " (${issues.size})"

    Card(colors = issuesColors()) {
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

    return true
}


@Composable
private fun issuesColors() : CardColors {
    val backColor = colorResource(R.color.soft_red)
    return CardDefaults.cardColors(containerColor = backColor)
}

@Composable
private fun FixesGroup(report: BuildingAnalyzingReport) {
    if (!report.hasFixes) return

    SpacedHorizontalDivider()
    HeaderText("Исправления")
    Spacer(Modifier.height(8.dp))
    ElementsList(BuildingFixingType.entries) { FixingTypeButton(it, report) }
}

@Composable
private fun FixingTypeButton(type: BuildingFixingType, report: BuildingAnalyzingReport) {
    val fixesData = report.fixes[type] ?: return
    if (fixesData.isEmpty()) return

    val loadingState = LocalSWLoadingState.current ?: return
    val missionId = LocalMissionId.current ?: return

    AutosizeStyledButton(fixingTypeTitle(type)) {
        fixIssues(type, fixesData, loadingState, missionId)
    }
}

private fun issueTypeTitle(type: BuildingIssuesType): String =
    when (type) {
        BuildingIssuesType.EVENTS -> "Cобытия"
        BuildingIssuesType.LOCKS -> "Замки"
        BuildingIssuesType.LOCATIONS -> "Локации"
        BuildingIssuesType.DEVICES -> "Устройства"
        BuildingIssuesType.SLABS -> "Перекрытия"
        BuildingIssuesType.WALLS -> "Стены"
    }

private fun fixingTypeTitle(type: BuildingFixingType): String =
    when (type) {
        BuildingFixingType.OUTER_SLAB_MATERIAL -> "Материал внешних перекрытий"
        BuildingFixingType.OUTER_WALL_MATERIAL -> "Материал внешних стен"
    }

private fun fixIssues(
    type: BuildingFixingType,
    dataset: Set<BuildingFixingData>,
    loadingState: MutableState<Boolean>,
    missionId: String
) {
    when (type) {
        BuildingFixingType.OUTER_SLAB_MATERIAL ->
            fixOuterSlabsMaterial(dataset, loadingState, missionId)
        BuildingFixingType.OUTER_WALL_MATERIAL ->
            fixOuterWallsMaterial(dataset, loadingState, missionId)
    }
}

private fun fixOuterSlabsMaterial(
    dataset: Set<BuildingFixingData>,
    loadingState: MutableState<Boolean>,
    missionId: String
) {
    val affected = mutableSetOf<BuildingLocation>()
    for (data in dataset) {
        val slab = (data as? OuterSlabMaterialFixing)?.slab ?: continue
        val location = slab.upRoom?.location ?: continue
        affected.add(location)
        slab.material = BuildingMaterial.outer
    }

    simpleCoroutineLaunch(loadingState) {
        for (location in affected) {
            DataProvider.updateLocation(missionId, location)
        }
    }
}

private fun fixOuterWallsMaterial(
    dataset: Set<BuildingFixingData>,
    loadingState: MutableState<Boolean>,
    missionId: String
) {
    val affected = mutableSetOf<BuildingLocation>()
    for (data in dataset) {
        val wall = (data as? OuterWallMaterialFixing)?.wall ?: continue
        affected.add(wall.location)
        wall.material = BuildingMaterial.outer
    }

    simpleCoroutineLaunch(loadingState) {
        for (location in affected) {
            DataProvider.updateLocation(missionId, location)
        }
    }
}