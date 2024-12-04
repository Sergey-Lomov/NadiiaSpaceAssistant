package com.sspirit.nadiiaspaceassistant.screens

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.services.dataproviders.CosmologyDataProvider
import com.sspirit.nadiiaspaceassistant.services.dataproviders.SpaceObject
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper

@Composable
fun SpaceObjectSelectionView(starSystemId: String?, nextRoute: Array<String>, navController: NavHostController) {
    ScreenWrapper(navController) {
        if (starSystemId == null) {
            Log.e("Navigation error", "StarSystem ID missed")
            return@ScreenWrapper
        }
        val starSystem = CosmologyDataProvider.spaceMap.firstOrNull { it.id == starSystemId }
        if (starSystem == null) {
            Log.e("Navigation error", "StarSystem ID invalid")
            return@ScreenWrapper
        }

        Column (
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(16.dp))
            for (spaceObject: SpaceObject in starSystem.objects) {
                Card(
                    modifier = Modifier.height(50.dp)
                ) {
                    Text(
                        text = spaceObject.title,
                        fontSize = 24.sp,
                        modifier = Modifier
                            .wrapContentSize()
                            .fillMaxSize()
                            .wrapContentHeight(align = CenterVertically),
                        textAlign = TextAlign.Center,
                    )
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}