package com.sspirit.nadiiaspaceassistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import com.sspirit.nadiiaspaceassistant.navigation.Navigation
import com.sspirit.nadiiaspaceassistant.navigation.NavigationHandler
import com.sspirit.nadiiaspaceassistant.screens.NotificationsView
import com.sspirit.nadiiaspaceassistant.ui.theme.NadiiaSpaceAssistantTheme

class MainActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NavigationHandler.setup()
        setContent {
            NadiiaSpaceAssistantTheme {
                Box {
                    Navigation()
                    NotificationsView()
                }
            }
        }
    }
}