package com.flowehealth.efr_version.application

import android.app.Application
import com.flowehealth.efr_version.bluetooth.parsing.Engine
import com.flowehealth.efr_version.BuildConfig
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import timber.log.Timber.DebugTree

@HiltAndroidApp
class SiliconLabsDemoApplication : Application() {
    companion object {
        lateinit var APP: SiliconLabsDemoApplication
    }

    init {
        APP = this
    }

    override fun onCreate() {
        super.onCreate()
        Engine.init(this)


        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
    }
}