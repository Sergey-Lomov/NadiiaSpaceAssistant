package com.sspirit.nadiiaspaceassistant.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.jvm.internal.Ref.IntRef
import kotlin.random.Random

fun <T> Collection<T>.random(weights: Array<Float>): T {
    if (weights.size != size)
        throw Exception("Array size and weight array size should be equal")

    val random = Random.nextFloat() * weights.sum()
    var displacement = 0f
    for (i in indices)
        if (random <= displacement + weights[i])
            return this.elementAt(i)
        else
            displacement += weights[i]

    throw Exception("Can't select random element with weights")
}

inline fun <T, R> Iterable<T>.flatArrayMap(transform: (T) -> Array<R>): List<R> =
    flatMap { transform(it).asIterable() }