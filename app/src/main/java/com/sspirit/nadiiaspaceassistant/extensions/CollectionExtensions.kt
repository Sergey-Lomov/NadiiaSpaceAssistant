package com.sspirit.nadiiaspaceassistant.extensions

interface IndexConvertibleKey {
    val index: Int
}

fun Array<Any>.getString(key: IndexConvertibleKey, default: String = "") : String {
    return this.elementAtOrNull(key.index)?.toString() ?: default
}

fun Array<Any>.getFloat(key: IndexConvertibleKey, default: Float = 0f) : Float {
    return getString(key).toFloatOrNull() ?: default
}

fun Array<Any>.getInt(key: IndexConvertibleKey, default: Int = 0) : Int {
    return getString(key).toIntOrNull() ?: default
}
