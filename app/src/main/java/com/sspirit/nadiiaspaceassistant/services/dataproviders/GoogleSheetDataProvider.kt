package com.sspirit.nadiiaspaceassistant.services.dataproviders

import android.content.Context
import android.util.Log
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential.fromStream
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.api.services.sheets.v4.model.ValueRange
import com.sspirit.nadiiaspaceassistant.NadiiaSpaceApplication
import com.sspirit.nadiiaspaceassistant.R
import java.io.InputStream

open class GoogleSheetDataProvider {
    open fun getSheetsService(): Sheets {
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

    open fun columnIndexByInt(intIndex: Long): String {
        val string26 = intIndex.toString(26)
        val elements = string26.map {
            val digit = it.toString().toInt(26)
            return (digit + 10).toString(36)
        }
        return elements.joinToString()
    }

    open fun updateCell(
        spreadsheetId: String,
        range: String,
        newValue: String,
        completion: (() -> Unit)? = null) {
        try {
            val valueRange = ValueRange().setValues(listOf(listOf(newValue)))
            getSheetsService()
                .spreadsheets()
                .values()
                .update(spreadsheetId, range, valueRange)
                .setValueInputOption("RAW")
                .execute()
            completion?.invoke()
        } catch (e : Exception) {
            Log.e("Database", "Data update error: ${e.toString()}")
        }
    }
}
