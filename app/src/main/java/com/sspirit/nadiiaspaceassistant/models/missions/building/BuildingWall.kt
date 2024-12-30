package com.sspirit.nadiiaspaceassistant.models.missions.building

data class BuildingWall (
    val location: BuildingLocation,
    val room1: BuildingRoom,
    val room2: BuildingRoom,
    val material: BuildingMaterial,
    var hasHole: Boolean
) {
    val rooms: Array<BuildingRoom>
        get() = arrayOf(room1, room2)

    val realLocations: Array<RealLifeLocation>
        get() = rooms.map { it.realLocation }.toTypedArray()

    val isLockedByHeap: Boolean
        get() {
            val total = rooms
                .flatMap { it.bigObjects.asIterable() }
                .filter {
                    val pos = it.position
                    return@filter if (pos is BuildingBigObjectPosition.NearWall)
                        pos.wall == this
                    else
                        false
                }
                .sumOf { it.size }

            return total >= BuildingBigObject.PASSAGE_LOCK_SIZE
        }

    fun isBetween(r1: RealLifeLocation, r2: RealLifeLocation): Boolean =
        r1 in realLocations && r2 in realLocations

    fun anotherRoom(room: BuildingRoom): BuildingRoom =
        when (room) {
            room1 -> room2
            room2 -> room1
            else -> throw Exception("Send to wall.anotherRoom room which not connected to wall")
        }

    override fun hashCode(): Int {
        var result = location.id.hashCode()
        result = 31 * result + room1.hashCode()
        result = 31 * result + room2.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BuildingWall

        if (location != other.location) return false
        if (room1 != other.room1) return false
        if (room2 != other.room2) return false
        if (material != other.material) return false
        if (hasHole != other.hasHole) return false

        return true
    }
}