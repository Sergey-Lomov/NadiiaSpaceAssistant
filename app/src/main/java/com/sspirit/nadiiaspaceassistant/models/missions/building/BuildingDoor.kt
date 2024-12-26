package com.sspirit.nadiiaspaceassistant.models.missions.building

import com.sspirit.nadiiaspaceassistant.models.missions.building.specloot.BuildingDoorKeyCardColor

enum class BuildingDoorHackingLevel(val string: String) {
    UNHACKABLE("-"),
    EASY("1"),
    MEDIUM("2"),
    HARD("3"),
    UNDEFINED("Неизвестно");

    companion object {
        fun byString(string: String): BuildingDoorHackingLevel {
            return BuildingDoorHackingLevel.entries.find { it.string == string } ?: UNDEFINED
        }
    }
}

enum class BuildingDoorTurn(val string: String) {
    AUTOMATIC("Авто"),
    EASY("1"),
    MEDIUM("2"),
    HARD("3"),
    UNDEFINED("Неизвестно");

    companion object {
        fun byString(string: String): BuildingDoorTurn {
            return BuildingDoorTurn.entries.find { it.string == string } ?: UNDEFINED
        }
    }
}

sealed class BuildingDoorLock {
    data class Code(val code: String) : BuildingDoorLock()
    data class Card(val color: BuildingDoorKeyCardColor) : BuildingDoorLock()
    data object Biometry : BuildingDoorLock()
    data object Remote : BuildingDoorLock()
    data object Undefined : BuildingDoorLock()

    companion object {
        fun byString(string: String): BuildingDoorLock {
            when (string) {
                "Биометрический" -> return Biometry
                "Красная карта" -> return Card(BuildingDoorKeyCardColor.RED)
                "Зеленая карта" -> return Card(BuildingDoorKeyCardColor.GREEN)
                "Синяя карта" -> return Card(BuildingDoorKeyCardColor.BLUE)
                "Удаленный" -> return Remote
                else -> Unit
            }

            if (string.startsWith("Кодовый")) {
                val code = string.split(" ").last()
                return Code(code)
            }

            return Undefined
        }
    }

    fun readable(): String {
        return when (this) {
            Biometry -> "Биометрический"
            Card(BuildingDoorKeyCardColor.RED) -> "Красная карта"
            Card(BuildingDoorKeyCardColor.GREEN) -> "Зеленая карта"
            Card(BuildingDoorKeyCardColor.BLUE) -> "Синяя карта"
            Remote -> "Удаленный"
            is Code -> "Кодовый $code"
            else -> ""
        }
    }
}

data class BuildingDoor(
    val passageway: BuildingPassageway,
    val locks: Array<BuildingDoorLock>,
    val hacking: BuildingDoorHackingLevel,
    val turn: BuildingDoorTurn,
    val material: BuildingMaterial
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BuildingDoor

        if (passageway != other.passageway) return false
        if (!locks.contentEquals(other.locks)) return false
        if (hacking != other.hacking) return false
        if (turn != other.turn) return false
        if (material != other.material) return false

        return true
    }

    override fun hashCode(): Int {
        var result = passageway.hashCode()
        result = 31 * result + locks.contentHashCode()
        result = 31 * result + hacking.hashCode()
        result = 31 * result + turn.hashCode()
        result = 31 * result + material.hashCode()
        return result
    }
}