package com.sspirit.nadiiaspaceassistant.models.missions.building.devices

enum class BuildingDeviceType(val title: String) {
    SAFETY_CONSOLE("Консоль безопасности"),
    SUPPORT_CONSOLE("Консоль жизнеобеспечения"),
    ENERGY_NODE("Энергоузел"),
    HOLO_PLAN("Голо-план"),
    ENERGY_CORE("Энергоядро (реактор)"),
    ACID_TANK("Резервуар кислоты"),
    MAINFRAME("Мэинфреим"),
    AUTO_DOCTOR("Автодоктор"),
    UNDEFINED("Неопределено");

    override fun toString(): String = title
}

private typealias Type = BuildingDeviceType

sealed class BuildingDevice(val type: BuildingDeviceType) {
    val title: String
        get() = type.title

    open var validDetailsData: Boolean = true

    data class SafetyConsole(
        var hacked: Boolean,
        override var validDetailsData: Boolean
    ) : BuildingDevice(Type.SAFETY_CONSOLE) {
        override val details: String = "Консоль безопасности позволяет управлять замками с дистанционным управлением по всему комплексу"
    }

    data object SupportConsole : BuildingDevice(Type.SUPPORT_CONSOLE) {
        override val details: String = "Консоль жизнеобеспечения позволяет управлять вентиляционными шахтами по всему комплексу"
    }

    data class EnergyNode(
        var state: EnergyNodeState,
        override var validDetailsData: Boolean
    ) : BuildingDevice(Type.ENERGY_NODE) {
        override val details: String = "Служит для распределения энергии между системами объекта. Можно перенастроить чтобы замедлить перегрузку реактора."
    }

    data class HoloPlan(
        val locations: Array<String>,
        override var validDetailsData: Boolean
    ) : BuildingDevice(Type.HOLO_PLAN) {
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

    data object EnergyCore : BuildingDevice(Type.ENERGY_CORE) {
        override val details: String = "Ядро энергетической системы. Можно замедлить перегрузку используя стержни стабилизации."
    }

    data class AcidTank(
        var charges: Int,
        override var validDetailsData: Boolean
    ) : BuildingDevice(Type.ACID_TANK) {
        override val details: String = "Резервуар с кислотой и зарядным устройством, с помощью которого можно перезаряжть кислотные заряды"
    }

    data object Mainframe : BuildingDevice(Type.MAINFRAME) {
        override val details: String = "Информационное сердце комплекса. Здесь хранятся самые важные и самые не важные данные."
    }

    data class AutoDoctor(
        var energy: Int,
        override var validDetailsData: Boolean
    ) : BuildingDevice(Type.AUTO_DOCTOR) {
        override val details: String = "Продвинутая автоматизированная медицинская система. Способна излечить многие проблемы даже без участия живого врача."
    }

    data object Undefined : BuildingDevice(Type.UNDEFINED) {
        override val details: String = "Сбой! Тип устройства не определен!"
    }

    abstract val details: String
}