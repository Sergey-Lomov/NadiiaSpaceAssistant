package com.sspirit.nadiiaspaceassistant.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex

data class OptionsPickerItem<T> (val value: T, val title: String)

@Composable
fun <T> OptionPicker(
    options: Array<OptionsPickerItem<T>>,
    isPickerVisible: MutableState<Boolean>,
    onSelect: (T) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable { isPickerVisible.value = false }

    ) {
        Card(
            modifier = Modifier
                .wrapContentSize()
                .align(Alignment.Center)
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .wrapContentSize()
            ) {
                for (option: OptionsPickerItem<T> in options) {
                    Text(
                        text = option.title,
                        fontSize = 26.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .height(50.dp)
                            .fillMaxWidth()
                            .wrapContentHeight(align = CenterVertically)
                            .clickable {
                                onSelect(option.value)
                                isPickerVisible.value = false
                            }
                    )

                    if (option !== options.last()) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            thickness = 1.dp,
                        )
                    }
                }
            }
        }
    }
}