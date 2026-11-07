package com.lukeneedham.snake.di

import com.lukeneedham.snake.ui.SettingsViewModel
import com.lukeneedham.common.DebugKit
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { SettingsViewModel(isDebugEnabled = DebugKit.enabled) }
}
