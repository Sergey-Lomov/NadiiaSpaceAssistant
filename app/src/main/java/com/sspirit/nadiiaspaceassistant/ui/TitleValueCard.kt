package com.sspirit.nadiiaspaceassistant.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val textModifier = Modifier
    .fillMaxHeight()
    .wrapContentHeight(align = CenterVertically)

private val textSize = 26.sp

@Composable
fun TitleValueCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    useDefaults: Boolean = true
) {
    MainContent(title, modifier, useDefaults) {
        Text(
            text = value,
            fontSize = textSize,
            modifier = textModifier
        )
    }
}

@Composable
fun EditableTitleValueCard(
    title: String,
    initialValue: String,
    modifier: Modifier = Modifier,
    useDefaults: Boolean = true,
    fieldWidth: Int? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    onChange: (String) -> Unit
) {
    var inputValue by remember { mutableStateOf(initialValue) }
    val focusManager = LocalFocusManager.current

    var fieldModifier = textModifier
    fieldWidth?.let { fieldModifier = fieldModifier.width(it.dp) }

    MainContent(title, modifier, useDefaults) {
        TextField(
            value = inputValue,
            modifier = fieldModifier,
            textStyle = TextStyle(
                fontSize = textSize * 0.9
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = keyboardType,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                }
            ),
            onValueChange = { newText ->
                inputValue = newText
                onChange(newText)
            }
        )
    }
}

@Composable
private fun MainContent(
    title: String,
    modifier: Modifier = Modifier,
    useDefaults: Boolean = true,
    valueBuilder: @Composable () -> Unit
) {
    val cardModifier = if (useDefaults) adaptedModifier(modifier) else modifier

    Card(modifier = cardModifier) {
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                fontSize = 26.sp,
                modifier = textModifier
            )

            Spacer(Modifier.weight(1f))

            valueBuilder()
        }
    }
}

private fun adaptedModifier(source: Modifier) : Modifier {
        return source
            .fillMaxWidth()
            .height(70.dp)
            .padding(horizontal = 16.dp, vertical = 4.dp)
}