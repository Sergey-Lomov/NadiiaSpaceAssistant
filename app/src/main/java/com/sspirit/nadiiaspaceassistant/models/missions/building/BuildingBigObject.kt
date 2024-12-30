package com.sspirit.nadiiaspaceassistant.models.missions.building

sealed class BuildingBigObjectPosition {
    data class LockPassage(val passage: BuildingPassage) : BuildingBigObjectPosition() {
        override fun toString(): String = "Блокирует проход"
    }

    data class NearWall(val wall: BuildingWall) : BuildingBigObjectPosition() {
        override fun toString(): String = "У стены"
    }

    data object Free : BuildingBigObjectPosition() {
        override fun toString(): String = "Свободное"
    }

    data object Center : BuildingBigObjectPosition() {
        override fun toString(): String = "В центре"
    }

    data object Undefined : BuildingBigObjectPosition() {
        override fun toString(): String = "Неизвестно"
    }
}

data class BuildingBigObject(
    val id: String,
    val size: Int,
    var room: BuildingRoom,
    val position: BuildingBigObjectPosition
) {
    val fullPosition: String
        get() = when (position) {
            BuildingBigObjectPosition.Free,
            BuildingBigObjectPosition.Undefined,
            BuildingBigObjectPosition.Center -> position.toString()
            is BuildingBigObjectPosition.NearWall -> {
                val wall = position.wall
                val anotherRoom = if (wall.room1 == room) wall.room2 else wall.room1
                position.toString() + " (с ${anotherRoom.realLocation.string})"
            }
            is BuildingBigObjectPosition.LockPassage -> {
                val passage = position.passage
                val anotherRoom = if (passage.room1 == room) passage.room2 else passage.room1
                position.toString() + " (в ${anotherRoom.realLocation.string})"
            }
        }

    fun isMovable(power: Int): Boolean =
        when (size) {
            1 -> power >= 10
            2 -> power >= 15
            3 -> power >= 20
            else -> true
        }

}