package com.mnishiguchi.criminalrecorder.ui

import android.app.Application
import com.mnishiguchi.criminalrecorder.data.AppDatabase
import com.mnishiguchi.criminalrecorder.util.mediumDateFormat

/**
 * An application singleton that allows us to have an easier access to the application context.
 * Make sure that this class is registered in AndroidManifest.xml so that we can use it in the app.
 */
class App : Application() {
    companion object {
        lateinit var instance: App
            private set
        val database: AppDatabase by lazy { AppDatabase.createPersistentDatabase(instance) }
        val mediumDateFormat: java.text.DateFormat by lazy { instance.mediumDateFormat() }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}