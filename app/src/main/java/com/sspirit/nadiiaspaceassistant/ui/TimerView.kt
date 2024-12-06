package com.sspirit.nadiiaspaceassistant.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sspirit.nadiiaspaceassistant.ui.utils.humanTime

@Composable
fun TimerView(
    timeleft: MutableFloatState,
    isRunning: MutableState<Boolean>,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(isRunning.value) {
        while (isRunning.value && timeleft.floatValue > 0) {
            kotlinx.coroutines.delay(1000)
            timeleft.value -= 1
            if (timeleft.floatValue <= 0)
                isRunning.value = false
        }
    }

    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .aspectRatio(1f)
                .fillMaxSize()
                .align(Alignment.Center)
                .background(Color(0xffd1dff6))
                .clickable {
                    if (timeleft.floatValue > 0)
                        isRunning.value = isRunning.value.not()
                }
        )
        Text(
            text = humanTime(timeleft.floatValue.toInt()),
            textAlign = TextAlign.Center,
            fontSize = 64.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxSize()
                .wrapContentHeight()
        )
    }
}