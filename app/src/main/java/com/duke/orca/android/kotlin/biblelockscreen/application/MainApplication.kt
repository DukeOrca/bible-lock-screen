package com.duke.orca.android.kotlin.biblelockscreen.application

import android.app.Application
import android.content.Context
import com.duke.orca.android.kotlin.biblelockscreen.BuildConfig
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    companion object {
        private lateinit var instance: MainApplication

        fun getApplicationContext(): Context = instance.applicationContext
    }
}