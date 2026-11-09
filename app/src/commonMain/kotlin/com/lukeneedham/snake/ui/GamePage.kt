package com.lukeneedham.snake.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.backhandler.BackHandler
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

/** The page: connects to the ViewModel and the navigation, runs the frame loop, and delegates all UI. No preview. */
@OptIn(ExperimentalComposeUiApi::class)
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

    // With the pause overlay open, back closes it and resumes the game instead of leaving the app
    BackHandler(enabled = state.isMenuOpen) { viewModel.onBack() }

    GamePageContent(
        state = state,
        onPress = viewModel::onPress,
        onRelease = viewModel::onRelease,
        onPageDrag = viewModel::onPageDrag,
        onPageRelease = viewModel::onPageRelease,
        onSceneSize = viewModel::onSceneSize,
        onOpenSettings = onOpenSettings,
    )
}
