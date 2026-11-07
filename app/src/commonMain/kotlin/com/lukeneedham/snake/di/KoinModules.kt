package com.lukeneedham.snake.di

import com.lukeneedham.common.DebugKit
import com.lukeneedham.snake.game.SnakeEngineFactory
import com.lukeneedham.snake.ui.GameViewModel
import com.lukeneedham.snake.ui.SettingsViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/** Every Koin module of the app. */
object KoinModules {
    private val app = module {
        single { SnakeEngineFactory.create() }
        viewModel { GameViewModel(get()) }
        viewModel { SettingsViewModel(isDebugEnabled = DebugKit.enabled) }
    }

    val modules: List<Module> = listOf(app)
}
