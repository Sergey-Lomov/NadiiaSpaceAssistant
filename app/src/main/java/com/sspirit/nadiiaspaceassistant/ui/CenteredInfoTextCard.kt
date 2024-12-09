package com.sspirit.nadiiaspaceassistant.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CenteredInfoTextCard(
    primary: String,
    secondary: String? = null,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null) {
    Card(
        modifier = modifier
            .clickable { onClick?.invoke() }
    ) {
        Column {
            Spacer(Modifier.height(16.dp))

            Text(
                text = primary,
                fontSize = 24.sp,
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .wrapContentHeight(align = CenterVertically),
                textAlign = TextAlign.Center,
            )

            if (secondary != null) {
                Text(
                    text = secondary,
                    fontSize = 16.sp,
                    color = Color.Gray,
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .wrapContentHeight(align = CenterVertically),
                    textAlign = TextAlign.Center,
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}