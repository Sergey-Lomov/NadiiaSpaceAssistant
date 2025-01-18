package com.sspirit.nadiiaspaceassistant.services.dataproviders.tablerows

interface RawDataConvertibleTableRow {
    fun toRawData(): List<String>
}

fun MutableList<String>.write(row: RawDataConvertibleTableRow) = addAll(row.toRawData())