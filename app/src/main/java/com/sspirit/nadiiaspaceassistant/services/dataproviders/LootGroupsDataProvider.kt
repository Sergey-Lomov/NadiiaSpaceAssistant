package com.sspirit.nadiiaspaceassistant.services.dataproviders

import android.util.Log
import com.sspirit.nadiiaspaceassistant.models.items.LootGroup
import com.sspirit.nadiiaspaceassistant.models.items.LootGroupItem
import com.sspirit.nadiiaspaceassistant.models.items.LootGroupLock
import com.sspirit.nadiiaspaceassistant.services.dataproviders.tablerows.LootGroupTableRow
import com.sspirit.nadiiaspaceassistant.utils.plusHours
import java.time.LocalDateTime

private const val expirationHours = 24
private const val lootSpreadsheetId = "14MXuy5wPFuFrsM8nYnFYw9lUFUErYR-BuEegsrVOTkA"
private const val lootListRange = "Loot!A3:K150"

object LootGroupsDataProvider : GoogleSheetDataProvider() {
    var groups: Array<LootGroup> = arrayOf()

    fun downloadLootGroups(forced: Boolean = false) {
        if (expirationDate != null && !forced) {
            if (LocalDateTime.now() < expirationDate) {
                return
            }
        }

        val response = request(lootSpreadsheetId, lootListRange)
        val rows = parseToArray(
            range = response,
            error = "Invalid loot groups data",
            parser = LootGroupTableRow::parse,
            upDownMerge = arrayOf(0, 1, 2, 3)
        )
        groups = groupsFrom(rows)
        expirationDate = LocalDateTime.now().plusHours(expirationHours)
    }

    fun getGroup(id: String) : LootGroup? {
        return groups.firstOrNull { it.id == id }
    }
}

private fun groupsFrom(rows: Array<LootGroupTableRow>): Array<LootGroup> {
    val groups = mutableMapOf<String, LootGroup>()

    for (row in rows) {
        if (groups[row.id] == null)
            groups[row.id] = groupFrom(row)

        val item = itemFrom(row)
        if (item != null)
            groups[row.id]?.items?.add(item)
    }

    return groups.values.toTypedArray()
}

private fun groupFrom(row: LootGroupTableRow): LootGroup {
    return LootGroup(
        id = row.id,
        title = row.title,
        description = row.description,
        lock = LootGroupLock.byString(row.lock),
    )
}

private fun itemFrom(row: LootGroupTableRow): LootGroupItem? {
    val descriptor = ItemDataProvider.descriptors.firstOrNull { it.id == row.itemId }
    if (descriptor == null) {
        Log.e(logTag, "Missed item descriptor for id: ${row.itemId}")
        return null
    }

    return LootGroupItem(
        descriptor = descriptor,
        amount = IntRange(row.minAmount, row.maxAmount),
        weight = row.weight
    )
}