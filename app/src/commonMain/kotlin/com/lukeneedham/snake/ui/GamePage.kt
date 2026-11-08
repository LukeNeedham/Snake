package com.lukeneedham.snake.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.withFrameNanos
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

/** The page: connects to the ViewModel and the navigation, runs the frame loop, and delegates all UI. No preview. */
@Composable
fun GamePage(onOpenSettings: () -> Unit) {
    val viewModel = koinViewModel<GameViewModel>()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // The game stops while the app is in the background
    LifecycleResumeEffect(viewModel) {
        onPauseOrDispose { viewModel.onPause() }
    }

    LaunchedEffect(viewModel) {
        var last = 0L
        while (true) {
            withFrameNanos { now ->
                if (last != 0L) viewModel.onFrame((now - last) / 1_000_000_000f)
                last = now
            }
        }
    }

    GamePageContent(
        state = state,
        onTap = viewModel::onTap,
        onSceneSize = viewModel::onSceneSize,
        onOpenSettings = onOpenSettings,
    )
}
