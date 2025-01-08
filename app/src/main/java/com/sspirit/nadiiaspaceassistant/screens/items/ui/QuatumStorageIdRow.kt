package com.sspirit.nadiiaspaceassistant.screens.items.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sspirit.nadiiaspaceassistant.models.items.QuantumStorage
import com.sspirit.nadiiaspaceassistant.ui.ColoredCircle

@Composable
fun QuantumStorageIdRow(id: Int, radius: Int = 18) {
    val bits = (0 until 8).map { (id shr it) and 1 }

    Row(Modifier.wrapContentWidth()) {
        for (i in bits.indices.reversed()) {
            val color = if (bits[i] == 1) Color.Red else Color.Black
            ColoredCircle(color, radius)
            if (i != 0)
                Spacer(Modifier.width(8.dp))
        }
    }
}