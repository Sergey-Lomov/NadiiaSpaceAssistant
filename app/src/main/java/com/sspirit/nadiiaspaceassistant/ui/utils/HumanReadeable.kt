package com.sspirit.nadiiaspaceassistant.ui.utils

import com.sspirit.nadiiaspaceassistant.utils.toString
import com.sspirit.nadiiaspaceassistant.services.fabrics.CosmonavigationTaskGenerationType
import com.sspirit.nadiiaspaceassistant.models.CosmonavigationTaskType
import com.sspirit.nadiiaspaceassistant.models.ItemsStorageNode
import com.sspirit.nadiiaspaceassistant.models.cosmology.SpacePOI
import com.sspirit.nadiiaspaceassistant.models.cosmology.SpacePOIStatus
import com.sspirit.nadiiaspaceassistant.models.missions.MissionType
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingRoom
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingSlab

fun humanReadable(bool: Boolean): String = if (bool) "Да" else "Нет"

fun humanReadable(status: SpacePOIStatus): String {
    return when (status) {
        SpacePOIStatus.AVAILABLE -> "Доступно"
        SpacePOIStatus.RESTRICTED -> "Ограничено"
        SpacePOIStatus.HIDDEN -> "Скрыто"
        SpacePOIStatus.UNDEFINED -> "Не валидно"
    }
}

fun humanReadable(type: CosmonavigationTaskType) : String {
   return when (type) {
        CosmonavigationTaskType.COLORED_GESTURES -> "Цветные жесты"
        CosmonavigationTaskType.GESTURES_FLOW -> "Поток жестов"
        CosmonavigationTaskType.FORMS_COMPARISON -> "Сравнение форм"
        CosmonavigationTaskType.COLORED_FINGERS -> "Цветные пальцы"
    }
}

fun humanReadable(type: CosmonavigationTaskGenerationType) : String {
    return when (type) {
        CosmonavigationTaskGenerationType.RANDOM -> "Случайный"
        CosmonavigationTaskGenerationType.COLORED_GESTURES -> "Цветные жесты"
        CosmonavigationTaskGenerationType.GESTURES_FLOW -> "Поток жестов"
        CosmonavigationTaskGenerationType.FORMS_COMPARISON -> "Сравнение форм"
        CosmonavigationTaskGenerationType.COLORED_FINGERS -> "Цветные пальцы"
    }
}

fun humanReadable(type: MissionType): String {
    return when (type) {
        MissionType.STORY -> "Приключение"
        MissionType.MEDS_TEST -> "Испытание препаратов"
        MissionType.ENERGY_LINES -> "Починка цепей реактора"
        MissionType.PROPERTY_EVACUATION -> "Эвакуация собственности"
        MissionType.UNDEFINED -> "Неопределено"
    }
}

fun humanReadableDifficult(difficult: Float, withValue: Boolean = true): String {
    val value = difficult.toString(2)
    val string = when (difficult) {
        in 0.0..0.15 -> "Элементарная"
        in 0.15..0.35 -> "Простая"
        in 0.35..0.65 -> "Средняя"
        in 0.65..0.85 -> "Сложная"
        in 0.85..1.0 -> "Невероятная"
        else -> "Неопределено"
    }

    return if (withValue) "$string($value)" else string
}

fun humanTime(seconds: Int) : String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60

    return buildString {
        if (hours > 0) append("$hours:")
        if (minutes > 0) append("$minutes:")
        append("$secs")
    }.trim()
}

fun humanReadableRoute(poi: SpacePOI): String {
    return "${poi.parent.parent.title}(${poi.parent.parent.id}) -> ${poi.parent.title} -> ${poi.title}"
}

fun stringsToList(strings: Iterable<String>): String {
    if (strings.toList().isEmpty()) return ""
    return "  • " + strings.joinToString("\n  • ")
}

fun fullRoomAddress(room: BuildingRoom): String =
    "${room.location.sector.title} : ${room.location.title}(${room.location.level}) : ${room.realLocation}"

fun fullSlabAddress(slab: BuildingSlab): String =
    "${slab.sector.title} (${slab.level.toString(1)})"

fun storageNodeDescription(item: ItemsStorageNode): String =
    "${item.item.title} x${item.amount}"