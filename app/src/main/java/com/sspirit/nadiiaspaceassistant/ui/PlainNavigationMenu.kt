package com.sspirit.nadiiaspaceassistant.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.extensions.navigateTo
import com.sspirit.nadiiaspaceassistant.navigation.Routes

data class PlainMenuItem(
    val title: String,
    val route: Routes,
    val dataGenerator: (() -> String)? = null
)

@Composable
fun PlainNavigationMenu(items: Array<PlainMenuItem>, navController: NavHostController) {
    ScreenWrapper(navController) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = CenterHorizontally,
        ) {
            for (item: PlainMenuItem in items) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    onClick = {
                        if (item.dataGenerator != null) {
                            val data = item.dataGenerator.invoke()
                            navController.navigateTo(item.route, data)
                        } else {
                            navController.navigateTo(item.route)
                        }
                    },
                ) {
                    Text(
                        text = item.title,
                        fontSize = 32.sp,
                        modifier = Modifier
                            .wrapContentSize()
                            .fillMaxSize()
                            .wrapContentHeight(align = CenterVertically),
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}
