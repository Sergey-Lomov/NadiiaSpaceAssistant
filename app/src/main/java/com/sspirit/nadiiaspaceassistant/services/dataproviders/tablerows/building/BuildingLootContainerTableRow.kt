package com.sspirit.nadiiaspaceassistant.services.dataproviders.tablerows.building

import android.util.Log
import com.sspirit.nadiiaspaceassistant.models.missions.building.Building
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingBigObject
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingBigObjectPosition
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingBigObjectPosition.Center
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingBigObjectPosition.Free
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingBigObjectPosition.LockPassage
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingBigObjectPosition.NearWall
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingBigObjectPosition.Undefined
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingLootContainer
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingLootContainerItem
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingPassage
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingRoom
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingWall
import com.sspirit.nadiiaspaceassistant.models.missions.building.RealLifeLocation
import com.sspirit.nadiiaspaceassistant.services.dataproviders.ItemDataProvider
import com.sspirit.nadiiaspaceassistant.services.dataproviders.LootGroupsDataProvider
import com.sspirit.nadiiaspaceassistant.services.dataproviders.logTag
import com.sspirit.nadiiaspaceassistant.services.dataproviders.missions.propertyevacuation.PropertyEvacuationDataProvider
import com.sspirit.nadiiaspaceassistant.utils.readInt
import com.sspirit.nadiiaspaceassistant.utils.readString
import com.sspirit.nadiiaspaceassistant.utils.write
import kotlin.jvm.internal.Ref.IntRef

data class BuildingLootContainerTableRow(
    val id : String,
    val groupId: String,
    val itemId: String,
    val amount: Int,
    val locationId: String,
    val realLocation: String,
) {

    companion object {
        fun parse(raw: Array<Any>, ref: IntRef = IntRef()): BuildingLootContainerTableRow {
            return BuildingLootContainerTableRow(
                id = raw.readString(ref),
                groupId = raw.readString(ref),
                locationId = raw.readString(ref),
                realLocation = raw.readString(ref),
                itemId = raw.readString(ref),
                amount = raw.readInt(ref),
            )
        }

        fun from(source: BuildingLootContainer): Array<BuildingLootContainerTableRow> =
            source.items.map {
                BuildingLootContainerTableRow(
                    id = source.id,
                    groupId = source.group.id,
                    locationId = source.room.location.id,
                    realLocation = source.room.realLocation.string,
                    itemId = it.item.id,
                    amount = it.amount,
                )
            }.toTypedArray()
    }

    fun toRawData(): List<String> {
        val data = mutableListOf<String>()
        data.write(id)
        data.write(groupId)
        data.write(locationId)
        data.write(realLocation)
        data.write(itemId)
        data.write(amount)
        return data
    }
}

fun Array<BuildingLootContainerTableRow>.toBuildingLootContainers(building: Building): Array<BuildingLootContainer> =
    groupBy { it.id }
        .mapNotNull {
            val firstRow = it.value.first()
            val items = it.value.mapNotNull inner@{ row ->
                val item = ItemDataProvider.getDescriptor(row.itemId) ?: return@inner null
                BuildingLootContainerItem(item, row.amount)
            }
            val realLocation = RealLifeLocation.byString(firstRow.realLocation)
            val room = building.room(firstRow.locationId, realLocation) ?: return@mapNotNull null
            val group = LootGroupsDataProvider.getGroup(firstRow.groupId) ?: return@mapNotNull null

            BuildingLootContainer(
                id = it.key,
                room = room,
                group = group,
                items = items.toMutableList()
            )
        }
        .toTypedArray()