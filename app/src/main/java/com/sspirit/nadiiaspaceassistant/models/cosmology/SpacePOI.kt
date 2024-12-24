package com.sspirit.nadiiaspaceassistant.models.cosmology

import com.sspirit.nadiiaspaceassistant.extensions.IndexConvertible

enum class SpacePOIOffice(val string: String) {
    ALLIANCE("Представительство Альянса"),
    VERSEMINING("Офис Verse Mining"),
    PICKAXE("Офис Братства Кирки"),
    XENOPHARM("Офис XenoPharm"),
    JPK("Офис JPK Inc."),
    TXI("Отделение ТИК"),
    GZI("Отделение ГИЗ"),
    TRADING_GUILD("Отделение Торговой Гильдии"),
    UNDEFINED("Неизвестно");

    companion object {
        fun byString(string: String): SpacePOIOffice {
            return SpacePOIOffice.entries.find { it.string == string } ?: UNDEFINED
        }
    }
}

enum class SpacePOIKeys(override val index: Int) : IndexConvertible {
    ID(0),
    PARENT(1),
    TITLE(2),
    SUBTITLE(3),
    INFO(4),
    STATUS(5),
    NAV_LENGTH_MULT(6),
    NAV_TIME_MULT(7),
    VISIT_REQUIREMENTS(8),
    PLACES(9),
    OFFICES(10),
}

enum class SpacePOIStatus(val string: String) {
    AVAILABLE("Доступно"),
    RESTRICTED("Недоступно"),
    HIDDEN("Секретно"),
    UNDEFINED("Неизвестно");

    companion object {
        fun byString(string: String): SpacePOIStatus {
            return SpacePOIStatus.entries.find { it.string == string } ?: UNDEFINED
        }
    }
}

data class SpacePOI (
    val id: String,
    val title: String,
    val subtitle: String?,
    val info: String,
    val visitRequirements: String,
    val parent: SpaceObject,
    val status: SpacePOIStatus,
    val navigationLengthMultiplier: Float,
    val navigationTimeMultiplier: Float,
    var places: Array<SpacePOIPlace> = arrayOf(),
    val offices: Array<SpacePOIOffice>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SpacePOI

        if (id != other.id) return false
        if (title != other.title) return false
        if (subtitle != other.subtitle) return false
        if (info != other.info) return false
        if (visitRequirements != other.visitRequirements) return false
        if (parent != other.parent) return false
        if (status != other.status) return false
        if (navigationLengthMultiplier != other.navigationLengthMultiplier) return false
        if (navigationTimeMultiplier != other.navigationTimeMultiplier) return false
        if (!places.contentEquals(other.places)) return false
        if (!offices.contentEquals(other.offices)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + (subtitle?.hashCode() ?: 0)
        result = 31 * result + info.hashCode()
        result = 31 * result + visitRequirements.hashCode()
        result = 31 * result + parent.hashCode()
        result = 31 * result + status.hashCode()
        result = 31 * result + navigationLengthMultiplier.hashCode()
        result = 31 * result + navigationTimeMultiplier.hashCode()
        result = 31 * result + places.contentHashCode()
        result = 31 * result + offices.contentHashCode()
        return result
    }
}