package com.sspirit.nadiiaspaceassistant.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TitlesValuesList(map: Map<String, Any>) {
    val pairsArray = map.map { it.key to it.value }.toTypedArray()
    TitlesValuesList(*pairsArray)
}

@Composable
fun TitlesValuesList(vararg pairs: Pair<String, Any>) {
    Column {
        for (pair in pairs) {
            TitleValueRow(pair.first, pair.second.toString(), fontSize = 18)
            if (pair !== pairs.last()) {
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}