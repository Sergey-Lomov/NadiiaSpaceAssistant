package com.sspirit.nadiiaspaceassistant.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.sspirit.nadiiaspaceassistant.R
import com.sspirit.nadiiaspaceassistant.services.NotificationsManager
import com.sspirit.nadiiaspaceassistant.ui.RegularText

enum class NotificationType {
    NEGATIVE,
    NEUTRAL,
    POSITIVE
}

@Composable
fun NotificationsView() {
    val isVisible = remember { mutableStateOf(false) }
    val message = remember { mutableStateOf("") }
    val type = remember { mutableStateOf(NotificationType.NEUTRAL) }

    LaunchedEffect(Unit) {
        NotificationsManager.isVisibleSate = isVisible
        NotificationsManager.messageState = message
        NotificationsManager.typeState = type
    }

    if (!isVisible.value) return
    val color = when (type.value) {
        NotificationType.NEGATIVE -> colorResource(R.color.soft_red)
        NotificationType.NEUTRAL -> Color.LightGray
        NotificationType.POSITIVE -> colorResource(R.color.soft_green)
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = color),
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .clickable { NotificationsManager.hideCurrentNotification() }
    ) {
        RegularText(
            text = message.value,
            modifier = Modifier.padding(8.dp)
        )
    }
}