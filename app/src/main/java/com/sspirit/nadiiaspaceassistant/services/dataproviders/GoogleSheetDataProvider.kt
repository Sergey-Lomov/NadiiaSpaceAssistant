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

open class GoogleSheetDataProvider {
    internal val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    internal val service = getSheetsService()
    var expirationDate: LocalDateTime? = null

    fun getSheetsService(): Sheets {
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

    open fun columnIndexByInt(intIndex: Int): String {
        val string26 = intIndex.toString(26)
        val elements = string26.map {
            val digit = it.toString().toInt(26)
            return (digit + 9).toString(36)
        }
        return elements.joinToString().uppercase()
    }

    open fun uploadData(
        spreadsheetId: String,
        sheet: String,
        column: Int,
        row: Int,
        data: List<List<String>>,
        completion: ((Boolean) -> Unit)? = null) {
        try {
            val width = data.first()?.size ?: 0
            val height = data.size
            val startColumn = columnIndexByInt(column)
            val endColumn = columnIndexByInt(column + width)
            val range = "${sheet}!${startColumn}${row}:${endColumn}${row + height}"

            val valueRange = ValueRange().setValues(data)

            getSheetsService()
                .spreadsheets()
                .values()
                .update(spreadsheetId, range, valueRange)
                .setValueInputOption("RAW")
                .execute()

            completion?.invoke(true)
        } catch (e : Exception) {
            Log.e("Database", "Data update error: ${e.toString()}")
            completion?.invoke(false)
        }
    }

    open fun uploadCell(
        spreadsheetId: String,
        range: String,
        newValue: String,
        completion: ((Boolean) -> Unit)? = null) {
        try {
            val valueRange = ValueRange().setValues(listOf(listOf(newValue)))
            getSheetsService()
                .spreadsheets()
                .values()
                .update(spreadsheetId, range, valueRange)
                .setValueInputOption("RAW")
                .execute()
            completion?.invoke(true)
        } catch (e : Exception) {
            Log.e("Database", "Data update error: ${e.toString()}")
            completion?.invoke(false)
        }
    }

    open fun getSheetNames(service: Sheets, spreadsheetId: String): List<String> {
        val spreadsheet: Spreadsheet = service.spreadsheets()
            .get(spreadsheetId)
            .setFields("sheets(properties(title))")
            .execute()

        return spreadsheet.sheets.mapNotNull { it.properties.title }
    }

    fun addSheet(spreadsheetId: String, sheetName: String) {
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

    inline fun <reified T>parseToArray(
        range: ValueRange,
        error: String,
        parser: (Array<Any>) -> T,
        upDownMerge: Array<Int> = arrayOf()
    ) : Array<T> {
        val rawObjects = range.getValues()?.map { it.toTypedArray() }?.toTypedArray()
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
            Log.e(logTag, "${error}: ${e.toString()}")
        }

        return objects.toTypedArray()
    }

    fun merge(main: Array<Any>, support: Array<Any>?, upDownMerge: Array<Int>) {
        if (support == null) return

        for (index in upDownMerge) {
            if (support.size -1 < index) continue
            val isEmpty = (main[index] as String).isEmpty()
            main[index] = if (isEmpty) support[index] else main[index]
        }
    }
}
