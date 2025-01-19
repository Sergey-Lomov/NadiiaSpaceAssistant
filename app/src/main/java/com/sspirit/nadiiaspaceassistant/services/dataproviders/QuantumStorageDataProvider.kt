package com.sspirit.nadiiaspaceassistant.services.dataproviders

import com.sspirit.nadiiaspaceassistant.models.items.QuantumStorage
import com.sspirit.nadiiaspaceassistant.services.dataproviders.tablerows.QuantumStorageTableRow
import com.sspirit.nadiiaspaceassistant.services.dataproviders.tablerows.toQuantumStorages
import com.sspirit.nadiiaspaceassistant.utils.plusHours
import java.time.LocalDateTime

private const val expirationHours = 2
private const val spreadsheetId = "14MXuy5wPFuFrsM8nYnFYw9lUFUErYR-BuEegsrVOTkA"
private const val sheet = "QStorages"
private const val firstRow = 3
private const val fullRange = "$sheet!A$firstRow:C150"

object QuantumStorageDataProvider : GoogleSheetDataProvider() {
    var storages: MutableList<QuantumStorage> = mutableListOf()

    fun downloadStorages(forced: Boolean = false) {
        if (expirationDate != null && !forced) {
            if (LocalDateTime.now() < expirationDate) {
                return
            }
        }

        val response = request(spreadsheetId, fullRange)
        val rows = parseToArray(response, "Quantum storages data invalid", QuantumStorageTableRow::parse)
        storages = rows.toQuantumStorages()
        expirationDate = LocalDateTime.now().plusHours(expirationHours)
    }

    fun getBy(id: Int) : QuantumStorage? {
        return storages.firstOrNull { it.id == id }
    }

    fun remove(storage: QuantumStorage, completion: Completion = null) = synchronized(storage) {
        val indices = searchRowsWithText(storage.id.toString(), spreadsheetId, sheet)
        deleteRows(spreadsheetId, sheet, indices) { success ->
            if (success) storages.remove(storage)
            completion?.invoke(success)
        }
    }

    fun add(storage: QuantumStorage, completion: Completion = null) {
        val rows = QuantumStorageTableRow.from(storage)
        val data = rows.map { it.toRawData() }
        insert(spreadsheetId, sheet, firstRow, data) { success ->
            if (success) storages.add(storage)
            completion?.invoke(success)
        }
    }

    fun replace(storage: QuantumStorage, completion: Completion) {
        val oldStorage = getBy(storage.id)
        if (oldStorage != null) {
            remove(oldStorage) { success ->
                if (!success) completion?.invoke(false)
            }
        }

        add(storage) { success ->
            if (!success && oldStorage != null)
                storages.add(oldStorage)
            completion?.invoke(success)
        }
    }
}