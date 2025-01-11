package com.sspirit.nadiiaspaceassistant.models.character

import com.sspirit.nadiiaspaceassistant.screens.building.TimeManager
import com.sspirit.nadiiaspaceassistant.services.CustomTimer
import com.sspirit.nadiiaspaceassistant.services.dataproviders.Completion

typealias CharacterRoutine = Array<CharacterRoutineItem>
typealias CharaterRoutinesMap = Map<CharacterSkillType, CharacterRoutine>

data class Character (
    val skills: Array<CharacterSkill>,
    val routines: CharaterRoutinesMap,
    val traits: MutableList<CharacterTrait> = mutableListOf(),
    val drugs: MutableList<Drug> = mutableListOf(),
    var drugLimits: MutableMap<DrugGroup, Int> = mutableMapOf(DrugGroup.STIMULATORS to 1)
) {
    companion object {
        fun emptyInstance() : Character {
            return Character(arrayOf(), mapOf())
        }
    }

    fun pureProgress(type: CharacterSkillType): Int {
        return skills.firstOrNull { it.type == type }?.progress ?: 0
    }

    fun progress(type: CharacterSkillType): Int {
        val traitsEffects = traits.flatMap { it.type.effects.asIterable() }
        val drugsEffects = drugs.flatMap { it.skillEffects.asIterable() }
        val pure = pureProgress(type)
        return traitsEffects.plus(drugsEffects)
            .toTypedArray()
            .affected(type, pure)
    }

    fun level(type: CharacterSkillType): Float {
        return progress(type).toFloat() / 10f
    }

    fun skill(type: CharacterSkillType) : CharacterSkill = skills.first { it.type == type }

    fun hasTraitType(type: CharacterTraitType) : Boolean =
        traits.any { it.type == type }

    fun hasTrait(trait: CharacterTrait) : Boolean =
        traits.any { it == trait }

    fun traitsBySkill(type: CharacterSkillType) : Array<CharacterTrait> = traits
        .filter { it.mayAffect(type) }
        .toTypedArray()

    fun traitsByTag(tag: CharacterTraitTag) : Array<CharacterTrait> = traits
        .filter { tag in it.type.tags }
        .toTypedArray()

    fun drugsBySkill(type: CharacterSkillType) : Array<Drug> = drugs
        .filter { it.mayAffect(type) }
        .toTypedArray()

    fun applyDrug(drug: Drug, onCompletion: Completion = null) {
        drug.overlaps.forEach { removeDrug(it) }
        removeDrug(drug)

        val limit = drugLimits[drug.group] ?: Int.MAX_VALUE
        val current = drugs.filter { it.group == drug.group }.size
        if (current >= limit) return

        if (drug.timeLimited) {
            drugs.add(drug)

            val timer = CustomTimer(drug.id, drug.title, drug.duration) {
                removeDrug(drug)
            }
            TimeManager.addCustomTimer(timer)
        }

        drug.onApply(onCompletion)
    }

    fun removeDrug(drug: Drug) {
        if (drug !in drugs) return

        drugs.remove(drug)
        if (drug.timeLimited) {
            TimeManager.removeCustomTimer(drug.id)
        }
        drug.onRemove()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Character

        if (!skills.contentEquals(other.skills)) return false
        if (routines != other.routines) return false

        return true
    }

    override fun hashCode(): Int {
        var result = skills.contentHashCode()
        result = 31 * result + routines.hashCode()
        return result
    }
}