package com.sspirit.nadiiaspaceassistant.models.cosmology

import com.sspirit.nadiiaspaceassistant.utils.IdEquatableEntity

enum class SpacePOIOffice(val string: String) {
    ALLIANCE("Представительство Альянса"),
    VERSEMINING("Офис Verse Mining"),
    PICKAXE("Офис Братства Кирки"),
    XENOPHARM("Офис XenoPharm"),
    JPK("Офис JPK Inc."),
    TXI("Отделение ТИК"),
    GZI("Отделение ГИЗ"),
    TRADING_GUILD("Отделение Торговой Гильдии"),
    NATHII_SHADOW("Ячейка Тени Натхии"),
    SMUGGLERS("Логово контрабандистов"),
    SECRETS_KEEPERS("Ячейка Хранителей таин"),
    EYE_APPLE_COALITION("Офис Коалиции Зрачка"),
    ENLIGHTENED_CHURCH("Церковь Просветлённых"),
    DORESHUAL("Терминалы Дорешуаля"),
    UNDEFINED("Неизвестно");

    companion object {
        fun byString(string: String): SpacePOIOffice {
            return SpacePOIOffice.entries.find { it.string == string } ?: UNDEFINED
        }
    }
}

enum class SpacePOIAccessStatus(val string: String) {
    AVAILABLE("Доступно"),
    RESTRICTED("Ограничено"),
    HIDDEN("Секретно"),
    UNDEFINED("Неизвестно");

    companion object {
        fun byString(string: String): SpacePOIAccessStatus {
            return SpacePOIAccessStatus.entries.find { it.string == string } ?: UNDEFINED
        }
    }
}

data class SpacePOI (
    override val id: String,
    val title: String,
    val subtitle: String?,
    val info: String,
    val visitRequirements: String,
    val parent: SpaceObject,
    val isLandable: Boolean,
    val accessStatus: SpacePOIAccessStatus,
    val navigationLengthMultiplier: Float,
    val navigationTimeMultiplier: Float,
    var places: Array<SpacePOIPlace> = arrayOf(),
    val offices: Array<SpacePOIOffice>,
    var connectedPOIs: Array<SpacePOI> = arrayOf()
) : IdEquatableEntity() {
    override fun equals(other: Any?): Boolean = super.equals(other)
    override fun hashCode(): Int = super.hashCode()
}