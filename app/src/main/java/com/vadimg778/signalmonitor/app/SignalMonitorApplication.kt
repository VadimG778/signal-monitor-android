package com.vadimg778.signalmonitor.app

import android.app.Application
import com.vadimg778.signalmonitor.app.di.applicationModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class SignalMonitorApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@SignalMonitorApplication)
            modules(applicationModule)
        }
    }
}
