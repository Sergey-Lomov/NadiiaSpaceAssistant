package com.sspirit.nadiiaspaceassistant.services.dataproviders.missions.propertyevacuation

import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingRoom
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingRoomDescriptor
import com.sspirit.nadiiaspaceassistant.services.dataproviders.GoogleSheetDataProvider
import com.sspirit.nadiiaspaceassistant.services.dataproviders.LootGroupsDataProvider
import com.sspirit.nadiiaspaceassistant.services.dataproviders.tablerows.building.RoomDescriptorTableRow
import com.sspirit.nadiiaspaceassistant.utils.plusHours
import java.time.LocalDateTime

private const val expirationHours = 24
private const val spreadsheetId = "1e9BueiGhzgvlNSKBjG7Tt6lCJop30ZRkowxuBX4qnuk"
private const val descriptorsRange = "Rooms!A2:J200"

object RoomsDescriptorsDataProvider : GoogleSheetDataProvider() {
    val descriptors = mutableMapOf<String, BuildingRoomDescriptor>()

    fun downloadRoomsDescriptors(forced: Boolean = false) {
        if (expirationDate != null && !forced) {
            if (LocalDateTime.now() < expirationDate) {
                return
            }
        }

        val response = request(spreadsheetId, descriptorsRange)
        descriptors.clear()
        val rows = parseToArray(
            range = response,
            error = "Invalid room descriptors data",
            parser = RoomDescriptorTableRow::parse,
            upDownMerge = arrayOf(0, 1)
        )
        for (row in rows) {
            if (descriptors[row.type] == null) {
                descriptors[row.type] = BuildingRoomDescriptor(
                    type = row.type,
                    description = row.description,
                    deviceTypes =  row.devices
                )
            }

            val lootGroup = LootGroupsDataProvider.getGroup(row.lootId)
            if (lootGroup != null)
                descriptors[row.type]?.loot?.add(lootGroup)
        }

        expirationDate = LocalDateTime.now().plusHours(expirationHours)
    }

    fun getFor(room: BuildingRoom): BuildingRoomDescriptor? = descriptors[room.type]
}