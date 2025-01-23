package com.sspirit.nadiiaspaceassistant.services.dataproviders

import android.content.Context
import android.util.Log
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential.fromStream
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.api.services.sheets.v4.model.AddSheetRequest
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest
import com.google.api.services.sheets.v4.model.DeleteDimensionRequest
import com.google.api.services.sheets.v4.model.DimensionRange
import com.google.api.services.sheets.v4.model.Request
import com.google.api.services.sheets.v4.model.SheetProperties
import com.google.api.services.sheets.v4.model.Spreadsheet
import com.google.api.services.sheets.v4.model.ValueRange
import com.sspirit.nadiiaspaceassistant.NadiiaSpaceApplication
import com.sspirit.nadiiaspaceassistant.R
import java.io.InputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

const val logTag = "Database"

typealias Completion = ((Boolean) -> Unit)?

open class GoogleSheetDataProvider {
    protected val service = getSheetsService()

    protected var expirationDate: LocalDateTime? = null

    private fun getSheetsService(): Sheets {
        val context: Context = NadiiaSpaceApplication.getContext()
        val inputStream: InputStream = context.resources.openRawResource(R.raw.google_oauth)
        val credentials = fromStream(inputStream)
            .createScoped(listOf(SheetsScopes.SPREADSHEETS))

        val httpTransport = GoogleNetHttpTransport.newTrustedTransport()
        val jsonFactory: JsonFactory = GsonFactory.getDefaultInstance()

        return Sheets
            .Builder(httpTransport, jsonFactory, credentials)
            .setApplicationName("NadiiaSpaceAssistant")
            .build()
    }

    protected fun request(spreadsheetId: String, sheet: String): ValueRange {
        return service
            .spreadsheets()
            .values()
            .get(spreadsheetId, sheet)
            .execute()
    }

    protected fun columnIndexByInt(intIndex: Int): String {
        val string26 = intIndex.toString(26)
        val elements = string26.map {
            val digit = it.toString().toInt(26)
            return@map (digit + 9).toString(36)
        }
        return elements.joinToString("").uppercase()
    }

    protected fun uploadRow(
        spreadsheetId: String,
        sheet: String,
        row: Int,
        data: List<String>,
        completion: ((Boolean) -> Unit)? = null
    ) {
        uploadData(spreadsheetId, sheet, 1, row, listOf(data), completion)
    }

    protected fun uploadData(
        spreadsheetId: String,
        sheet: String,
        column: Int,
        startRow: Int,
        data: List<List<String>>,
        completion: ((Boolean) -> Unit)? = null
    ) {
        try {
            val width = data.firstOrNull()?.size ?: 0
            val endRow = startRow + data.size - 1
            val startColumn = columnIndexByInt(column)
            val endColumn = columnIndexByInt(column + width - 1)
            val range = "${sheet}!${startColumn}${startRow}:${endColumn}${endRow}"

            val valueRange = ValueRange().setValues(data)

            service
                .spreadsheets()
                .values()
                .update(spreadsheetId, range, valueRange)
                .setValueInputOption("USER_ENTERED")
                .execute()

            completion?.invoke(true)
        } catch (e: Exception) {
            Log.e("Database", "Data update error: ${e.toString()}")
            completion?.invoke(false)
        }
    }

    protected fun uploadCell(
        spreadsheetId: String,
        sheet: String,
        column: String,
        row: Int,
        newValue: Any,
        completion: ((Boolean) -> Unit)? = null
    ) {
        try {
            val valueRange = ValueRange().setValues(listOf(listOf(newValue.toString())))
            val range = "$sheet!$column$row"
            service
                .spreadsheets()
                .values()
                .update(spreadsheetId, range, valueRange)
                .setValueInputOption("RAW")
                .execute()
            completion?.invoke(true)
        } catch (e: Exception) {
            Log.e("Database", "Data update error: ${e.toString()}")
            completion?.invoke(false)
        }
    }

    protected fun getSheetNames(spreadsheetId: String): List<String> {
        val spreadsheet: Spreadsheet = service.spreadsheets()
            .get(spreadsheetId)
            .setFields("sheets(properties(title))")
            .execute()

        return spreadsheet.sheets.mapNotNull { it.properties.title }
    }

    protected fun firstRowWithText(
        text: String,
        spreadsheetId: String,
        sheetName: String,
        column: Int = 1,
    ): Int? {
        val rows = searchRowsWithText(text, spreadsheetId, sheetName, column)
        return rows.firstOrNull()
    }

    protected fun searchAssociatedRowsWithText(
        text: String,
        spreadsheetId: String,
        sheetName: String,
        column: Int = 1,
        associatedColumn: Int
    ): Map<Int, String?> {
        val columnIndex = columnIndexByInt(column)
        val associatedColumnIndex = columnIndexByInt(associatedColumn)

        val range = if (column < associatedColumn)
            "$sheetName!A:$associatedColumnIndex"
        else
            "$sheetName!A:$columnIndex"

        val response = request(spreadsheetId, range)
        return response.getValues()
            .mapIndexedNotNull { index, value ->
                if (value.elementAtOrNull(column - 1) == text) index + 1 else null
            }
            .associateWith {
                response.getValues()[it - 1].elementAtOrNull(associatedColumn - 1)?.toString()
            }
    }

    protected fun searchRowsWithText(
        text: String,
        spreadsheetId: String,
        sheetName: String,
        column: Int = 1,
    ): Array<Int> {
        val columnIndex = columnIndexByInt(column)
        val range = "$sheetName!$columnIndex:$columnIndex"
        val response = request(spreadsheetId, range)
        return response.getValues()
            .mapIndexedNotNull { index, value ->
                if (value.firstOrNull() == text) index + 1 else null
            }
            .toTypedArray()
    }

    protected fun addSheet(spreadsheetId: String, sheetName: String) {
        val addSheetRequest = Request().apply {
            addSheet = AddSheetRequest().apply {
                properties = SheetProperties().apply {
                    title = sheetName
                }
            }
        }

        val batchUpdateRequest = BatchUpdateSpreadsheetRequest().apply {
            requests = listOf(addSheetRequest)
        }

        service.spreadsheets().batchUpdate(spreadsheetId, batchUpdateRequest).execute()
    }

    private fun rowDeleteRequest(sheetId: Int, row: Int, ): Request =
        Request().setDeleteDimension(
            DeleteDimensionRequest().setRange(
                DimensionRange()
                    .setSheetId(sheetId)
                    .setDimension("ROWS")
                    .setStartIndex(row - 1)
                    .setEndIndex(row)
            )
        )

    protected fun deleteRows(
        spreadsheetId: String,
        sheet: String,
        rows: Array<Int>,
        completion: ((Boolean) -> Unit)? = null
    ) {
        if (rows.isEmpty()) {
            completion?.invoke(true)
            return
        }

        try {
            val spreadsheet = service
                .spreadsheets()
                .get(spreadsheetId)
                .execute()
            val sheetId = spreadsheet.sheets
                .first { it.properties.title == sheet }
                .properties.sheetId
            val requests = rows
                .sortedDescending()
                .map { rowDeleteRequest(sheetId, it) }
            val batchUpdateRequest = BatchUpdateSpreadsheetRequest()
                .setRequests(requests)
            service
                .spreadsheets()
                .batchUpdate(spreadsheetId, batchUpdateRequest)
                .execute()

            completion?.invoke(true)
        } catch (e: Exception) {
            Log.e("Database", "Rows deletion error: $e")
            completion?.invoke(false)
        }
    }

    protected fun deleteRow(
        spreadsheetId: String,
        sheet: String,
        row: Int,
        completion: ((Boolean) -> Unit)? = null
    ) {
        try {
            val spreadsheet = service
                .spreadsheets()
                .get(spreadsheetId)
                .execute()
            val sheetId = spreadsheet.sheets
                .first { it.properties.title == sheet }
                .properties.sheetId

            val deleteRequest = rowDeleteRequest(sheetId, row)
            val batchUpdateRequest = BatchUpdateSpreadsheetRequest()
                .setRequests(listOf(deleteRequest))
            service
                .spreadsheets()
                .batchUpdate(spreadsheetId, batchUpdateRequest)
                .execute()

            completion?.invoke(true)
        } catch (e: Exception) {
            Log.e("Database", "Row deletion error: $e")
            completion?.invoke(false)
        }
    }

    protected fun insert(
        spreadsheetId: String,
        sheet: String,
        row: Int,
        data: List<List<String>>,
        completion: ((Boolean) -> Unit)?
    ) {
        try {
            val valueRange = ValueRange().setValues(data)
            val range = "$sheet!A$row"
            service.spreadsheets()
                .values()
                .append(spreadsheetId, range, valueRange)
                .setValueInputOption("USER_ENTERED")
                .setInsertDataOption("INSERT_ROWS")
                .execute()
            completion?.invoke(true)
        } catch (e: Exception) {
            Log.e("Database", "Data insert error: $e")
            completion?.invoke(false)
        }
    }

    protected fun append(
        spreadsheetId: String,
        sheet: String,
        data: List<String>,
        completion: ((Boolean) -> Unit)?
    ) {
        try {
            val valueRange = ValueRange().setValues(listOf(data))
            service.spreadsheets()
                .values()
                .append(spreadsheetId, sheet, valueRange)
                .setValueInputOption("USER_ENTERED")
                .execute()
            completion?.invoke(true)
        } catch (e: Exception) {
            Log.e("Database", "Data append error: $e")
            completion?.invoke(false)
        }
    }

    protected inline fun <reified T>parseToArray(
        range: ValueRange,
        error: String,
        parser: (Array<Any>) -> T,
        upDownMerge: Array<Int> = arrayOf()
    ) : Array<T> {
        val rawObjects = range.getValues()
            ?.filter { it.size != 0 }
            ?.map { it.toTypedArray() }
            ?.toTypedArray()
        val objects = mutableListOf<T>()

        try {
            if (rawObjects != null) {
                var previous: Array<Any>? = null
                for (rawObject in rawObjects) {
                    merge(rawObject, previous, upDownMerge)
                    objects.add(parser(rawObject))
                    previous = rawObject
                }
            }
        } catch (e: Exception) {
            Log.e(logTag, "${error}: $e")
        }

        return objects.toTypedArray()
    }

    protected fun merge(main: Array<Any>, support: Array<Any>?, upDownMerge: Array<Int>) {
        if (support == null) return

        for (index in upDownMerge) {
            if (support.size -1 < index) continue
            val isEmpty = (main[index] as String).isEmpty()
            main[index] = if (isEmpty) support[index] else main[index]
        }
    }
}
