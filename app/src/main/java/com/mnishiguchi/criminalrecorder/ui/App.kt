package com.mnishiguchi.criminalrecorder.ui

import android.app.Application

/**
 * An application singleton that allows us to have an easier access to the application context.
 * Make sure that this class is registered in AndroidManifest.xml so that we can use it in the app.
 */
class App : Application() {
    companion object {
        lateinit var instance: App
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}