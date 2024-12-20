package com.sspirit.nadiiaspaceassistant.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StyledButton(
    title: String,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        shape = CircleShape,
        enabled = enabled,
        modifier = modifier,
        onClick = onClick
    ) {
        Text(
            text = title,
            fontSize = 24.sp,
            style = MaterialTheme.typography.labelLarge
        )
    }
}