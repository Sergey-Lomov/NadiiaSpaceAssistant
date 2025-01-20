package com.sspirit.nadiiaspaceassistant.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.jvm.internal.Ref.IntRef

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

fun Array<Any>.getDate(
    key: IndexConvertible,
    formatter: DateTimeFormatter = localDateFormatter
) : LocalDate {
    return LocalDate.parse(getString(key), formatter)
}

fun Array<Any>.getRange(key1: IndexConvertible, key2: IndexConvertible) : IntRange {
    return IntRange(getInt(key1), getInt(key2))
}

fun Array<Any>.getNullableString(key: IndexConvertible, nullStub: String = "-") : String? {
    val string = elementAtOrNull(key.index)?.toString()
    return if (string == nullStub) null else string
}

fun Array<Any>.getSplitString(
    key: IndexConvertible,
    delimiter: String = ",",
    trim: Boolean = true
) : Array<String> {
    return getSplitString(key.index, delimiter, trim)
}

fun Array<Any>.getSplitString(
    index: Int,
    delimiter: String = ",",
    trim: Boolean = true
) : Array<String> {
    val string = getString(index,"")
    if (string.isEmpty()) return arrayOf()

    var result = string.split(delimiter)

    if (trim)
        result = result.map { it.trim() }

    return result.toTypedArray()
}



fun Array<Any>.ignore(ref: IntRef, amount: Int) : Array<Any> {
    ref.element += amount
    return this
}

fun Array<Any>.readString(ref: IntRef, default: String = "") : String {
    ref.element++
    return elementAtOrNull(ref.element - 1)?.toString() ?: default
}

fun Array<Any>.readFloat(ref: IntRef, default: Float = 0f) : Float {
    return readString(ref).toFloatOrNull() ?: default
}

fun Array<Any>.readInt(ref: IntRef, default: Int = 0) : Int {
    return readString(ref).toIntOrNull() ?: default
}

fun Array<Any>.readBoolean(ref: IntRef, default: Boolean = false) : Boolean {
    return readString(ref).lowercase().toBooleanStrictOrNull() ?: default
}

fun Array<Any>.readDate(
    ref: IntRef,
    formatter: DateTimeFormatter = localDateFormatter
) : LocalDate {
    return LocalDate.parse(readString(ref), formatter)
}

fun Array<Any>.readOptionalDate(
    ref: IntRef,
    formatter: DateTimeFormatter = localDateFormatter
) : LocalDate? {
    return runCatching { readDate(ref, formatter) }.getOrNull()
}

fun Array<Any>.readSplitString(ref: IntRef, delimiter: String = ",", trim: Boolean = true) : Array<String> {
    ref.element++
    return getSplitString(ref.element - 1, delimiter, trim)
}

inline fun <T, R> Array<T>.flatArrayMap(transform: (T) -> Array<R>): List<R> =
    flatMap { transform(it).asIterable() }