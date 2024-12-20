package com.sspirit.nadiiaspaceassistant.screens.cosmonavigation

import android.media.MediaPlayer
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.NadiiaSpaceApplication
import com.sspirit.nadiiaspaceassistant.R
import com.sspirit.nadiiaspaceassistant.navigation.Routes
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper
import com.sspirit.nadiiaspaceassistant.ui.StyledButton
import com.sspirit.nadiiaspaceassistant.ui.TimerView

val CosmonavigationSuccessLambda = compositionLocalOf<(() -> Unit)?> {null}

const val reactronEffect = 1.2f
const val superReactronEffect = 1.33f

@Composable
fun CosmonavigationTaskExecutionView(time: Float, navController: NavHostController) {
    val timeleft = remember { mutableFloatStateOf(time) }
    val isRunning = remember { mutableStateOf(false) }
    val isReactronAvailable = remember { mutableStateOf(true) }
    val isSuperReactronAvailable = remember { mutableStateOf(true) }

    LaunchedEffect(isRunning.value) {
        if (!isRunning.value && timeleft.floatValue <= 0) {
            Log.d("Test", "Call sound")
            playSound()
        }
    }

    ScreenWrapper(navController) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            TimerView(
                timeleft = timeleft,
                isRunning = isRunning,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            )
            Spacer(Modifier.height(32.dp))
            MedsButtons(timeleft, isReactronAvailable, isSuperReactronAvailable)
            Spacer(Modifier.height(16.dp))
            ResultsButtons(navController)
        }
    }
}

fun playSound()  {
    val context = NadiiaSpaceApplication.getContext()
    val mediaPlayer = MediaPlayer.create(context, R.raw.time_finish)
    mediaPlayer.start()
    mediaPlayer.setOnCompletionListener {
        mediaPlayer.release()
    }
}

@Composable
fun MedsButtons(
    timeleft: MutableFloatState,
    isReactronAvailable: MutableState<Boolean>,
    isSuperReactronAvailable: MutableState<Boolean>
) {
    Row {
        StyledButton(
            title = "+20%",
            enabled = isReactronAvailable.value,
            modifier = Modifier.weight(1f),
        ) {
            isReactronAvailable.value = false
            timeleft.value *= reactronEffect
        }

        Spacer(Modifier.width(8.dp))

        StyledButton(
            title = "+33%",
            enabled = isSuperReactronAvailable.value,
            modifier = Modifier.weight(1f),
        ) {
            if (!isReactronAvailable.value)
                timeleft.value /= reactronEffect
            isReactronAvailable.value = false
            isSuperReactronAvailable.value = false
            timeleft.value *= superReactronEffect
        }
    }
}

@Composable
fun ResultsButtons(navController: NavHostController) {
    val successLambda = CosmonavigationSuccessLambda.current
    Row {
        StyledButton(
            title = "Успех",
            modifier = Modifier.weight(1f),
        ) {
            if (successLambda != null)
                successLambda.invoke()
            else {
//                navController.navigate(Routes.Main.route) {
//                    popUpTo(Routes.Main.route) { inclusive = true }
//                }

                navController.popBackStack()
                navController.popBackStack()
            }
        }

        Spacer(Modifier.width(8.dp))

        StyledButton(
            title = "Провал",
            modifier = Modifier.weight(1f),
        ) {
            navController.popBackStack()
        }
    }
}