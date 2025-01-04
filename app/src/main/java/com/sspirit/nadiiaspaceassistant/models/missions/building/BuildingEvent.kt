package com.sspirit.nadiiaspaceassistant.models.missions.building

enum class BuildingEventTrigger(val title: String) {
    FIRST_ENTRANCE("При первом входе"),
    GO_INSIDE("При углублении"),
    LOOTING("При сборе лута");

    override fun toString(): String {
        return title
    }
}

enum class BuildingEvent(val title: String, val trigger: BuildingEventTrigger) {
    CABLES_FALL("Выпадение кабелей", BuildingEventTrigger.FIRST_ENTRANCE),
    CEIL_FALL("Обвал покрытия", BuildingEventTrigger.FIRST_ENTRANCE),
    HARD_CEIL_FALL("Обвал тяжелого покрытия", BuildingEventTrigger.FIRST_ENTRANCE),
    FLOOR_FALL("Провал пола", BuildingEventTrigger.GO_INSIDE),
    DEFENSE_TURRET("Активация турелей безопасности", BuildingEventTrigger.FIRST_ENTRANCE),
    POISON_GAS("Ядовитые испарения", BuildingEventTrigger.GO_INSIDE),
    PANIC_ATTACK("Паническая атака", BuildingEventTrigger.GO_INSIDE),
    ACID_CONTAINER("Утечка кислоты (контейнер)", BuildingEventTrigger.LOOTING),
    ENGINEER_EPIPHANY("Инженерное прозрение", BuildingEventTrigger.GO_INSIDE),
    UNDEFINED("Неизвестно", BuildingEventTrigger.FIRST_ENTRANCE);

    companion object {
        fun byString(string: String): BuildingEvent {
            return BuildingEvent.entries.find { it.title == string } ?: UNDEFINED
        }
    }

    override fun toString(): String {
        return title
    }
}