package com.sspirit.nadiiaspaceassistant

import android.app.Application
import android.content.Context
import com.sspirit.nadiiaspaceassistant.services.external_monitor.LocalServerManager

class NadiiaSpaceApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
        LocalServerManager.startServer()
    }

    companion object {
        private lateinit var instance: NadiiaSpaceApplication

        fun getContext(): Context {
            return instance.applicationContext
        }
    }
}