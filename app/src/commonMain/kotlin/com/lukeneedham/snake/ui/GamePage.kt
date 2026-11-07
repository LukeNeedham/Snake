package com.lukeneedham.snake.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

/** The page: connects to the ViewModel and the navigation, and delegates all UI. No preview. */
@Composable
fun GamePage(onOpenSettings: () -> Unit) {
    val viewModel = koinViewModel<GameViewModel>()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LifecycleEventEffect(Lifecycle.Event.ON_PAUSE) { viewModel.onPause() }
    GamePageContent(
        state = state,
        onSwipe = viewModel::onSwipe,
        onTap = viewModel::onTap,
        onOpenSettings = {
            viewModel.onPause()
            onOpenSettings()
        },
    )
}
