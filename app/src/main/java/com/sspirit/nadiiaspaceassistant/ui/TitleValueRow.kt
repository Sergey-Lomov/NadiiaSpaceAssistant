package com.sspirit.nadiiaspaceassistant.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TitleValueRow(
    title: String,
    value: Any,
    fontSize: Int = 18,
    fontWeight: FontWeight = FontWeight.Normal,
    balancedWeights: Boolean = false,
) {
    val valueString = value.toString()
    val lWeight = title.length.toFloat() / (valueString.length + title.length)
    val rWeight = valueString.length.toFloat() / (valueString.length + title.length)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        val lModifier = if (balancedWeights) Modifier.weight(lWeight) else Modifier.wrapContentWidth()
        val rModifier = if (balancedWeights) Modifier.weight(rWeight) else Modifier.wrapContentWidth()

        Text(
            text = title,
            fontSize = fontSize.sp,
            fontWeight = fontWeight,
            modifier = lModifier.defaultMinSize(minWidth = 100.dp)
        )
        if (balancedWeights)
            Spacer(Modifier.width(8.dp))
        else
            Spacer(Modifier.weight(1f))

        Text(
            text = valueString,
            fontSize = fontSize.sp,
            fontWeight = fontWeight,
            textAlign = TextAlign.End,
            modifier = rModifier.defaultMinSize(minWidth = 100.dp)
        )
    }
}