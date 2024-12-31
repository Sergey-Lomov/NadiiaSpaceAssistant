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
    var position: BuildingBigObjectPosition
) {
    companion object {
        val PASSAGE_LOCK_SIZE = 2
        val CEILING_LADDER_SIZE = 3
    }

    val fullPosition: String
        get() {
            return when (val pos = this.position) {
                BuildingBigObjectPosition.Free,
                BuildingBigObjectPosition.Undefined,
                BuildingBigObjectPosition.Center -> pos.toString()

                is BuildingBigObjectPosition.NearWall -> {
                    val wall = pos.wall
                    val anotherRoom = if (wall.room1 == room) wall.room2 else wall.room1
                    pos.toString() + " (с ${anotherRoom.realLocation.string})"
                }

                is BuildingBigObjectPosition.LockPassage -> {
                    val passage = pos.passage
                    val anotherRoom = if (passage.room1 == room) passage.room2 else passage.room1
                    pos.toString() + " (в ${anotherRoom.realLocation.string})"
                }
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