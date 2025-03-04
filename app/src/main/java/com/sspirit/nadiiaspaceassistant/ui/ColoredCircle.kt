package com.sspirit.nadiiaspaceassistant.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sspirit.nadiiaspaceassistant.screens.building.ui.BuildingElementsColors

@Composable
fun ColoredCircle(
    color: Color,
    size: Int,
    offset: IntOffset = IntOffset(0,0),
    body: (@Composable () -> Unit)? = null
) {
    ColoredCircle(color, size, 0, Color.Transparent, offset, body)
}

@Composable
fun ColoredCircle(
    color: Color,
    size: Int,
    borderWith: Int,
    borderColor: Color,
    offset: IntOffset = IntOffset(0,0),
    body: (@Composable () -> Unit)? = null
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(size.dp)
            .offset(offset.x.dp, offset.y.dp)
            .clip(CircleShape)
            .border(borderWith.dp, borderColor)
            .background(color)
    ) {
        body?.invoke()
    }
}

@Composable
fun ColoredCircle(
    colors: BuildingElementsColors,
    size: Int,
    title: String,
    fontSize: Int = 18,
    offset: IntOffset = IntOffset(0,0)
) {
    ColoredCircle(colors.back(), colors.info(), size, title, fontSize, offset)
}

@Composable
fun ColoredCircle(
    backColor: Color,
    textColor: Color,
    size: Int,
    title: String,
    fontSize: Int = 18,
    offset: IntOffset = IntOffset(0,0)
) {
    ColoredCircle(backColor, size, offset) {
        Text(
            text = title,
            color = textColor,
            fontSize = fontSize.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
        )
    }
}