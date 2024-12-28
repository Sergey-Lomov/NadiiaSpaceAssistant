package com.sspirit.nadiiaspaceassistant.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

@Composable
fun HeaderText(
    text: String,
    align: TextAlign = TextAlign.Center,
    modifier: Modifier = Modifier) {
    Text(
        text = text,
        textAlign = align,
        fontSize = 26.sp,
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
fun RegularText(
    text: String,
    color: Color = MaterialTheme.colorScheme.onSurface,
    autofill: Boolean = true,
    align: TextAlign = TextAlign.Left,
    modifier: Modifier = Modifier
) {
    val adapted = if (autofill) modifier.fillMaxWidth() else Modifier
    Text(
        text = text,
        color = color,
        fontSize = 18.sp,
        textAlign = align,
        modifier = adapted
    )
}