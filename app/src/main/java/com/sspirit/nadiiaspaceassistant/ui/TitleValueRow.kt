package com.sspirit.nadiiaspaceassistant.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TitleValueRow(
    title: String,
    value: String,
    fontSize: Int = 18,
    fontWeight: FontWeight = FontWeight.Normal
) {
    Row(
       modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            fontSize = fontSize.sp,
            fontWeight = fontWeight
        )
        Spacer(Modifier.weight(1f).defaultMinSize(minWidth = 8.dp))
        Text(
            text = value,
            fontSize = fontSize.sp,
            fontWeight = fontWeight,
            textAlign = TextAlign.Right
        )
    }
}