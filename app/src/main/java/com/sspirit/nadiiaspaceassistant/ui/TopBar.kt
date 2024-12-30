package com.sspirit.nadiiaspaceassistant.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun TopBar(navigator: NavHostController, title: String? = null) {
    NavigationBar(
        containerColor = Color.Transparent,
        modifier = Modifier.height(60.dp)
    ) {
        Box {
            if (title != null) {
                HeaderText(
                    text = title,
                    modifier = Modifier
                        .fillMaxHeight()
                        .wrapContentHeight(Alignment.CenterVertically)
                )
            }

            Row(
                Modifier.padding(16.dp)
            ) {
                if (navigator.previousBackStackEntry != null)
                    IconButton(
                        onClick = {
                            navigator.popBackStack()
                        },
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            "Back",
                        )
                    }
            }
        }
    }
}