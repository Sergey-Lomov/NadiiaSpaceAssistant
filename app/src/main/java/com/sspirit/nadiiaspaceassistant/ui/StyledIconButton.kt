package com.sspirit.nadiiaspaceassistant.ui

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

@Composable
fun StyledIconButton(
    icon: ImageVector,
    description: String,
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
        Icon(
            imageVector = icon,
            contentDescription = description,
            tint = Color.White
        )
    }
}