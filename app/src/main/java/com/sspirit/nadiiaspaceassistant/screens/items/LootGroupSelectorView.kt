package com.sspirit.nadiiaspaceassistant.screens.items

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.models.items.LootGroup
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingRoom
import com.sspirit.nadiiaspaceassistant.services.ViewModelsRegister
import com.sspirit.nadiiaspaceassistant.services.dataproviders.LootGroupsDataProvider
import com.sspirit.nadiiaspaceassistant.services.dataproviders.missions.propertyevacuation.RoomsDescriptorsDataProvider
import com.sspirit.nadiiaspaceassistant.ui.CenteredInfoTextCard
import com.sspirit.nadiiaspaceassistant.ui.HeaderText
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper
import com.sspirit.nadiiaspaceassistant.ui.ScrollableColumn
import com.sspirit.nadiiaspaceassistant.ui.SpacedHorizontalDivider
import com.sspirit.nadiiaspaceassistant.viewmodels.building.LootGroupSelectorViewModel

private enum class LootGroupResolution(val title: String) {
    NO_RESOLUTION("Полный список"),
    ROOM_RECOMMENDED("Рекомендовано для комнаты"),
    UN_RECOMMENDED("Не рекомендовано"),
}

private val order = arrayOf(
    LootGroupResolution.ROOM_RECOMMENDED,
    LootGroupResolution.UN_RECOMMENDED,
    LootGroupResolution.NO_RESOLUTION
)

@Composable
fun LootGroupSelectorView(modelId: String, navigator: NavHostController) {
    val model = ViewModelsRegister.get<LootGroupSelectorViewModel>(modelId) ?: return
    val grouped = LootGroupsDataProvider.groups.groupBy { groupResolution(it, model.room) }

    ScreenWrapper(navigator, "Выбор контейнера") {
        ScrollableColumn {
            for (resolution in order) {
                val groups = grouped[resolution] ?: continue
                HeaderText(resolution.title)
                Spacer(Modifier.height(16.dp))
                for (group in groups) {
                    CenteredInfoTextCard(group.title) {
                        model.onSelect(group)
                        navigator.popBackStack()
                    }

                    if (group != groups.last())
                        Spacer(Modifier.height(8.dp))
                }

                if (resolution !== order.last())
                    SpacedHorizontalDivider()
            }
        }
    }
}

private fun groupResolution(group: LootGroup, room: BuildingRoom?): LootGroupResolution {
    if (room == null) return LootGroupResolution.NO_RESOLUTION

    val descriptor = RoomsDescriptorsDataProvider.descriptors[room.type]
        ?: return LootGroupResolution.NO_RESOLUTION

    return if (group in descriptor.loot) LootGroupResolution.ROOM_RECOMMENDED
    else LootGroupResolution.UN_RECOMMENDED
}