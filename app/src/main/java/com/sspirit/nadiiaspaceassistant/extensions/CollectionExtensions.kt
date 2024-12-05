package com.sspirit.nadiiaspaceassistant.extensions

interface IndexConvertibleKey {
    val index: Int
}

fun Array<Any>.getString(key: IndexConvertibleKey) : String {
    return this.elementAtOrNull(key.index)?.toString() ?: ""
}

fun Array<Any>.getFloat(key: IndexConvertibleKey, default: Float = 0f) : Float {
    return getString(key).toFloatOrNull() ?: default
}