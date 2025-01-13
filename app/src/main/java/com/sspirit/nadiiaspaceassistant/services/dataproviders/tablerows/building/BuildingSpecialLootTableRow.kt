package com.sspirit.nadiiaspaceassistant.services.dataproviders.tablerows.building

import com.sspirit.nadiiaspaceassistant.models.missions.building.Building
import com.sspirit.nadiiaspaceassistant.models.missions.building.specloot.BuildingSpecialLootContainer
import com.sspirit.nadiiaspaceassistant.models.missions.building.RealLifeLocation
import com.sspirit.nadiiaspaceassistant.models.missions.building.specloot.BuildingDoorCode
import com.sspirit.nadiiaspaceassistant.models.missions.building.specloot.BuildingDoorKeyCard
import com.sspirit.nadiiaspaceassistant.models.missions.building.specloot.BuildingDoorKeyCardColor
import com.sspirit.nadiiaspaceassistant.models.missions.building.specloot.SpecialLoot
import com.sspirit.nadiiaspaceassistant.models.missions.building.specloot.StoryItem
import com.sspirit.nadiiaspaceassistant.utils.readSplittedString
import com.sspirit.nadiiaspaceassistant.utils.readString
import com.sspirit.nadiiaspaceassistant.utils.write
import kotlin.jvm.internal.Ref.IntRef

data class BuildingSpecialLootTableRow(
    val id: String,
    val type: String,
    val params: Array<String>,
    val locationId: String,
    val realLocation: String,
) {
    companion object {
        fun parse(raw: Array<Any>, ref: IntRef = IntRef()): BuildingSpecialLootTableRow {
            return BuildingSpecialLootTableRow(
                id = raw.readString(ref),
                type = raw.readString(ref),
                params = raw.readSplittedString(ref),
                locationId = raw.readString(ref),
                realLocation = raw.readString(ref),
            )
        }

        fun from(source: BuildingSpecialLootContainer): BuildingSpecialLootTableRow =
            BuildingSpecialLootTableRow(
                id = source.id,
                type = typeString(source.loot),
                params = paramsStrings(source.loot),
                locationId = source.room?.location?.id ?: "",
                realLocation = source.room?.realLocation?.string ?: ""
            )
    }

    fun toBuildingSpecialLoot(building: Building): BuildingSpecialLootContainer? {
        val loot = lootBy(type, params) ?: return null
        val realLocationValue = RealLifeLocation.byString(realLocation)
        val room = building.room(locationId, realLocationValue)
        return BuildingSpecialLootContainer(id, loot, room)
    }

    fun toRawData(): List<String> {
        val data = mutableListOf<String>()
        data.write(id)
        data.write(type)
        data.write(params)
        data.write(locationId)
        data.write(realLocation)
        return data
    }
}

private val typesString = mapOf(
    BuildingDoorCode::class to "Код",
    BuildingDoorKeyCard::class to "Карта доступа",
    StoryItem::class to "Цель задания"
)

private fun typeString(loot: SpecialLoot): String =
    typesString[loot::class] ?: "Неопределено"

private fun paramsStrings(loot: SpecialLoot): Array<String> =
    when (loot) {
        is BuildingDoorCode -> arrayOf(loot.code)
        is BuildingDoorKeyCard -> arrayOf(loot.color.toString())
        is StoryItem -> arrayOf(loot.title)
        else -> arrayOf()
    }

private fun lootBy(type: String, params: Array<String>): SpecialLoot? =
    when (type) {
        typesString[BuildingDoorCode::class] -> BuildingDoorCode(params.first())
        typesString[BuildingDoorKeyCard::class] -> {
            val color = BuildingDoorKeyCardColor.byString(params.first())
            BuildingDoorKeyCard(color)
        }
        typesString[StoryItem::class] -> StoryItem(params.first())
        else -> null
    }