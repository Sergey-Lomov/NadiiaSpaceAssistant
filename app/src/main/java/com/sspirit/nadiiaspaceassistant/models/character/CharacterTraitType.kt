package com.sspirit.nadiiaspaceassistant.models.character

import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingDoorHackingLevel
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingDoorHackingLevel.UNDEFINED

private typealias Effect = CharacterSkillEffect
private typealias Tag = CharacterTraitTag

enum class CharacterTraitTag(val title: String) {
    ACHIEVEMENT("Достижение"),
    MALAISE("Недуг"),
    TRAUMA("Травма"),
}

enum class CharacterTraitType(
    val title: String,
    val description: String,
    val effects: Array<Effect> = arrayOf(),
    val tags: Array<Tag>
) {
    UNDEFINED(
        title = "Неопределено",
        description = "Произошел сбой. Черта не определена.",
        tags = arrayOf()
    ),

    ABSOLUTE_PHYSIOLOGY(
        title = "Безупречная физиология",
        description = "Вы достигли максимального развития физиологии",
        tags = arrayOf(Tag.ACHIEVEMENT)
    ),

    ABSOLUTE_MENTALITY(
        title = "Безупречная ментальность",
        description = "Вы достигли максимального ментального здоровья",
        tags = arrayOf(Tag.ACHIEVEMENT)
    ),

    ABSOLUTE_NUTRITION(
        title = "Безупречная питание",
        description = "Вы достигли вершин правильного питания",
        tags = arrayOf(Tag.ACHIEVEMENT)
    ),

    NAUSEA(
        title = "Тошнота",
        description = "Вас мутит, это отвлекает и мешает контролировать тело.",
        effects = arrayOf(Effect.agility(-5)),
        tags = arrayOf(Tag.MALAISE)
    ),

    WEAKNESS(
        title = "Вялость",
        description = "Вы обессилены, даже просто шевелить конечностями и то тяжело.",
        effects = arrayOf(Effect.power(-5)),
        tags = arrayOf(CharacterTraitTag.MALAISE)
    ),

    MIGRAINE(
        title = "Мигрень",
        description = "У вас раскалывается голова, сложно сосредоточиться на чем-нибудь.",
        effects = arrayOf(Effect.intelligent(-5)),
        tags = arrayOf(CharacterTraitTag.MALAISE)
    ),

    LEG_INJURY(
        title = "Ушиб ноги",
        description = "Нога здорово болит, лучше воздержаться от прыжков",
        effects = arrayOf(Effect.agility(-5)),
        tags = arrayOf(CharacterTraitTag.TRAUMA)
    ),

    HEAD_GASH (
        title = "Разбитая голова",
        description = "В голове все плывет, лучше даже не смотреть на сложные схемы.",
        effects = arrayOf(Effect.intelligent(-5)),
        tags = arrayOf(CharacterTraitTag.TRAUMA)
    ),

    ACID_BURN_ARM (
        title = "Кислотный ожог руки",
        description = "Прикосновение к чему-либо отзывается резкой боллью. Открывать слишком тугие двери и двигать слишком тяжелые предметы не получиться.",
        effects = arrayOf(Effect.power(-5)),
        tags = arrayOf(CharacterTraitTag.TRAUMA)
    ),

    MILD_INTOXICATION (
        title = "Легкая интоксикация",
        description = "Все тело пропитано отравой. Все органы и системы работает хуже чем обычно.",
        effects = arrayOf(Effect.power(-4), Effect.agility(-4), Effect.intelligent(-4) ),
        tags = arrayOf(CharacterTraitTag.MALAISE)
    );

    companion object {
        fun byString(string: String): CharacterTraitType {
            return CharacterTraitType.entries.find { it.title == string } ?: UNDEFINED
        }
    }
}