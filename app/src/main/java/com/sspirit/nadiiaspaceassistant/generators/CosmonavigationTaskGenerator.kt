package com.sspirit.nadiiaspaceassistant.generators

import com.sspirit.nadiiaspaceassistant.models.CosmonavigationTask
import com.sspirit.nadiiaspaceassistant.models.CosmonavigationTaskSequence
import com.sspirit.nadiiaspaceassistant.models.CosmonavigationTaskSequenceElementColor
import com.sspirit.nadiiaspaceassistant.models.CosmonavigationTaskSequenceElementForm
import com.sspirit.nadiiaspaceassistant.models.CosmonavigationTaskSequenceStep
import com.sspirit.nadiiaspaceassistant.models.CosmonavigationTaskType
import kotlin.math.round

enum class CosmonavigationTaskGenerationType {
    RANDOM,
    COLORED_GESTURES,
    GESTURES_FLOW,
    FORMS_COMPARISON,
    COLORED_FINGERS,
}

data class CosmonavigationTaskGenerationRequest (
    var type: CosmonavigationTaskGenerationType,
    var sequenceLengthMultiplier: Float,
    var stepDurationMultiplier: Float,
    var adaptiveDifficult: Float) {

    val difficult: Float
        get() = sequenceLengthMultiplier * (1 / stepDurationMultiplier)

    companion object {
        fun commonTravel(
            adaptiveDifficult: Float
        ): CosmonavigationTaskGenerationRequest {
            return CosmonavigationTaskGenerationRequest(
                type = CosmonavigationTaskGenerationType.RANDOM,
                sequenceLengthMultiplier = 1.0f,
                stepDurationMultiplier = 1.0f,
                adaptiveDifficult = adaptiveDifficult,
            )
        }

        fun randomType(
            sequenceLengthMultiplier: Float,
            stepDurationMultiplier: Float,
            adaptiveDifficult: Float
        ): CosmonavigationTaskGenerationRequest {
            return CosmonavigationTaskGenerationRequest(
                type = CosmonavigationTaskGenerationType.RANDOM,
                sequenceLengthMultiplier = sequenceLengthMultiplier,
                stepDurationMultiplier = stepDurationMultiplier,
                adaptiveDifficult = adaptiveDifficult,
            )
        }
    }
}

fun generateCosmonavigationTask(request: CosmonavigationTaskGenerationRequest) : CosmonavigationTask {
    val type = when (request.type) {
        CosmonavigationTaskGenerationType.RANDOM -> CosmonavigationTaskType.entries.toTypedArray().random()
        CosmonavigationTaskGenerationType.COLORED_GESTURES -> CosmonavigationTaskType.COLORED_GESTURES
        CosmonavigationTaskGenerationType.GESTURES_FLOW -> CosmonavigationTaskType.GESTURES_FLOW
        CosmonavigationTaskGenerationType.FORMS_COMPARISON -> CosmonavigationTaskType.FORMS_COMPARISON
        CosmonavigationTaskGenerationType.COLORED_FINGERS -> CosmonavigationTaskType.COLORED_FINGERS
    }

    val length = round(type.baseSequenceLength() * request.sequenceLengthMultiplier).toInt()
    val timeLimit = type.baseStepDuration() * request.stepDurationMultiplier * length

    val adaptiveDifficult = request.adaptiveDifficult
    val sequence = when (type) {
        CosmonavigationTaskType.COLORED_GESTURES -> coloredGesturesSequence(length, adaptiveDifficult)
        CosmonavigationTaskType.GESTURES_FLOW -> gesturesFlowSequence(length, adaptiveDifficult)
        CosmonavigationTaskType.FORMS_COMPARISON -> formsComparisonSequence(length, adaptiveDifficult)
        CosmonavigationTaskType.COLORED_FINGERS -> coloredFingersSequence(length, adaptiveDifficult)
    }

    return CosmonavigationTask(type, request.difficult, timeLimit, sequence)
}

private fun coloredGesturesSequence(length: Int, adaptiveDifficult: Float) : CosmonavigationTaskSequence {
    val colors = when {
        adaptiveDifficult < 1.0f -> randomPallet(3)
        adaptiveDifficult in 1.0f..< 3.0f -> randomPallet(4)
        adaptiveDifficult >= 3.0f -> randomPallet(5)
        else -> randomPallet(4)
    }

    val mainSteps = mutableListOf<CosmonavigationTaskSequenceStep>()
    repeat(length) {
        val availableColors = colors.clone().toMutableList()
        if (mainSteps.isNotEmpty()) {
            availableColors.remove(mainSteps.last().last().color)
        }

        val step = CosmonavigationTaskSequenceStep(
            CosmonavigationTaskSequenceElementForm.FIGURE_CIRCLE,
            availableColors.random()
        )
        mainSteps.add(step)
    }
    val mainLine = mainSteps.toTypedArray()

    val gesturesSteps = mutableListOf<CosmonavigationTaskSequenceStep>()
    val gestures = randomGestures(colors.size)
    for (i: Int in gestures.indices) {
        val step = CosmonavigationTaskSequenceStep(gestures[i], colors[i])
        gesturesSteps.add(step)
    }
    val gesturesLine = gesturesSteps.toTypedArray()

    return arrayOf(mainLine, gesturesLine)
}

private fun gesturesFlowSequence(length: Int, adaptiveDifficult: Float) : CosmonavigationTaskSequence {
    return CosmonavigationTaskSequence()
}

private fun formsComparisonSequence(length: Int, adaptiveDifficult: Float) : CosmonavigationTaskSequence {
    return CosmonavigationTaskSequence()
}

private fun coloredFingersSequence(length: Int, adaptiveDifficult: Float) : CosmonavigationTaskSequence {
    return CosmonavigationTaskSequence()
}

private fun randomPallet(size: Int) : Array<CosmonavigationTaskSequenceElementColor> {
    return CosmonavigationTaskSequenceElementColor.entries.shuffled().take(size).toTypedArray()
}

private fun randomGestures(size: Int) : Array<CosmonavigationTaskSequenceElementForm> {
    val availableGestures = listOf(
        CosmonavigationTaskSequenceElementForm.GESTURE_FIST,
        CosmonavigationTaskSequenceElementForm.GESTURE_FINGER1,
        CosmonavigationTaskSequenceElementForm.GESTURE_FINGER2,
        CosmonavigationTaskSequenceElementForm.GESTURE_FINGER3,
        CosmonavigationTaskSequenceElementForm.GESTURE_FINGER4,
    )

    return availableGestures.shuffled().take(size).toTypedArray()
}