package com.sspirit.nadiiaspaceassistant.extensions

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.jvm.internal.Ref.IntRef
import kotlin.random.Random

interface IndexConvertible {
    val index: Int
}

fun Array<Any>.getString(key: IndexConvertible, default: String = "") : String {
    return elementAtOrNull(key.index)?.toString() ?: default
}

fun Array<Any>.getString(index: Int, default: String = "") : String {
    return elementAtOrNull(index)?.toString() ?: default
}

fun Array<Any>.getFloat(key: IndexConvertible, default: Float = 0f) : Float {
    return getString(key).toFloatOrNull() ?: default
}

fun Array<Any>.getFloat(index: Int, default: Float = 0f) : Float {
    return getString(index).toFloatOrNull() ?: default
}

fun Array<Any>.getInt(key: IndexConvertible, default: Int = 0) : Int {
    return getString(key).toIntOrNull() ?: default
}

fun Array<Any>.getInt(index: Int, default: Int = 0) : Int {
    return getString(index).toIntOrNull() ?: default
}

fun Array<Any>.getBoolean(key: IndexConvertible, default: Boolean = false) : Boolean {
    return getString(key).lowercase().toBooleanStrictOrNull() ?: default
}

fun Array<Any>.getBoolean(index: Int, default: Boolean = false) : Boolean {
    return getString(index).lowercase().toBooleanStrictOrNull() ?: default
}

fun Array<Any>.getDate(key: IndexConvertible, formatter: DateTimeFormatter) : LocalDate {
    return LocalDate.parse(getString(key), formatter)
}

fun Array<Any>.getRange(key1: IndexConvertible, key2: IndexConvertible) : IntRange {
    return IntRange(getInt(key1), getInt(key2))
}

fun Array<Any>.getNullableString(key: IndexConvertible, nullStub: String = "-") : String? {
    val string = elementAtOrNull(key.index)?.toString()
    return if (string == nullStub) null else string
}

fun Array<Any>.getSplitedString(
    key: IndexConvertible,
    delimiter: String = ",",
    trim: Boolean = true
) : Array<String> {
    return getSplitedString(key.index, delimiter, trim)
}

fun Array<Any>.getSplitedString(
    index: Int,
    delimiter: String = ",",
    trim: Boolean = true
) : Array<String> {
    val string = getString(index,"")
    var result = string.split(delimiter)

    if (trim)
        result = result.map { it.trim() }

    return result.toTypedArray()
}



fun Array<Any>.readString(ref: IntRef, default: String = "") : String {
    ref.element++
    return elementAtOrNull(ref.element - 1)?.toString() ?: default
}

fun Array<Any>.readFloat(ref: IntRef, default: Float = 0f) : Float {
    ref.element++
    return getString(ref.element - 1).toFloatOrNull() ?: default
}

fun Array<Any>.readInt(ref: IntRef, default: Int = 0) : Int {
    ref.element++
    return getString(ref.element - 1).toIntOrNull() ?: default
}

fun Array<Any>.readBoolean(ref: IntRef, default: Boolean = false) : Boolean {
    ref.element++
    return getString(ref.element - 1).lowercase().toBooleanStrictOrNull() ?: default
}

fun Array<Any>.readSplitedString(ref: IntRef, delimiter: String = ",", trim: Boolean = true) : Array<String> {
    ref.element++
    return getSplitedString(ref.element - 1, delimiter, trim)
}



fun <T> Collection<T>.random(weights: Array<Float>): T {
    if (weights.size != size)
        throw Exception("Array size and weight array size should be equal")

    val random = Random.nextFloat() * weights.sum()
    var displacement = 0f
    for (i in 0 until size)
        if (random <= displacement + weights[i])
            return this.elementAt(i)
        else
            displacement += weights[i]

    throw Exception("Can't select random element with weights")
}