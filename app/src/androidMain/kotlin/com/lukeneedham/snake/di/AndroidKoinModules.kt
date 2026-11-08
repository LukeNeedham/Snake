package com.lukeneedham.snake.di

import com.lukeneedham.snake.data.BestScoreStore
import com.lukeneedham.snake.data.DataStoreBestScoreStore
import com.lukeneedham.snake.data.DataStoreSettingsStore
import com.lukeneedham.snake.data.SettingsStore
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

/** What only Android can provide: [KoinModules] holds the rest. */
object AndroidKoinModules {

    private val android = module {
        single<BestScoreStore> { DataStoreBestScoreStore(androidContext()) }
        single<SettingsStore> { DataStoreSettingsStore(androidContext()) }
    }

    val modules: List<Module> = listOf(android)
}
