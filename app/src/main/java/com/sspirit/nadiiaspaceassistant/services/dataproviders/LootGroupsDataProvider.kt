package com.sspirit.nadiiaspaceassistant.services.dataproviders

import android.util.Log
import com.google.api.services.sheets.v4.model.ValueRange
import com.sspirit.nadiiaspaceassistant.extensions.getFloat
import com.sspirit.nadiiaspaceassistant.extensions.getRange
import com.sspirit.nadiiaspaceassistant.models.items.LootGroup
import com.sspirit.nadiiaspaceassistant.models.items.LootGroupItem
import com.sspirit.nadiiaspaceassistant.models.items.LootGroupKeys
import com.sspirit.nadiiaspaceassistant.models.items.LootGroupLock
import com.sspirit.nadiiaspaceassistant.extensions.getString
import java.time.LocalDateTime

private const val expirationHours = 24
private const val lootSpreadsheetId = "14MXuy5wPFuFrsM8nYnFYw9lUFUErYR-BuEegsrVOTkA"
private const val lootListRange = "Loot!A3:K150"

object LootGroupsDataProvider : GoogleSheetDataProvider() {
    var groups: Array<LootGroup> = arrayOf()

    fun getLootGroups(forced: Boolean = false) {
        if (expirationDate != null && !forced) {
            if (LocalDateTime.now() < expirationDate) {
                return
            }
        }

        val response = service
            .spreadsheets()
            .values()
            .get(lootSpreadsheetId, lootListRange)
            .execute()

        groups = parseGroups(response)
        expirationDate = LocalDateTime.now().plusHours(expirationHours.toLong())
    }
}

private fun parseGroups(range: ValueRange): Array<LootGroup> {
    val rawLines = range.getValues()?.map { it.toTypedArray() }?.toTypedArray()
    val groups = mutableMapOf<String, LootGroup>()

    try {
        if (rawLines != null) {
            for (raw in rawLines) {
                val id = raw.getString(LootGroupKeys.ID)
                if (groups[id] == null)
                    groups[id] = parseGroup(raw)

                val item = parseItem(raw)
                if (item != null)
                    groups[id]?.items?.add(item)
            }
        }
    } catch (e: Exception) {
        Log.e(logTag, "Invalid loot groups data: ${e.toString()}")
    }

    return groups.values.toTypedArray()
}

private fun parseGroup(raw: Array<Any>): LootGroup {
    val rawLock = raw.getString(LootGroupKeys.LOCK)
    return LootGroup(
        id = raw.getString(LootGroupKeys.ID),
        title = raw.getString(LootGroupKeys.TITLE),
        description = raw.getString(LootGroupKeys.DESCRIPTION),
        lock = LootGroupLock.byString(rawLock),
        items = mutableListOf()
    )
}

private fun parseItem(raw: Array<Any>): LootGroupItem? {
    val id = raw.getString(LootGroupKeys.ITEM_ID)
    val descriptor = ItemDataProvider.descriptors.firstOrNull { it.id == id }
    if (descriptor == null) {
        Log.e(logTag, "Missed item descriptor for id: $id")
        return null
    }

    return LootGroupItem(
        descriptor = descriptor,
        amount = raw.getRange(LootGroupKeys.ITEM_MIN_AMOUNT, LootGroupKeys.ITEM_MAX_AMOUNT),
        weight = raw.getFloat(LootGroupKeys.ITEM_WEIGHT)
    )
}