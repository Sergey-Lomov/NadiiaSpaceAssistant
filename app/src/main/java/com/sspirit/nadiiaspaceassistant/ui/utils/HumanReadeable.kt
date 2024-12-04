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