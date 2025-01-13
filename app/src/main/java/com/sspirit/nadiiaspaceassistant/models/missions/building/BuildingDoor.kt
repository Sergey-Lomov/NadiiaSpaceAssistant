package com.sspirit.nadiiaspaceassistant.models.missions.building

import com.sspirit.nadiiaspaceassistant.models.missions.building.specloot.BuildingDoorKeyCardColor

enum class BuildingDoorHackingLevel(val string: String) {
    UNHACKABLE("-"),
    EASY("1"),
    MEDIUM("2"),
    HARD("3"),
    UNDEFINED("?");

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
    BROKEN("Сломан"),
    UNDEFINED("?");

    companion object {
        fun byString(string: String): BuildingDoorTurn {
            return BuildingDoorTurn.entries.find { it.string == string } ?: UNDEFINED
        }
    }
}

sealed class BuildingDoorLock {
    data class Code(val code: String) : BuildingDoorLock() {
        override fun toString(): String = "Кодовый $code"
    }

    data class Card(val color: BuildingDoorKeyCardColor) : BuildingDoorLock() {
        override fun toString(): String =
            when (color) {
                BuildingDoorKeyCardColor.RED -> "Красная карта"
                BuildingDoorKeyCardColor.GREEN -> "Зеленая карта"
                BuildingDoorKeyCardColor.BLUE -> "Синяя карта"
                BuildingDoorKeyCardColor.UNDEFINED -> "Неопознанная карта"
            }
    }

    data object Biometry : BuildingDoorLock() {
        override fun toString(): String = "Биометрический"
    }

    data object Remote : BuildingDoorLock() {
        override fun toString(): String = "Удаленный"
    }

    data object Undefined : BuildingDoorLock() {
        override fun toString(): String = "Неизвестно"
    }

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
}

data class BuildingDoor(
    val passageway: BuildingPassage,
    var locks: Array<BuildingDoorLock>,
    val hacking: BuildingDoorHackingLevel,
    val turn: BuildingDoorTurn,
    val material: BuildingMaterial
) {
    val isDestructible: Boolean
        get() = material.isDestructible

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