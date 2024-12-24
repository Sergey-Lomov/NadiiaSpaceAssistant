package com.sspirit.nadiiaspaceassistant.models.missions.building

import com.sspirit.nadiiaspaceassistant.models.missions.building.specloot.BuildingDoorKeyCardColor

//enum class BuildingDoorLock(val string: String) {
//    BIOMETRY("Биометрический"),
//    RED_CARD("Красная карта"),
//    GREEN_CARD("Зеленая карта"),
//    BLUE_CARD("Синяя карта"),
//    REMOTE("Удаленный"),
//    CODE("Кодовый"),
//    UNDEFINED("Неизвестно");
//
//    companion object {
//        fun byString(string: String): BuildingDoorLock {
//            return BuildingDoorLock.entries.find { it.string == string } ?: UNDEFINED
//        }
//    }
//}

sealed class BuildingDoorLock {
    data class Code(val code: String) : BuildingDoorLock()
    data class Card(val color: BuildingDoorKeyCardColor) : BuildingDoorLock()
    data object Biometry : BuildingDoorLock()
    data object Remote : BuildingDoorLock()
    data object Undefined : BuildingDoorLock()
}

data class BuildingDoor(
    val realLocation1: RealLifeLocation,
    val realLocation2: RealLifeLocation,
    val locks: Array<BuildingDoorLock>,
    val hackingDifficult: Int,
    val turnDifficult: Int,
    val material: BuildingMaterial
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BuildingDoor

        if (realLocation1 != other.realLocation1) return false
        if (realLocation2 != other.realLocation2) return false
        if (!locks.contentEquals(other.locks)) return false
        if (hackingDifficult != other.hackingDifficult) return false
        if (turnDifficult != other.turnDifficult) return false
        if (material != other.material) return false

        return true
    }

    override fun hashCode(): Int {
        var result = realLocation1.hashCode()
        result = 31 * result + realLocation2.hashCode()
        result = 31 * result + locks.contentHashCode()
        result = 31 * result + hackingDifficult
        result = 31 * result + turnDifficult
        result = 31 * result + material.hashCode()
        return result
    }
}