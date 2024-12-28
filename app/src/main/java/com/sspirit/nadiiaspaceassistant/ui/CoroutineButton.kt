package com.sspirit.nadiiaspaceassistant.ui

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sspirit.nadiiaspaceassistant.services.dataproviders.CharacterDataProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun CoroutineButton(
    title: String,
    modifier: Modifier = Modifier,
    routine: () -> Unit,
    completion: (() -> Unit)? = null
) {
    var inProgress by remember { mutableStateOf(false) }

    Button(
        shape = CircleShape,
        modifier = modifier,
        enabled = inProgress.not(),
        onClick =  {
            inProgress = true
            val job = CoroutineScope(Dispatchers.IO).launch {
                try {
                    routine.invoke()
                } catch (e: Exception) {
                    Log.e("Coroutine error", e.toString())
                }
            }
            job.invokeOnCompletion {
                CoroutineScope(Dispatchers.Main).launch {
                    inProgress = false
                    completion?.invoke()
                }
            }
        }
    ) {
        Box(modifier = Modifier
            .defaultMinSize(minWidth = 20.dp)
        ) {
            val color = if (inProgress) Color.Transparent else Color.White
            Text(
                text = title,
                fontSize = 24.sp,
                style = MaterialTheme.typography.labelLarge,
                color = color,
                modifier = Modifier.align(Alignment.Center)
            )
            if (inProgress) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.secondary,
                    strokeWidth = 2.dp,
                    modifier = Modifier
                        .size(20.dp)
                        .padding(0.dp)
                        .align(Alignment.Center),
                )
            }
        }
    }
}