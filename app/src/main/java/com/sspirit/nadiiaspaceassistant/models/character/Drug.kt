package com.sspirit.nadiiaspaceassistant.models.character

import android.graphics.Color
import com.sspirit.nadiiaspaceassistant.screens.building.TimeManager
import com.sspirit.nadiiaspaceassistant.services.dataproviders.CharacterDataProvider
import com.sspirit.nadiiaspaceassistant.services.dataproviders.Completion
import com.sspirit.nadiiaspaceassistant.utils.simpleCoroutineLaunch

enum class DrugGroup {
    STIMULATORS,
}

open class Drug(
    val id: String,
    val title: String,
    val description: String,
    val color: Color,
    val skillEffects: Array<CharacterSkillEffect> = arrayOf(),
    val overlaps: Array<Drug> = arrayOf(),
    val duration: Int = 0,
    val group: DrugGroup? = null
) {
    open fun onApply(onCompletion: Completion = null) { }
    open fun onRemove() { }

    fun mayAffect(skill: CharacterSkillType): Boolean =
        skillEffects.mayAffect(skill)

    fun effectOn(skill: CharacterSkillType): Int =
        skillEffects.effectOn(skill)

    companion object {
        val all = arrayOf(
            Agistim,
            AgistimPlus,
            Intelstim,
            IntelstimPlus,
            Powerstim,
            PowerstimPlus,
            Regeneron,
            Neurolaps,
            Clearifin,
            Longitron
        )
    }

    val timeLimited: Boolean
        get() = duration > 0

    data object Agistim: Drug(
        id = "Agistim",
        title = "Аджистим",
        description = "Увеличивает ловкость на 5",
        color = Color.valueOf(0x079681),
        skillEffects = arrayOf( CharacterSkillEffect.agility(5) ),
        duration = 300,
        group = DrugGroup.STIMULATORS
    )

    data object AgistimPlus: Drug(
        id = "AgistimPlus",
        title = "Аджистим+",
        description = "Увеличивает ловкость на 9",
        color = Color.valueOf(0x079681),
        skillEffects = arrayOf( CharacterSkillEffect.agility(9) ),
        overlaps = arrayOf( Agistim ),
        duration = 300,
        group = DrugGroup.STIMULATORS
    )

    data object Intelstim: Drug(
        id = "Intelstim",
        title = "Интелстим",
        description = "Увеличивает разум на 5",
        color = Color.valueOf(0x13448E),
        skillEffects = arrayOf( CharacterSkillEffect.intelligent(5) ),
        duration = 300,
        group = DrugGroup.STIMULATORS
    )

    data object IntelstimPlus: Drug(
        id = "IntelstimPlus",
        title = "Интелстим+",
        description = "Увеличивает разум на 9",
        color = Color.valueOf(0x13448E),
        skillEffects = arrayOf( CharacterSkillEffect.intelligent(9) ),
        overlaps = arrayOf( Intelstim ),
        duration = 300,
        group = DrugGroup.STIMULATORS
    )

    data object Powerstim: Drug(
        id = "Powerstim",
        title = "Поверстим",
        description = "Увеличивает силу на 5",
        color = Color.valueOf(0xAF7F1E),
        skillEffects = arrayOf( CharacterSkillEffect.power(5) ),
        duration = 300,
        group = DrugGroup.STIMULATORS
    )

    data object PowerstimPlus: Drug(
        id = "PowerstimPlus",
        title = "Поверстим+",
        description = "Увеличивает силу на 9",
        color = Color.valueOf(0xAF7F1E),
        skillEffects = arrayOf( CharacterSkillEffect.power(9) ),
        overlaps = arrayOf( Powerstim ),
        duration = 300,
        group = DrugGroup.STIMULATORS
    )

    data object Neurolaps: Drug(
        id = "Neurolaps",
        title = "Нейролапс",
        description = "Позволяет применять два препарата одновременно",
        color = Color.valueOf(0x2B2929),
        duration = 30
    ) {
        override fun onApply(onCompletion: Completion) {
            val limits = CharacterDataProvider.character.drugLimits
            val limit = limits[DrugGroup.STIMULATORS] ?: 1
            limits[DrugGroup.STIMULATORS] = limit + 1
            onCompletion?.invoke(true)
        }

        override fun onRemove() {
            val limits = CharacterDataProvider.character.drugLimits
            val limit = limits[DrugGroup.STIMULATORS] ?: 2
            limits[DrugGroup.STIMULATORS] = limit - 1
        }
    }

    data object Clearifin: Drug(
        id = "Clearifin",
        title = "Кларифин",
        description = "Очищяет кровь, мгновенно выводя из организма все препараты",
        color = Color.valueOf(0xFFFCF5),
        overlaps = arrayOf( Powerstim, PowerstimPlus, Intelstim, IntelstimPlus, Agistim, AgistimPlus ),
    )

    data object Regeneron: Drug(
        id = "Regeneron",
        title = "Регенерон",
        description = "Исцеляет одну случайную травму",
        color = Color.valueOf(0x009640),
    ) {
        override fun onApply(onCompletion: Completion) {
            val traumas = CharacterDataProvider.character.traitsByTag(CharacterTraitTag.TRAUMA)
            val trauma = traumas.randomOrNull() ?: return
            simpleCoroutineLaunch {
                CharacterDataProvider.removeTrait(trauma) {
                    onCompletion?.invoke(it)
                }
            }
        }
    }

    data object Longitron: Drug(
        id = "Longitron",
        title = "Лонгитрон",
        description = "Удваивает время действия уже принятых препаратов",
        color = Color.valueOf(0x3AAA35),
    ) {
        override fun onApply(onCompletion: Completion) {
            CharacterDataProvider.character.drugs.forEach {
                val timer = TimeManager.getCustomTimer(it.id) ?: return@forEach
                timer.timeLeft.value *= 2
            }
            onCompletion?.invoke(true)
        }
    }
}