package com.sspirit.nadiiaspaceassistant.screens

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.services.dataproviders.CosmologyDataProvider
import com.sspirit.nadiiaspaceassistant.services.dataproviders.StarSystem
import com.sspirit.nadiiaspaceassistant.ui.LoadingIndicator
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val cardInRow = 2

@Composable
fun StarSystemSelectionView(navController: NavHostController) {
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val job = CoroutineScope(Dispatchers.IO).launch {
            try {
                CosmologyDataProvider.updateSpaceMap()
            } catch (e: Exception) {
                Log.e("Request error", e.toString())
            }
        }
        job.invokeOnCompletion {
            CoroutineScope(Dispatchers.Main).launch {
                loading = false
            }
        }
    }

    ScreenWrapper(navController) {
        if (loading) {
            LoadingIndicator()
        } else {
            MainContent(navController)
        }
    }
}

@Composable
private fun MainContent(navController: NavHostController) {
    Column (Modifier.verticalScroll(rememberScrollState())) {
        Spacer(Modifier.height(16.dp))

        val map = CosmologyDataProvider.spaceMap
        val rowsCount = (map.size + cardInRow - 1) / cardInRow
        for (row: Int in 0 until rowsCount) {
            Row(
                modifier = Modifier
                    .height(90.dp)
            ) {
                Spacer(Modifier.width(16.dp))
                for (index: Int in 0 until cardInRow) {
                    val star = map.elementAtOrNull(row * cardInRow + index)
                    if (star != null) {
                        Card (
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        ) {
                            StarColumn(star, navController)
                        }
                        Spacer(Modifier.width(16.dp))
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun StarColumn(star: StarSystem, navController: NavHostController) {
    Column {
        Spacer(Modifier.weight(1f))
        Text(
            text = star.id,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(CenterVertically)
                .wrapContentWidth(Alignment.CenterHorizontally)
        )
        Text(
            text = star.title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(CenterVertically)
                .wrapContentWidth(Alignment.CenterHorizontally)
        )
        Spacer(Modifier.weight(1f))
    }
}