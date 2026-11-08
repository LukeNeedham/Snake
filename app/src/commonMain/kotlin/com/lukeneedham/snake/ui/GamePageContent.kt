package com.lukeneedham.snake.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import com.lukeneedham.snake.game.Arena
import com.lukeneedham.snake.game.GamePhase
import com.lukeneedham.snake.game.GameWorld
import com.lukeneedham.snake.render.GrassPalette
import com.lukeneedham.snake.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * All of the page's UI: the game scene, with a button to the settings over it on the title and game over screens.
 * `internal`: the screenshots of the app use it too.
 */
@Composable
internal fun GamePageContent(
    state: GameUiState,
    onTap: () -> Unit,
    onSceneSize: (widthPx: Int, heightPx: Int) -> Unit,
    onOpenSettings: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GrassPalette.base)
            .onSizeChanged { onSceneSize(it.width, it.height) },
    ) {
        GameScene(
            world = state.world,
            texts = sceneTexts(score = state.score, bestScore = state.bestScore, isNewBest = state.isNewBest),
            onTap = onTap,
        )
        if (state.phase == GamePhase.Ready || state.phase == GamePhase.GameOver) {
            SettingsButton(
                onClick = onOpenSettings,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .windowInsetsPadding(WindowInsets.safeDrawing)
                    .padding(8.dp),
            )
        }
    }
}

/** For Android Studio while building the page: the title screen. */
@Preview
@Composable
private fun Preview() {
    val world = GameWorld(Arena(108f, 240f))
    AppTheme {
        GamePageContent(
            state = GameUiState(world = world, phase = world.phase, score = 0, bestScore = 0, isNewBest = false),
            onTap = {},
            onSceneSize = { _, _ -> },
            onOpenSettings = {},
        )
    }
}
