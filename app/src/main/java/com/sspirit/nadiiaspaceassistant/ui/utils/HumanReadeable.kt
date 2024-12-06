package com.sspirit.nadiiaspaceassistant.ui.utils

import com.sspirit.nadiiaspaceassistant.generators.CosmonavigationTaskGenerationType
import com.sspirit.nadiiaspaceassistant.models.CosmonavigationTaskType

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