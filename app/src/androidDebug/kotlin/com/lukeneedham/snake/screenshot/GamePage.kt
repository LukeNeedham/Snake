package com.lukeneedham.snake.screenshot

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.lukeneedham.snake.game.Arena
import com.lukeneedham.snake.game.GameWorld
import com.lukeneedham.snake.game.ToadKind
import com.lukeneedham.snake.ui.GamePageContent
import com.lukeneedham.snake.ui.GameUiState
import com.lukeneedham.snake.ui.theme.AppTheme

/** Rendered by CI to docs/screenshots/<position in order.txt>-GamePageReady.png. */
@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun GamePageReadyScreenshot() {
    val world = remember {
        GameWorld(Arena(108f, 240f), seed = 7).also { repeat(150) { _ -> it.update(1f / 60f) } }
    }
    val state = GameUiState(world = world, phase = world.phase, score = 0, bestScore = 12, isNewBest = false)
    AppTheme { GamePageContent(state = state, onPress = { _, _ -> }, onRelease = {}, onSceneSize = { _, _ -> }, onOpenSettings = {}) }
}

/** Rendered by CI to docs/screenshots/<position in order.txt>-GamePage.png. */
@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun GamePageScreenshot() {
    val world = remember {
        GameWorld(Arena(108f, 240f), seed = 7).also {
            it.start()
            repeat(720) { _ -> it.update(1f / 60f) }
            it.score = 7
        }
    }
    val state = GameUiState(world = world, phase = world.phase, score = world.score, bestScore = 12, isNewBest = false)
    AppTheme { GamePageContent(state = state, onPress = { _, _ -> }, onRelease = {}, onSceneSize = { _, _ -> }, onOpenSettings = {}) }
}

/** Rendered by CI to docs/screenshots/<position in order.txt>-GamePageHallucinating.png. */
@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun GamePageHallucinatingScreenshot() {
    val world = remember {
        GameWorld(Arena(108f, 240f), seed = 7).also {
            it.start()
            repeat(720) { _ -> it.update(1f / 60f) }
            it.score = 7
            it.applyEffect(ToadKind.Purple)
            repeat(75) { _ -> it.update(1f / 60f) }
        }
    }
    val state = GameUiState(world = world, phase = world.phase, score = world.score, bestScore = 12, isNewBest = false)
    AppTheme { GamePageContent(state = state, onPress = { _, _ -> }, onRelease = {}, onSceneSize = { _, _ -> }, onOpenSettings = {}) }
}

/** Rendered by CI to docs/screenshots/<position in order.txt>-GamePageGameOver.png. */
@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun GamePageGameOverScreenshot() {
    val world = remember {
        GameWorld(Arena(108f, 240f), seed = 7).also {
            it.start()
            repeat(720) { _ -> it.update(1f / 60f) }
            it.score = 13
            it.endGame()
            repeat(75) { _ -> it.update(1f / 60f) }
        }
    }
    val state = GameUiState(world = world, phase = world.phase, score = world.score, bestScore = 13, isNewBest = true)
    AppTheme { GamePageContent(state = state, onPress = { _, _ -> }, onRelease = {}, onSceneSize = { _, _ -> }, onOpenSettings = {}) }
}

/** Rendered by CI to docs/screenshots/<position in order.txt>-GamePagePaused.png. */
@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun GamePagePausedScreenshot() {
    val world = remember {
        GameWorld(Arena(108f, 240f), seed = 7).also {
            it.start()
            repeat(720) { _ -> it.update(1f / 60f) }
            it.score = 7
            it.pause()
        }
    }
    val state = GameUiState(world = world, phase = world.phase, score = world.score, bestScore = 12, isNewBest = false)
    AppTheme { GamePageContent(state = state, onPress = { _, _ -> }, onRelease = {}, onSceneSize = { _, _ -> }, onOpenSettings = {}) }
}
