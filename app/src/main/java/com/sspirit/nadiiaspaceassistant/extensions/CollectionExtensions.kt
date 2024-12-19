package com.sspirit.nadiiaspaceassistant.extensions

import com.sspirit.nadiiaspaceassistant.services.dataproviders.ItemDataProvider.dateFormatter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

interface IndexConvertibleKey {
    val index: Int
}

fun Array<Any>.getString(key: IndexConvertibleKey, default: String = "") : String {
    return elementAtOrNull(key.index)?.toString() ?: default
}

fun Array<Any>.getFloat(key: IndexConvertibleKey, default: Float = 0f) : Float {
    return getString(key).toFloatOrNull() ?: default
}

fun Array<Any>.getInt(key: IndexConvertibleKey, default: Int = 0) : Int {
    return getString(key).toIntOrNull() ?: default
}

fun Array<Any>.getBoolean(key: IndexConvertibleKey, default: Boolean) : Boolean {
    return getString(key).lowercase().toBooleanStrictOrNull() ?: default
}

fun Array<Any>.getDate(key: IndexConvertibleKey, formatter: DateTimeFormatter) : LocalDate {
    return LocalDate.parse(getString(key), formatter)
}

fun Array<Any>.getNullableString(key: IndexConvertibleKey, nullStub: String = "-") : String? {
    val string = elementAtOrNull(key.index)?.toString()
    return if (string == nullStub) null else string
}

fun Array<Any>.getSplitedString(
    key: IndexConvertibleKey,
    delimiter: String,
    trim: Boolean = true
) : Array<String> {
    val string = getString(key,"")
    var result = string.split(delimiter)

    if (trim)
        result = result.map { it.trim() }

    return result.toTypedArray()
}
