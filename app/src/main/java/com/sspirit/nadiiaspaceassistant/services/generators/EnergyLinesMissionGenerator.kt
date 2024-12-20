package com.sspirit.nadiiaspaceassistant.services.generators

import android.util.Log
import com.sspirit.nadiiaspaceassistant.models.missions.EnergyLines
import java.time.LocalDate
import java.util.UUID

data class EnergyLineRule(
    val lAttribute: Int,
    val rAttribute: Int,
    val lValue: Int,
    val rValue: Int,
    val positive: Boolean
) {
    val left: Pair<Int, Int>
        get() = Pair(lAttribute, lValue)

    val right: Pair<Int, Int>
        get() = Pair(rAttribute, rValue)
}

fun generateEnergyLinesMissionStub(): EnergyLines {
    return EnergyLines(
        id = UUID.randomUUID().toString(),
        client = "Demo client",
        reward = 0,
        difficult = 0.0f,
        expiration = LocalDate.now().plusDays(1),
        requirements = "",
        place = "Demo place",
        landingTimeMult = 1.0f,
        landingLengthMult = 1.0f,
        rules = arrayOf(""),
        values = arrayOf(""),
        landingInfo = "",
        hardPlaces = false,
        light = true
    )
}

fun generateEnergyLinesMission() {
    val sets = mutableListOf<Array<Int>>()
    val attributesCount = 3
    val setsCount = 3
    val valuesRange = 1..setsCount

    for (setIndex in 0 until setsCount) {
        val set = mutableListOf<Int>()
        for (attributeIndex in 0 until attributesCount) {
            val used = sets.map { it[attributeIndex] }.toSet()
            val availableRange = valuesRange.minus(used)
            set.add(availableRange.random())
        }
        sets.add(set.toTypedArray())
    }

    val rules = mutableListOf<EnergyLineRule>()
    for (setIndex in 0 until sets.size)
        for (attrIndex in 0 until attributesCount) {
            for (subAttrIndex in attrIndex + 1 until attributesCount)
                for (subSetIndex in 0 until sets.size) {
                    val rule = EnergyLineRule(
                        lAttribute = attrIndex,
                        rAttribute = subAttrIndex,
                        lValue = sets[setIndex][attrIndex],
                        rValue = sets[subSetIndex][subAttrIndex],
                        positive = subSetIndex == setIndex
                    )
                    rules.add(rule)
                }
        }

    var compressing =  true
    while (compressing) {
        compressing = false
        val randomized = rules.shuffled()
        for (target in randomized) {
            val another = randomized.minus(target)
            if (isReproducible(target, another, valuesRange)) {
                rules.remove(target)
                compressing = true
                break
            }
        }
    }

    for (rule in rules) {
        Log.d("Rules Test", "${rule.lAttribute}:${rule.lValue} ${if (rule.positive) "-->" else "-X>"} ${rule.rAttribute}:${rule.rValue}")
    }
    Log.d("tag","Kek")
}

private fun isReproducible(target: EnergyLineRule, another: List<EnergyLineRule>, valuesRange: IntRange): Boolean {
    if (target.positive) {
        val byNegative = reproducePositiveByNegatives(target, another, valuesRange)
        val byTransitivity= reproducePositiveByTransitivity(target, another)
        return byTransitivity || byNegative
    } else {
        return reproduceNegativesByPositive(target, another)
    }
}

private fun reproduceNegativesByPositive(target: EnergyLineRule, another: List<EnergyLineRule>): Boolean {
    for (rule in another) {
        if (rule.left == target.left && rule.rAttribute == target.rAttribute && rule.positive)
            return true
        if (rule.right == target.right && rule.lAttribute == target.lAttribute && rule.positive)
            return true
    }
    return false
}

private fun reproducePositiveByNegatives(
    target: EnergyLineRule,
    another: List<EnergyLineRule>,
    valuesRange: IntRange
): Boolean {
    val rUnhandled = valuesRange.minus(target.rValue).toMutableList()
    val lUnhandled = valuesRange.minus(target.lValue).toMutableList()
    for (rule in another) {
        if (rule.left == target.left
            && rule.rAttribute == target.rAttribute
            && !rule.positive)
            rUnhandled.remove(rule.rValue)
        else if (rule.right == target.right
            && rule.lAttribute == target.lAttribute
            && !rule.positive)
            lUnhandled.remove(rule.lValue)
    }

    return rUnhandled.isEmpty() || lUnhandled.isEmpty()
}

private fun reproducePositiveByTransitivity(target: EnergyLineRule, another: List<EnergyLineRule>): Boolean {
    val achievable = mutableListOf<Pair<Int, Int>>()
    achievable.add(target.left)

    var growth = true
    while (growth) {
        growth = false
        for (rule in another) {
            if (!rule.positive) continue
            if (rule.left in achievable && rule.right !in achievable) {
                achievable.add(rule.right)
                growth = true
            } else if (rule.right in achievable && rule.left !in achievable) {
                achievable.add(rule.left)
                growth = true
            }
        }
    }

    return target.right in achievable
}