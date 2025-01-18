package com.sspirit.nadiiaspaceassistant.services.dataproviders.tablerows.building

import com.sspirit.nadiiaspaceassistant.models.missions.building.Building
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingBigObject
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingBigObjectPosition
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingBigObjectPosition.Center
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingBigObjectPosition.Free
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingBigObjectPosition.LockPassage
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingBigObjectPosition.NearWall
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingBigObjectPosition.Undefined
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingPassage
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingRoom
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingWall
import com.sspirit.nadiiaspaceassistant.models.missions.building.RealLifeLocation
import com.sspirit.nadiiaspaceassistant.services.dataproviders.tablerows.RawDataConvertibleTableRow
import com.sspirit.nadiiaspaceassistant.utils.readInt
import com.sspirit.nadiiaspaceassistant.utils.readSplittedString
import com.sspirit.nadiiaspaceassistant.utils.readString
import com.sspirit.nadiiaspaceassistant.utils.write
import kotlin.jvm.internal.Ref.IntRef

data class BuildingBigObjectTableRow(
    val id : String,
    val size: Int,
    val locationId: String,
    val locationTitle: String,
    val realLocation: String,
    val position: String,
    val positionInfo: Array<String>
) : RawDataConvertibleTableRow {

    companion object {
        fun parse(raw: Array<Any>, ref: IntRef = IntRef()): BuildingBigObjectTableRow {
            return BuildingBigObjectTableRow(
                id = raw.readString(ref),
                size = raw.readInt(ref),
                locationId = raw.readString(ref),
                locationTitle = raw.readString(ref),
                realLocation = raw.readString(ref),
                position = raw.readString(ref),
                positionInfo = raw.readSplittedString(ref)
            )
        }

        fun from(source: BuildingBigObject): BuildingBigObjectTableRow =
            BuildingBigObjectTableRow(
                id = source.id,
                size = source.size,
                locationId = source.room.location.id,
                locationTitle = source.room.location.title,
                realLocation = source.room.realLocation.string,
                position = source.position.toString(),
                positionInfo = positionInfo(source.position, source.room)
            )
    }

    fun toBuildingBigObject(building: Building): BuildingBigObject? {
        val realLocationValue = RealLifeLocation.byString(realLocation)
        val room = building.room(locationId, realLocationValue) ?: return null
        val position = positionBy(position, positionInfo, room) ?: return null
        return BuildingBigObject(
            id = id,
            size = size,
            room = room,
            position = position
        )
    }

    override fun toRawData(): List<String> {
        val data = mutableListOf<String>()
        data.write(id)
        data.write(size)
        data.write(locationId)
        data.write(locationTitle)
        data.write(realLocation)
        data.write(position)
        data.write(positionInfo)
        return data
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BuildingBigObjectTableRow

        if (id != other.id) return false
        if (size != other.size) return false
        if (locationId != other.locationId) return false
        if (realLocation != other.realLocation) return false
        if (position != other.position) return false
        if (!positionInfo.contentEquals(other.positionInfo)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + size
        result = 31 * result + locationId.hashCode()
        result = 31 * result + realLocation.hashCode()
        result = 31 * result + position.hashCode()
        result = 31 * result + positionInfo.contentHashCode()
        return result
    }
}

private fun positionBy(string: String, additionalData: Array<String>, room: BuildingRoom): BuildingBigObjectPosition? {
    return when (string) {
        "Свободное" -> Free
        "В центре" -> Center
        "У стены" -> {
            val wall = parseWallData(additionalData, room) ?: return null
            NearWall(wall)
        }
        "Блокирует проход" -> {
            val passage = parsePassageData(additionalData, room) ?: return null
            LockPassage(passage)
        }
        else -> Undefined
    }
}

private fun positionInfo(position: BuildingBigObjectPosition, room: BuildingRoom): Array<String> =
    when (position) {
        Free, Undefined, Center -> arrayOf()
        is LockPassage -> {
            val anotherRoom = position.passage.anotherRoom(room)
            arrayOf(anotherRoom.realLocation.string)
        }
        is NearWall -> {
            val anotherRoom = position.wall.anotherRoom(room)
            arrayOf(anotherRoom.realLocation.string)
        }
    }

private fun parseWallData(data: Array<String>, room: BuildingRoom): BuildingWall? {
    val realLocation = RealLifeLocation.byString(data[0])
    return room.walls
        .firstOrNull { it.isBetween(room.realLocation, realLocation) }
}

private fun parsePassageData(data: Array<String>, room: BuildingRoom): BuildingPassage? {
    val realLocation = RealLifeLocation.byString(data[0])
    return room.passages
        .firstOrNull { it.isBetween(room.realLocation, realLocation) }
}