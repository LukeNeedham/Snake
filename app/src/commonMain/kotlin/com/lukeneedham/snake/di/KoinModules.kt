package com.lukeneedham.snake.di

import com.lukeneedham.snake.ui.GameViewModel
import com.lukeneedham.snake.ui.SettingsViewModel
import com.lukeneedham.common.DebugKit
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/** All of the app's Koin modules: `App` starts Koin with [modules]. A feature with many bindings gets an object of its own that this one lists. */
object KoinModules {

    private val app = module {
        viewModel { GameViewModel(get(), get()) }
        viewModel { SettingsViewModel(isDebugEnabled = DebugKit.enabled, settingsStore = get()) }
    }

    val modules: List<Module> = listOf(app)
}
