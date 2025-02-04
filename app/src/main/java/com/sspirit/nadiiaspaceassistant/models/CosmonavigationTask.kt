package com.sspirit.nadiiaspaceassistant.models

import kotlinx.serialization.Serializable

private const val ALWAYS_ALLOW_DIFFICULT = 1.5
private const val ALWAYS_ALLOW_SKILL = 3.0
private const val DIFFICULT_ALLOWING_GAP = 0.5

enum class CosmonavigationTaskType {

    COLORED_GESTURES {
        override fun baseSequenceLength() = 10
        override fun baseStepDuration() = 3.0f
    },

    GESTURES_FLOW {
        override fun baseSequenceLength() = 10
        override fun baseStepDuration() = 2.5f
    },

    FORMS_COMPARISON {
        override fun baseSequenceLength() = 10
        override fun baseStepDuration() = 2.5f
    },

    COLORED_FINGERS   {
        override fun baseSequenceLength() = 8
        override fun baseStepDuration() = 4.0f
    };

    abstract fun baseSequenceLength(): Int
    abstract fun baseStepDuration(): Float
}

enum class CosmonavigationTaskSequenceElementForm {
    FIGURE_CIRCLE,
    FIGURE_TRIANGLE,
    FIGURE_SQUARE,
    FIGURE_PENTAGON,
    FIGURE_STAR,

    GESTURE_FIST,
    GESTURE_FINGER1,
    GESTURE_FINGER2,
    GESTURE_FINGER3,
    GESTURE_FINGER4,
}

enum class CosmonavigationTaskSequenceElementColor {
    YELLOW,
    MAGENTA,
    ORANGE,
    GREEN,
    BLUE
}

@Serializable
data class CosmonavigationTaskSequenceElement(
    val form: CosmonavigationTaskSequenceElementForm,
    val color: CosmonavigationTaskSequenceElementColor
)

typealias CosmonavigationTaskSequenceStep = Array<CosmonavigationTaskSequenceElement>
typealias CosmonavigationTaskSequenceLine = Array<CosmonavigationTaskSequenceStep>
typealias CosmonavigationTaskSequence = Array<CosmonavigationTaskSequenceLine>

fun CosmonavigationTaskSequenceStep(
    form: CosmonavigationTaskSequenceElementForm,
    color: CosmonavigationTaskSequenceElementColor,
) : CosmonavigationTaskSequenceStep {
    return arrayOf(CosmonavigationTaskSequenceElement(form, color))
}

fun CosmonavigationTaskSequenceStep() : CosmonavigationTaskSequenceStep {
    return arrayOf()
}

fun CosmonavigationTaskSequenceLine() : CosmonavigationTaskSequenceLine {
    return arrayOf()
}

fun CosmonavigationTaskSequence() : CosmonavigationTaskSequence {
    return arrayOf()
}

fun CosmonavigationTaskSequence.getLength() : Int {
    if (size == 0) { return 0  }
    return map { it.size }.max()
}

@Serializable
data class CosmonavigationTask (
    val type: CosmonavigationTaskType,
    val difficult: Float,
    val timeLimit: Float,
    val sequence: CosmonavigationTaskSequence
) {
    companion object {
        fun difficult(length: Float, time: Float): Float = length * (1 / time)

        fun isAllowedForSkill(skillLevel: Float, length: Float, time: Float): Boolean {
            val difficult = difficult(length, time)
            if (difficult <= ALWAYS_ALLOW_DIFFICULT) return true
            if (skillLevel >= ALWAYS_ALLOW_SKILL) return true
            return (difficult * 10) <= skillLevel + DIFFICULT_ALLOWING_GAP
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CosmonavigationTask

        if (type != other.type) return false
        if (difficult != other.difficult) return false
        if (timeLimit != other.timeLimit) return false
        if (!sequence.contentEquals(other.sequence)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + difficult.hashCode()
        result = 31 * result + timeLimit.hashCode()
        result = 31 * result + sequence.contentHashCode()
        return result
    }
}