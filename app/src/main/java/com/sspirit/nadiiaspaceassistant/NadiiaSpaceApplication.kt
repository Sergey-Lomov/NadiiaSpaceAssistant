package com.sspirit.nadiiaspaceassistant

import android.app.Application
import android.content.Context

class NadiiaSpaceApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        private lateinit var instance: NadiiaSpaceApplication

        fun getContext(): Context {
            return instance.applicationContext
        }
    }
}