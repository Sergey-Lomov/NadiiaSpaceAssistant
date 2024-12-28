package com.sspirit.nadiiaspaceassistant.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TitlesValuesList(map: Map<String, Any>) {
    Column {
        var counter = 0
        for (pair in map) {
            TitleValueRow(pair.key, pair.value.toString(), fontSize = 18)
            if (counter < map.size - 1) {
                Spacer(Modifier.height(8.dp))
            }
            counter++;
        }
    }
}