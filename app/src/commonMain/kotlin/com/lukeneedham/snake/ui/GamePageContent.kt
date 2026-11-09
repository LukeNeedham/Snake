package com.lukeneedham.snake.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import com.lukeneedham.snake.game.Arena
import com.lukeneedham.snake.game.GameWorld
import com.lukeneedham.snake.game.MenuItem
import com.lukeneedham.snake.game.PressTarget
import com.lukeneedham.snake.render.GrassPalette
import com.lukeneedham.snake.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * All of the page's UI: the game scene, which draws its own buttons in pixel art. A press on the settings button of the menu
 * opens the settings page. `internal`: the screenshots of the app use it too.
 */
@Composable
internal fun GamePageContent(
    state: GameUiState,
    onPress: (pointerId: Long, target: PressTarget) -> Unit,
    onRelease: (pointerId: Long) -> Unit,
    onSceneSize: (widthPx: Int, heightPx: Int) -> Unit,
    onOpenSettings: () -> Unit,
    onPageDrag: (pages: Float) -> Unit = {},
    onPageRelease: () -> Unit = {},
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
            onPress = { pointerId, target ->
                if (target == PressTarget.Item(MenuItem.Settings)) onOpenSettings() else onPress(pointerId, target)
            },
            onRelease = onRelease,
            onPageDrag = onPageDrag,
            onPageRelease = onPageRelease,
        )
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
            onPress = { _, _ -> },
            onRelease = {},
            onSceneSize = { _, _ -> },
            onOpenSettings = {},
        )
    }
}
