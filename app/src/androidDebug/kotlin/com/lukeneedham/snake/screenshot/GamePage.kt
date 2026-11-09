package com.lukeneedham.snake.screenshot

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.lukeneedham.snake.game.Arena
import com.lukeneedham.snake.game.BirdKind
import com.lukeneedham.snake.game.BirdLanding
import com.lukeneedham.snake.game.Critter
import com.lukeneedham.snake.game.DeathCause
import com.lukeneedham.snake.game.GameWorld
import com.lukeneedham.snake.game.PausePage
import com.lukeneedham.snake.game.ToadKind
import com.lukeneedham.snake.game.Vec2
import com.lukeneedham.snake.ui.GamePageContent
import com.lukeneedham.snake.ui.GameUiState
import com.lukeneedham.snake.ui.theme.AppTheme
import kotlin.random.Random

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

/** Rendered by CI to docs/screenshots/<position in order.txt>-GamePageBirds.png. */
@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun GamePageBirdsScreenshot() {
    val world = remember {
        GameWorld(Arena(108f, 240f), seed = 7).also {
            it.start()
            repeat(720) { _ -> it.update(1f / 60f) }
            it.score = 7
            val head = Vec2(it.snake.x, it.snake.y)
            val random = Random(3)
            // A robin and a crow have landed away from the snake, and a blue tit is still flying in
            val robin = BirdLanding.spot(it.arena, random, head)
            it.addCritter(Critter.Bird(900, BirdKind.Robin, robin.x, robin.y, robin.x, robin.y, staySeconds = 60f, isLanded = true))
            val crow = BirdLanding.spot(it.arena, random, head)
            it.addCritter(Critter.Bird(901, BirdKind.Crow, crow.x, crow.y, crow.x, crow.y, staySeconds = 60f, isLanded = true))
            it.addCritter(Critter.Bird(902, BirdKind.BlueTit, 20f, 70f, 60f, 130f, staySeconds = 60f))
            // The snake has eaten a sparrow and a pigeon: two power ups run at once
            it.powerUps.apply(BirdKind.Sparrow)
            it.powerUps.apply(BirdKind.Pigeon)
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

/** Rendered by CI to docs/screenshots/<position in order.txt>-GamePageHunger.png. */
@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun GamePageHungerScreenshot() {
    val world = remember {
        GameWorld(Arena(108f, 240f), seed = 7).also {
            it.start()
            repeat(720) { _ -> it.update(1f / 60f) }
            it.score = 7
            it.openOverlay(PausePage.Hunger)
        }
    }
    val state = GameUiState(world = world, phase = world.phase, score = world.score, bestScore = 12, isNewBest = false)
    AppTheme { GamePageContent(state = state, onPress = { _, _ -> }, onRelease = {}, onSceneSize = { _, _ -> }, onOpenSettings = {}) }
}

/** Rendered by CI to docs/screenshots/<position in order.txt>-GamePageStatus.png. */
@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun GamePageStatusScreenshot() {
    val world = remember {
        GameWorld(Arena(108f, 240f), seed = 7).also {
            it.start()
            repeat(720) { _ -> it.update(1f / 60f) }
            it.score = 7
            it.applyEffect(ToadKind.Olive)
            it.applyEffect(ToadKind.Brown)
            it.powerUps.apply(BirdKind.BlueTit)
            repeat(120) { _ -> it.update(1f / 60f) }
            it.openOverlay(PausePage.Status)
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
            it.endGame(DeathCause.Starvation)
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
