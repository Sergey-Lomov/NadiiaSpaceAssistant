package com.sspirit.nadiiaspaceassistant.services.fabrics

import com.sspirit.nadiiaspaceassistant.models.CosmonavigationTask
import com.sspirit.nadiiaspaceassistant.models.CosmonavigationTaskSequence
import com.sspirit.nadiiaspaceassistant.models.CosmonavigationTaskSequenceElement
import com.sspirit.nadiiaspaceassistant.models.CosmonavigationTaskSequenceElementColor
import com.sspirit.nadiiaspaceassistant.models.CosmonavigationTaskSequenceElementForm
import com.sspirit.nadiiaspaceassistant.models.CosmonavigationTaskSequenceLine
import com.sspirit.nadiiaspaceassistant.models.CosmonavigationTaskSequenceStep
import com.sspirit.nadiiaspaceassistant.models.CosmonavigationTaskType
import kotlinx.serialization.Serializable
import kotlin.math.round
import kotlin.random.Random

enum class CosmonavigationTaskGenerationType {
    RANDOM,
    COLORED_GESTURES,
    GESTURES_FLOW,
    FORMS_COMPARISON,
    COLORED_FINGERS,
}

private enum class GestureFlowStyle {
    ICONS,
    POINTS,
    MIXED
}

private enum class GestureFlowStepStyle {
    ICONS,
    POINTS
}

@Serializable
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
            return randomType(
                sequenceLengthMultiplier = 1.0f,
                stepDurationMultiplier = 1.0f,
                adaptiveDifficult = adaptiveDifficult,
            )
        }

        fun randomType(
            sequenceLengthMultiplier: Float,
            stepDurationMultiplier: Float,
            adaptiveDifficult: Float,
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
        CosmonavigationTaskType.COLORED_GESTURES ->
            coloredGesturesSequence(length, adaptiveDifficult)
        CosmonavigationTaskType.GESTURES_FLOW ->
            gesturesFlowSequence(length, adaptiveDifficult)
        CosmonavigationTaskType.FORMS_COMPARISON ->
            formsComparisonSequence(length, adaptiveDifficult)
        CosmonavigationTaskType.COLORED_FINGERS ->
            coloredFingersSequence(length, adaptiveDifficult)
    }

    return CosmonavigationTask(type, request.difficult, timeLimit, sequence)
}

private fun coloredGesturesSequence(length: Int, adaptiveDifficult: Float) : CosmonavigationTaskSequence {
    val colorsPallet = when {
        adaptiveDifficult < 1.0f -> randomPallet(3)
        adaptiveDifficult in 1.0f..< 3.0f -> randomPallet(4)
        adaptiveDifficult >= 3.0f -> randomPallet(5)
        else -> randomPallet(4)
    }

    val mainSteps = mutableListOf<CosmonavigationTaskSequenceStep>()
    repeat(length) {
        val availableColors = colorsPallet.clone().toMutableList()
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
    val gestures = randomGestures(colorsPallet.size)
    for (i: Int in gestures.indices) {
        val step = CosmonavigationTaskSequenceStep(gestures[i], colorsPallet[i])
        gesturesSteps.add(step)
    }
    val gesturesLine = gesturesSteps.toTypedArray()

    return arrayOf(mainLine, gesturesLine)
}

private fun gesturesFlowSequence(length: Int, adaptiveDifficult: Float) : CosmonavigationTaskSequence {
    val gesturesPallet = when {
        adaptiveDifficult < 1.0f -> randomGestures(3)
        adaptiveDifficult in 1.0f..< 3.0f -> randomGestures(4)
        adaptiveDifficult >= 3.0f -> randomGestures(5)
        else -> randomGestures(4)
    }

    val maxPoints = when {
        adaptiveDifficult < 1.0f -> 3
        adaptiveDifficult in 1.0f..< 2.0f -> 4
        adaptiveDifficult in 2.0f..< 3.0f -> 3
        adaptiveDifficult >= 3.0f -> 4
        else -> 3
    }

    val style = when {
        adaptiveDifficult < 2.0f -> arrayOf(GestureFlowStyle.POINTS, GestureFlowStyle.ICONS).random()
        else -> GestureFlowStyle.MIXED
    }

    val line1 = gestureFlowLine(length, style, gesturesPallet, maxPoints)
    val line2 = gestureFlowLine(length, style, gesturesPallet, maxPoints)

    return arrayOf(line1, line2)
}

private fun gestureFlowLine(
    length: Int,
    style: GestureFlowStyle,
    gestures: Array<CosmonavigationTaskSequenceElementForm>,
    maxPoints: Int
) : CosmonavigationTaskSequenceLine {
    val line = mutableListOf<CosmonavigationTaskSequenceStep>()

    var previousForm: CosmonavigationTaskSequenceElementForm? = null
    var previousPoints: Int? = null
    repeat(length) {
        val availableForms = gestures.filter { it !== previousForm }
        val availablePoints = (1..maxPoints).filter { it != previousPoints }
        val stepStyle = when (style) {
            GestureFlowStyle.ICONS -> GestureFlowStepStyle.ICONS
            GestureFlowStyle.POINTS -> GestureFlowStepStyle.POINTS
            GestureFlowStyle.MIXED -> arrayOf(
                GestureFlowStepStyle.POINTS,
                GestureFlowStepStyle.ICONS
            ).random()
        }

        val step = when(stepStyle) {
            GestureFlowStepStyle.ICONS -> {
                val form = availableForms.random()
                val color = CosmonavigationTaskSequenceElementColor.entries.random()
                previousPoints = null
                previousForm = form
                CosmonavigationTaskSequenceStep(form, color)
            }

            GestureFlowStepStyle.POINTS -> {
                val count = availablePoints.random()
                previousPoints = count
                previousForm = null
                val points = mutableListOf<CosmonavigationTaskSequenceElement>()
                repeat(count) {
                    val color = CosmonavigationTaskSequenceElementColor.entries.random()
                    val element = CosmonavigationTaskSequenceElement(
                        CosmonavigationTaskSequenceElementForm.FIGURE_CIRCLE,
                        color
                    )
                    points.add(element)
                }
                points.toTypedArray()
            }
        }

        line.add(step)
    }

    return line.toTypedArray()
}

private fun formsComparisonSequence(length: Int, adaptiveDifficult: Float) : CosmonavigationTaskSequence {
    val figuresPallet = when {
        adaptiveDifficult < 1.0f -> randomFigures(3)
        adaptiveDifficult in 1.0f..< 2.0f -> randomFigures(3)
        adaptiveDifficult in 2.0f..< 3.0f -> randomFigures(4)
        adaptiveDifficult >= 3.0f -> randomFigures(5)
        else -> randomFigures(4)
    }

    val colorsPallet = when {
        adaptiveDifficult < 1.0f -> randomPallet(1)
        adaptiveDifficult in 1.0f..< 2.0f -> randomPallet(2)
        adaptiveDifficult in 2.0f..< 3.0f -> randomPallet(3)
        adaptiveDifficult >= 3.0f -> randomPallet(4)
        else -> randomPallet(4)
    }

    val baseMatchChance = 0.4
    val matchChanceProgression = 0.2
    var matchChance = baseMatchChance

    val line1 = mutableListOf<CosmonavigationTaskSequenceStep>()
    val line2 = mutableListOf<CosmonavigationTaskSequenceStep>()
    repeat(length) {
        val previous1 = line1.lastOrNull()?.first()?.form
        val previous2 = line2.lastOrNull()?.first()?.form
        val available1 = figuresPallet.filter { it != previous1 }
        var available2 = figuresPallet.filter { it != previous2 }

        val match = Random.nextFloat() <= matchChance
        if (match) {
            matchChance = baseMatchChance
        } else {
            matchChance += matchChanceProgression
        }

        val form1 = available1.random()
        val step1 = CosmonavigationTaskSequenceStep(form1, colorsPallet.random())
        line1.add(step1)

        val step2 = if (match)
            CosmonavigationTaskSequenceStep(form1, colorsPallet.random())
        else {
            available2 = available2.filter { it != form1 }
            CosmonavigationTaskSequenceStep(available2.random(), colorsPallet.random())
        }

        line2.add(step2)
    }

    return arrayOf(line1.toTypedArray(), line2.toTypedArray())
}

private fun coloredFingersSequence(length: Int, adaptiveDifficult: Float) : CosmonavigationTaskSequence {
    val colorsPallet = when {
        adaptiveDifficult < 1.0f -> randomPallet(3)
        adaptiveDifficult in 1.0f..< 2.0f -> randomPallet(4)
        adaptiveDifficult in 2.0f..< 3.0f -> randomPallet(3)
        adaptiveDifficult >= 3.0f -> randomPallet(4)
        else -> randomPallet(4)
    }

    val maxPoints = if (adaptiveDifficult < 2) 1 else 2
    val line1 = coloredFingersLine(length, colorsPallet, maxPoints)
    val line2 = coloredFingersLine(length, colorsPallet, maxPoints)
    return arrayOf(line1, line2)
}

private fun coloredFingersLine(
    length: Int,
    colorsPallet: Array<CosmonavigationTaskSequenceElementColor>,
    maxPoints: Int,
) : CosmonavigationTaskSequenceLine {
    val line = mutableListOf<CosmonavigationTaskSequenceStep>()
    var previousColor: CosmonavigationTaskSequenceElementColor? = null

    repeat(length) {
        val colors = colorsPallet.filter { it != previousColor }
        val points = (1..maxPoints).random()

        val step = mutableListOf<CosmonavigationTaskSequenceElement>()
        repeat(points) {
            val color = colors.random()
            val element = CosmonavigationTaskSequenceElement(
                CosmonavigationTaskSequenceElementForm.FIGURE_CIRCLE,
                color
            )
            step.add(element)

            previousColor = if (points == 1) color else null
        }

        line.add(step.toTypedArray())
    }

    return line.toTypedArray()
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

private fun randomFigures(size: Int) : Array<CosmonavigationTaskSequenceElementForm> {
    val availableGestures = listOf(
        CosmonavigationTaskSequenceElementForm.FIGURE_CIRCLE,
        CosmonavigationTaskSequenceElementForm.FIGURE_STAR,
        CosmonavigationTaskSequenceElementForm.FIGURE_TRIANGLE,
        CosmonavigationTaskSequenceElementForm.FIGURE_SQUARE,
        CosmonavigationTaskSequenceElementForm.FIGURE_PENTAGON,
    )

    return availableGestures.shuffled().take(size).toTypedArray()
}