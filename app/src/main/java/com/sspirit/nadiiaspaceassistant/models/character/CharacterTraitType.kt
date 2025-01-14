package com.sspirit.nadiiaspaceassistant.models.character

private typealias Effect = CharacterSkillEffect
private typealias Tag = CharacterTraitTag

enum class CharacterTraitTag(val title: String) {
    ACHIEVEMENT("Достижение"),
    MALAISE("Недуг"),
    TRAUMA("Травма");

    override fun toString(): String = title
}

enum class CharacterTraitType(
    val title: String,
    val info: String,
    val effectInfo: String,
    val effects: Array<Effect> = arrayOf(),
    val tags: Array<Tag>,
    val limit: Int = 1
) {
    UNDEFINED(
        title = "Неопределено",
        info = "Произошел сбой. Черта не определена.",
        effectInfo = "Эффекта нет",
        tags = arrayOf()
    ),

    ABSOLUTE_PHYSIOLOGY(
        title = "Безупречная физиология",
        info = "Вы достигли максимального развития физиологии",
        effectInfo = "Сбрасывается вместо потери развития физиологии",
        tags = arrayOf(Tag.ACHIEVEMENT)
    ),

    ABSOLUTE_MENTALITY(
        title = "Безупречная ментальность",
        info = "Вы достигли максимального ментального здоровья",
        effectInfo = "Сбрасывается вместо потери развития ментального здоровья",
        tags = arrayOf(Tag.ACHIEVEMENT)
    ),

    ABSOLUTE_NUTRITION(
        title = "Безупречное питание",
        info = "Вы достигли вершин правильного питания",
        effectInfo = "Сбрасывается вместо потери развития правильного питания",
        tags = arrayOf(Tag.ACHIEVEMENT)
    ),

    LETHARGIC(
        title = "Вялость",
        info = "Все тело отзывается с неохотой. Сил как-будто бы не стало меньше, но точность и скорость движений явно снизились.",
        effectInfo = "Ловкость -5",
        effects = arrayOf(Effect.agility(-5)),
        tags = arrayOf(Tag.MALAISE)
    ),

    WEAKNESS(
        title = "Слабость",
        info = "Вы обессилены, даже просто шевелить конечностями и то тяжело.",
        effectInfo = "Сила -5",
        effects = arrayOf(Effect.power(-5)),
        tags = arrayOf(CharacterTraitTag.MALAISE)
    ),

    MIGRAINE(
        title = "Мигрень",
        info = "У вас раскалывается голова, сложно сосредоточиться на чем-нибудь.",
        effectInfo = "Разум -5",
        effects = arrayOf(Effect.intelligent(-5)),
        tags = arrayOf(CharacterTraitTag.MALAISE)
    ),

    LEG_INJURY(
        title = "Ушиб ноги",
        info = "Нога здорово болит, лучше воздержаться от прыжков",
        effectInfo = "Ловкость -5. Запрещено быстро спрыгивать в дыры в полу",
        effects = arrayOf(Effect.agility(-5)),
        tags = arrayOf(CharacterTraitTag.TRAUMA),
        limit = 2
    ),

    HEAD_GASH (
        title = "Разбитая голова",
        info = "В голове все плывет, лучше даже не смотреть на сложные схемы.",
        effectInfo = "Разум -5. Запрещено взламывать сложные и средние замки",
        effects = arrayOf(Effect.intelligent(-5)),
        tags = arrayOf(CharacterTraitTag.TRAUMA)
    ),

    ARM_ACID_BURN (
        title = "Кислотный ожог руки",
        info = "Прикосновение к чему-либо отзывается резкой боллью. Открывать слишком тугие двери и двигать слишком тяжелые предметы не получиться.",
        effectInfo = "Сила -5. Запрещено открывать двери с неисправным механизмом. Запрещено двигать крупные предметы размера 2 и 3",
        effects = arrayOf(Effect.power(-5)),
        tags = arrayOf(CharacterTraitTag.TRAUMA),
        limit = 2
    ),

    MILD_INTOXICATION (
        title = "Легкая интоксикация",
        info = "Все тело пропитано отравой. Все органы и системы работает хуже чем обычно.",
        effectInfo = "Разум -4, Ловкость -4, Сила -4",
        effects = arrayOf(Effect.power(-4), Effect.agility(-4), Effect.intelligent(-4) ),
        tags = arrayOf(CharacterTraitTag.MALAISE),
        limit = Int.MAX_VALUE
    );

    companion object {
        fun byString(string: String): CharacterTraitType {
            return CharacterTraitType.entries.find { it.title == string } ?: UNDEFINED
        }
    }

    fun effectOn(skill: CharacterSkillType) : Int =
        effects.effectOn(skill)
}