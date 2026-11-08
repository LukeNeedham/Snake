package com.lukeneedham.snake

import android.app.Application
import com.lukeneedham.snake.di.AndroidKoinModules
import com.lukeneedham.snake.di.KoinModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        // First, so that the logger and the crash handler are there for everything after it
        initDebugKit(BuildConfig.DEBUG)
        startKoin {
            androidContext(this@App)
            modules(KoinModules.modules + AndroidKoinModules.modules)
        }
    }
}
