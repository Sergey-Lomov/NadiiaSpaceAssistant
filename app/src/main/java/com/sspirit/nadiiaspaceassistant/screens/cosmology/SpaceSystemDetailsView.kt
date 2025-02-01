package com.sspirit.nadiiaspaceassistant.screens.cosmology

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.Workspaces
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.utils.navigateTo
import com.sspirit.nadiiaspaceassistant.models.cosmology.SpaceSystem
import com.sspirit.nadiiaspaceassistant.navigation.Routes
import com.sspirit.nadiiaspaceassistant.services.dataproviders.CosmologyDataProvider
import com.sspirit.nadiiaspaceassistant.services.external_monitor.ExternalMonitorManager
import com.sspirit.nadiiaspaceassistant.services.external_monitor.LocalServerManager
import com.sspirit.nadiiaspaceassistant.ui.RegularText
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper
import com.sspirit.nadiiaspaceassistant.ui.ScrollableColumn
import com.sspirit.nadiiaspaceassistant.ui.SpacedHorizontalDivider
import com.sspirit.nadiiaspaceassistant.ui.StyledIconButton
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Composable
fun SpaceSystemDetailsView(system: SpaceSystem, navigator: NavHostController) {
    ScreenWrapper(navigator, "Детали системы") {
        ScrollableColumn {
            InfoCard(system)
            SpacedHorizontalDivider()
            SpaceObjectSelector(system = system) { obj ->
                val indices = CosmologyDataProvider.sectorMap.indicesOf(obj)
                val json = Json.encodeToString(indices)
                navigator.navigateTo(Routes.SpaceObjectDetails, json)
            }
        }
    }
}

@Composable
private fun InfoCard(system: SpaceSystem) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(8.dp)) {
            RegularText(system.info)
            Spacer(Modifier.height(8.dp))
            Row {
                StyledIconButton(
                    icon = Icons.Default.Brightness7,
                    modifier = Modifier.weight(1f)
                ) {
                    ExternalMonitorManager.showSystemStar(system)
                }

                Spacer(Modifier.width(16.dp))

                StyledIconButton(
                    icon = Icons.Default.Workspaces,
                    modifier = Modifier.weight(1f)
                ) {
                    ExternalMonitorManager.showSystemGallery(system)
                }
            }
        }
    }
}