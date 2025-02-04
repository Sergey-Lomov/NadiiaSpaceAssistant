package com.sspirit.nadiiaspaceassistant.ui.utils

import com.sspirit.nadiiaspaceassistant.utils.toString
import com.sspirit.nadiiaspaceassistant.services.fabrics.CosmonavigationTaskGenerationType
import com.sspirit.nadiiaspaceassistant.models.CosmonavigationTaskType
import com.sspirit.nadiiaspaceassistant.models.ItemsStorageNode
import com.sspirit.nadiiaspaceassistant.models.cosmology.SpacePOI
import com.sspirit.nadiiaspaceassistant.models.cosmology.SpacePOIAccessStatus
import com.sspirit.nadiiaspaceassistant.models.missions.MissionType
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingLocation
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingRoom
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingSlab
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingWall
import com.sspirit.nadiiaspaceassistant.screens.cosmology.SpacePOILandingStatus

fun humanReadable(bool: Boolean): String = if (bool) "Да" else "Нет"

fun humanReadable(status: SpacePOIAccessStatus): String =
    when (status) {
        SpacePOIAccessStatus.AVAILABLE -> "Доступно для посещения"
        SpacePOIAccessStatus.RESTRICTED -> "Посещение ограничено"
        SpacePOIAccessStatus.HIDDEN -> "Точка интереса скрыта"
        SpacePOIAccessStatus.UNDEFINED -> "Не валидно"
    }


fun humanReadable(status: SpacePOILandingStatus): String =
    when (status) {
        SpacePOILandingStatus.UNAVAIABLE -> "Посадка невозможна"
        SpacePOILandingStatus.UNALLOWED -> "Посадка слишком сложна"
        SpacePOILandingStatus.ALLOWED -> "Посадка возможна"
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

    return if (withValue) "$string ($value)" else string
}

fun humanTime(seconds: Int, showEmpty: Boolean = false) : String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60

    return buildString {
        if (showEmpty) {
            append("${hours.toString(2)}:")
            append("${minutes.toString(2)}:")
            append(secs.toString(2))
        } else {
            if (hours > 0) append("$hours:")
            if (minutes > 0) append("$minutes:")
            append("$secs")
        }
    }.trim()
}

fun humanReadableRoute(poi: SpacePOI): String {
    return "${poi.parent.parent.title}(${poi.parent.parent.id}) -> ${poi.parent.title} -> ${poi.title}"
}

fun stringsToList(strings: Iterable<String>): String {
    if (strings.toList().isEmpty()) return ""
    return "  • " + strings.joinToString("\n  • ")
}

fun fullAddress(location: BuildingLocation): String =
    "${location.sector.title} : ${location.title}(${location.level})"


fun fullAddress(room: BuildingRoom): String =
    "${room.location.sector.title} : ${room.location.title}(${room.location.level}) : ${room.realLocation}"

fun fullAddress(slab: BuildingSlab): String =
    "${slab.sector.title} (${slab.level.toString(1)}) : ${slab.realLocation}"

fun fullAddress(wall: BuildingWall): String =
    "${fullAddress(wall.location)} : ${wall.room1.realLocation}-${wall.room2.realLocation}"

fun storageNodeDescription(item: ItemsStorageNode): String =
    "${item.item.title} x${item.amount}"