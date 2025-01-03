package com.sspirit.nadiiaspaceassistant.services.dataproviders.tablerows.location

import com.sspirit.nadiiaspaceassistant.models.missions.building.devices.BuildingDevice
import com.sspirit.nadiiaspaceassistant.models.missions.building.devices.BuildingDevice.AcidTank
import com.sspirit.nadiiaspaceassistant.models.missions.building.devices.BuildingDevice.AutoDoctor
import com.sspirit.nadiiaspaceassistant.models.missions.building.devices.BuildingDevice.EnergyCore
import com.sspirit.nadiiaspaceassistant.models.missions.building.devices.BuildingDevice.EnergyNode
import com.sspirit.nadiiaspaceassistant.models.missions.building.devices.BuildingDevice.HoloPlan
import com.sspirit.nadiiaspaceassistant.models.missions.building.devices.BuildingDevice.Mainframe
import com.sspirit.nadiiaspaceassistant.models.missions.building.devices.BuildingDevice.SafetyConsole
import com.sspirit.nadiiaspaceassistant.models.missions.building.devices.BuildingDevice.SupportConsole
import com.sspirit.nadiiaspaceassistant.models.missions.building.devices.BuildingDevice.Undefined
import com.sspirit.nadiiaspaceassistant.models.missions.building.devices.EnergyNodeState
import com.sspirit.nadiiaspaceassistant.utils.readSplittedString
import com.sspirit.nadiiaspaceassistant.utils.write
import kotlin.jvm.internal.Ref.IntRef

private const val ADDITIONAL_DATA_ARRAY_SEPARATOR = "|"
private val dataSizes = mapOf(
    "Консоль безопасности" to 0,
    "Консоль жизнеобеспечения" to 0,
    "Голо-план" to 1,
    "Энергоузел" to 1,
    "Энергоядро (реактор)" to 0,
    "Резервуар кислоты" to 1,
    "Мэинфреим" to 0,
    "Автодоктор" to 0,
)

data class LocationTableRowDevice(
    val type: String,
    val data: Array<String>
) {
    companion object {
        fun parseArray(raw: Array<Any>, displacement: IntRef): Array<LocationTableRowDevice> {
            val types = raw.readSplittedString(displacement)
            val data = raw.readSplittedString(displacement)
            val iterator = data.iterator()
            return types
                .map {
                    val dataRange = 0 until (dataSizes[it] ?: 0)
                    LocationTableRowDevice(
                        type = it,
                        data = dataRange
                            .map { iterator.next() }
                            .toTypedArray()
                    )
                }
                .toTypedArray()
        }

        fun from(source: BuildingDevice) : LocationTableRowDevice =
            LocationTableRowDevice(
                type = source.title,
                data = additionalData(source)
            )
    }

    fun toBuildingDevice() : BuildingDevice =
        when (type) {
            "Консоль безопасности" -> {
                val hacked = data.firstOrNull()?.toBoolean() ?: false
                SafetyConsole(hacked)
            }
            "Консоль жизнеобеспечения" -> SupportConsole
            "Голо-план" -> {
                val locIds = (data.firstOrNull() ?: "")
                    .split(ADDITIONAL_DATA_ARRAY_SEPARATOR)
                    .toTypedArray()
                HoloPlan(locIds)
            }
            "Энергоузел" -> {
                val state = EnergyNodeState.byString(data.firstOrNull() ?: "")
                EnergyNode(state)
            }
            "Энергоядро (реактор)" -> EnergyCore
            "Резервуар кислоты" -> {
                val charges = data.firstOrNull()?.toInt() ?: 0
                AcidTank(charges)
            }
            "Мэинфреим" -> Mainframe
            "Автодоктор" -> AutoDoctor
            else -> Undefined
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LocationTableRowDevice

        if (type != other.type) return false
        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + data.contentHashCode()
        return result
    }
}

private fun additionalData(device: BuildingDevice): Array<String> {
    return when (device) {
        SupportConsole,
        EnergyCore,
        Mainframe,
        AutoDoctor,
        Undefined -> arrayOf()
        is SafetyConsole -> arrayOf(device.hacked.toString())
        is HoloPlan -> device.locations
        is EnergyNode -> arrayOf(device.state.toString())
        is AcidTank -> arrayOf(device.charges.toString())
    }
}

fun MutableList<String>.write(devices: Array<LocationTableRowDevice>) {
    val types = devices.map { it.type }.toTypedArray()
    val data = devices
        .map { it.data.joinToString(separator = ADDITIONAL_DATA_ARRAY_SEPARATOR) }
        .toTypedArray()
    write(types)
    write(data)
}