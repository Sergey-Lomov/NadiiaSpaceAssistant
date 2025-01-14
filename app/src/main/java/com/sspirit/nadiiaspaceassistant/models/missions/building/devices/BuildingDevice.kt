package com.sspirit.nadiiaspaceassistant.models.missions.building.devices

sealed class BuildingDevice(val title: String) {
    data class SafetyConsole(var hacked: Boolean) : BuildingDevice("Консоль безопасности") {
        override val details: String = "Консоль безопасности позволяет управлять замками с дистанционным управлением по всему комплексу"
    }

    data object SupportConsole : BuildingDevice("Консоль жизнеобеспечения") {
        override val details: String = "Консоль жизнеобеспечения позволяет управлять вентиляционными шахтами по всему комплексу"
    }

    data class EnergyNode(var state: EnergyNodeState) : BuildingDevice("Энергоузел") {
        override val details: String = "Служит для распределения энергии между системами объекта. Можно перенастроить чтобы замедлить перегрузку реактора."
    }

    data class HoloPlan(val locations: Array<String>) : BuildingDevice("Голо-план") {
        override val details: String = "Содержит полезную (а иногда и не очень) информацию о комплексе"

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as HoloPlan

            if (!locations.contentEquals(other.locations)) return false
            if (details != other.details) return false

            return true
        }

        override fun hashCode(): Int {
            var result = locations.contentHashCode()
            result = 31 * result + details.hashCode()
            return result
        }
    }

    data object EnergyCore : BuildingDevice("Энергоядро (реактор)") {
        override val details: String = "Ядро энергетической системы. Можно замедлить перегрузку используя стержни стабилизации."
    }

    data class AcidTank(var charges: Int) : BuildingDevice("Резервуар кислоты") {
        override val details: String = "Резервуар с кислотой и зарядным устройством, с помощью которого можно перезаряжть кислотные заряды"
    }

    data object Mainframe : BuildingDevice("Мэинфреим") {
        override val details: String = "Информационное сердце комплекса. Здесь хранятся самые важные и самые не важные данные."
    }

    data class AutoDoctor(var energy: Int) : BuildingDevice("Автодоктор") {
        override val details: String = "Продвинутая автоматизированная медицинская система. Способна излечить многие проблемы даже без участия живого врача."
    }

    data object Undefined : BuildingDevice("Неопределено") {
        override val details: String = "Сбой! Тип устройства не определен!"
    }

    abstract val details: String
}