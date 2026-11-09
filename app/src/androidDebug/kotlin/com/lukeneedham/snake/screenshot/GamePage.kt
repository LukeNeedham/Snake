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
import com.lukeneedham.snake.game.IntroStage
import com.lukeneedham.snake.game.Opening
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
        GameWorld(Arena(108f, 240f), seed = 7, opening = Opening.Tutorial).also { repeat(170) { _ -> it.update(1f / 60f) } }
    }
    val state = GameUiState(world = world, phase = world.phase, score = 0, bestScore = 12, isNewBest = false)
    AppTheme { GamePageContent(state = state, onPress = { _, _ -> }, onRelease = {}, onSceneSize = { _, _ -> }, onOpenSettings = {}) }
}

/** Rendered by CI to docs/screenshots/<position in order.txt>-GamePageRumble.png. */
@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun GamePageRumbleScreenshot() {
    // The stomach of the snake rumbles in its den: zigzag lines along its body
    val world = remember {
        GameWorld(Arena(108f, 240f), seed = 7, opening = Opening.Tutorial).also {
            it.start()
            repeat(300) { _ -> it.update(1f / 60f) }
        }
    }
    val state = GameUiState(world = world, phase = world.phase, score = 0, bestScore = 12, isNewBest = false)
    AppTheme { GamePageContent(state = state, onPress = { _, _ -> }, onRelease = {}, onSceneSize = { _, _ -> }, onOpenSettings = {}) }
}

/** Rendered by CI to docs/screenshots/<position in order.txt>-GamePageThinking.png. */
@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun GamePageThinkingScreenshot() {
    // The snake thinks of a mouse: three bubbles, the last with the mouse in it
    val world = remember {
        GameWorld(Arena(108f, 240f), seed = 7, opening = Opening.Tutorial).also {
            it.start()
            repeat(504) { _ -> it.update(1f / 60f) }
        }
    }
    val state = GameUiState(world = world, phase = world.phase, score = 0, bestScore = 12, isNewBest = false)
    AppTheme { GamePageContent(state = state, onPress = { _, _ -> }, onRelease = {}, onSceneSize = { _, _ -> }, onOpenSettings = {}) }
}

/** Rendered by CI to docs/screenshots/<position in order.txt>-GamePageLesson.png. */
@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun GamePageLessonScreenshot() {
    // The bubble has popped, the snake turns, and a text tells the player to tap
    val world = remember {
        GameWorld(Arena(108f, 240f), seed = 7, opening = Opening.Tutorial).also {
            it.start()
            repeat(660) { _ -> it.update(1f / 60f) }
        }
    }
    val state = GameUiState(world = world, phase = world.phase, score = 0, bestScore = 12, isNewBest = false)
    AppTheme { GamePageContent(state = state, onPress = { _, _ -> }, onRelease = {}, onSceneSize = { _, _ -> }, onOpenSettings = {}) }
}

/** Rendered by CI to docs/screenshots/<position in order.txt>-GamePageSteering.png. */
@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun GamePageSteeringScreenshot() {
    // The path up is blocked by stones, and an arrow points to the path to the left
    val world = remember {
        GameWorld(Arena(108f, 240f), seed = 7, opening = Opening.Tutorial).also {
            it.start()
            // The story is over and the snake has turned: the stones block the path up, and an arrow shows the way to the left
            repeat(660) { _ -> it.update(1f / 60f) }
            it.tap()
            repeat(30) { _ -> it.update(1f / 60f) }
        }
    }
    val state = GameUiState(world = world, phase = world.phase, score = 0, bestScore = 12, isNewBest = false)
    AppTheme { GamePageContent(state = state, onPress = { _, _ -> }, onRelease = {}, onSceneSize = { _, _ -> }, onOpenSettings = {}) }
}

/** Rendered by CI to docs/screenshots/<position in order.txt>-GameTutorialHunger.png. */
@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun GameTutorialHungerScreenshot() {
    // The hunger bar shows for the first time, with an arrow and the words that the belly decreases, and it runs down
    val world = remember {
        GameWorld(Arena(108f, 240f), seed = 7, opening = Opening.Tutorial).also {
            it.start()
            it.jumpToLesson(IntroStage.Hunger)
            repeat(90) { _ -> it.update(1f / 60f) }
        }
    }
    val state = GameUiState(world = world, phase = world.phase, score = 0, bestScore = 12, isNewBest = false)
    AppTheme { GamePageContent(state = state, onPress = { _, _ -> }, onRelease = {}, onSceneSize = { _, _ -> }, onOpenSettings = {}) }
}

/** Rendered by CI to docs/screenshots/<position in order.txt>-GameTutorialZoom.png. */
@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun GameTutorialZoomScreenshot() {
    // The sprint button shows, with an arrow and the words that say to hold it
    val world = remember {
        GameWorld(Arena(108f, 240f), seed = 7, opening = Opening.Tutorial).also {
            it.start()
            it.jumpToLesson(IntroStage.Sprint)
            repeat(60) { _ -> it.update(1f / 60f) }
        }
    }
    val state = GameUiState(world = world, phase = world.phase, score = 0, bestScore = 12, isNewBest = false)
    AppTheme { GamePageContent(state = state, onPress = { _, _ -> }, onRelease = {}, onSceneSize = { _, _ -> }, onOpenSettings = {}) }
}

/** Rendered by CI to docs/screenshots/<position in order.txt>-GameTutorialMouse.png. */
@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun GameTutorialMouseScreenshot() {
    // The words say to eat so as not to starve, and a mouse has run into the middle of the room
    val world = remember {
        GameWorld(Arena(108f, 240f), seed = 7, opening = Opening.Tutorial).also {
            it.start()
            it.jumpToLesson(IntroStage.Mouse)
            repeat(330) { _ -> it.update(1f / 60f) }
        }
    }
    val state = GameUiState(world = world, phase = world.phase, score = 0, bestScore = 12, isNewBest = false)
    AppTheme { GamePageContent(state = state, onPress = { _, _ -> }, onRelease = {}, onSceneSize = { _, _ -> }, onOpenSettings = {}) }
}

/** Rendered by CI to docs/screenshots/<position in order.txt>-GameTutorialToad.png. */
@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun GameTutorialToadScreenshot() {
    // The words say that toads can be eaten, and the pink toad has hopped to the middle of the room
    val world = remember {
        GameWorld(Arena(108f, 240f), seed = 7, opening = Opening.Tutorial).also {
            it.start()
            it.jumpToLesson(IntroStage.Toad)
            repeat(600) { _ -> it.update(1f / 60f) }
        }
    }
    val state = GameUiState(world = world, phase = world.phase, score = 0, bestScore = 12, isNewBest = false)
    AppTheme { GamePageContent(state = state, onPress = { _, _ -> }, onRelease = {}, onSceneSize = { _, _ -> }, onOpenSettings = {}) }
}

/** Rendered by CI to docs/screenshots/<position in order.txt>-GameTutorialBird.png. */
@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun GameTutorialBirdScreenshot() {
    // The words say to catch birds, and a bird has flown in to the middle of the room
    val world = remember {
        GameWorld(Arena(108f, 240f), seed = 7, opening = Opening.Tutorial).also {
            it.start()
            it.jumpToLesson(IntroStage.Bird)
            repeat(420) { _ -> it.update(1f / 60f) }
        }
    }
    val state = GameUiState(world = world, phase = world.phase, score = 0, bestScore = 12, isNewBest = false)
    AppTheme { GamePageContent(state = state, onPress = { _, _ -> }, onRelease = {}, onSceneSize = { _, _ -> }, onOpenSettings = {}) }
}

/** Rendered by CI to docs/screenshots/<position in order.txt>-GameTutorialWaking.png. */
@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun GameTutorialWakingScreenshot() {
    // The snake wakes up, with its eyes open after the first blink
    val world = remember {
        GameWorld(Arena(108f, 240f), seed = 7, opening = Opening.Tutorial).also {
            it.start()
            repeat(60) { _ -> it.update(1f / 60f) }
        }
    }
    val state = GameUiState(world = world, phase = world.phase, score = 0, bestScore = 12, isNewBest = false)
    AppTheme { GamePageContent(state = state, onPress = { _, _ -> }, onRelease = {}, onSceneSize = { _, _ -> }, onOpenSettings = {}) }
}

/** Rendered by CI to docs/screenshots/<position in order.txt>-GameTutorialTasting.png. */
@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun GameTutorialTastingScreenshot() {
    // The snake sticks its tongue out
    val world = remember {
        GameWorld(Arena(108f, 240f), seed = 7, opening = Opening.Tutorial).also {
            it.start()
            repeat(138) { _ -> it.update(1f / 60f) }
        }
    }
    val state = GameUiState(world = world, phase = world.phase, score = 0, bestScore = 12, isNewBest = false)
    AppTheme { GamePageContent(state = state, onPress = { _, _ -> }, onRelease = {}, onSceneSize = { _, _ -> }, onOpenSettings = {}) }
}

/** Rendered by CI to docs/screenshots/<position in order.txt>-GameTutorialPopped.png. */
@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun GameTutorialPoppedScreenshot() {
    // The big bubble pops, and the snake starts to turn
    val world = remember {
        GameWorld(Arena(108f, 240f), seed = 7, opening = Opening.Tutorial).also {
            it.start()
            repeat(570) { _ -> it.update(1f / 60f) }
        }
    }
    val state = GameUiState(world = world, phase = world.phase, score = 0, bestScore = 12, isNewBest = false)
    AppTheme { GamePageContent(state = state, onPress = { _, _ -> }, onRelease = {}, onSceneSize = { _, _ -> }, onOpenSettings = {}) }
}

/** Rendered by CI to docs/screenshots/<position in order.txt>-GameTutorialZoomed.png. */
@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun GameTutorialZoomedScreenshot() {
    // The snake has sprinted for long enough, and the words say that zooming makes it hungry
    val world = remember {
        GameWorld(Arena(108f, 240f), seed = 7, opening = Opening.Tutorial).also {
            it.start()
            it.jumpToLesson(IntroStage.Sprint)
            it.introPress(7L, isInSprintZone = true)
            repeat(135) { _ -> it.update(1f / 60f) }
        }
    }
    val state = GameUiState(world = world, phase = world.phase, score = 0, bestScore = 12, isNewBest = false)
    AppTheme { GamePageContent(state = state, onPress = { _, _ -> }, onRelease = {}, onSceneSize = { _, _ -> }, onOpenSettings = {}) }
}

/** Rendered by CI to docs/screenshots/<position in order.txt>-GameTutorialDizzy.png. */
@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun GameTutorialDizzyScreenshot() {
    // The snake has eaten the pink toad: it is dizzy, and the words say that toads poison
    val world = remember {
        GameWorld(Arena(108f, 240f), seed = 7, opening = Opening.Tutorial).also {
            it.start()
            it.jumpToLesson(IntroStage.Dizzy)
            it.applyEffect(ToadKind.Pink)
            repeat(30) { _ -> it.update(1f / 60f) }
        }
    }
    val state = GameUiState(world = world, phase = world.phase, score = 0, bestScore = 12, isNewBest = false)
    AppTheme { GamePageContent(state = state, onPress = { _, _ -> }, onRelease = {}, onSceneSize = { _, _ -> }, onOpenSettings = {}) }
}

/** Rendered by CI to docs/screenshots/<position in order.txt>-GameTutorialPowers.png. */
@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun GameTutorialPowersScreenshot() {
    // The snake has eaten the bird: it has the power, and the words say that birds give powers
    val world = remember {
        GameWorld(Arena(108f, 240f), seed = 7, opening = Opening.Tutorial).also {
            it.start()
            it.jumpToLesson(IntroStage.Powers)
            it.powerUps.apply(BirdKind.BlueTit)
            repeat(30) { _ -> it.update(1f / 60f) }
        }
    }
    val state = GameUiState(world = world, phase = world.phase, score = 0, bestScore = 12, isNewBest = false)
    AppTheme { GamePageContent(state = state, onPress = { _, _ -> }, onRelease = {}, onSceneSize = { _, _ -> }, onOpenSettings = {}) }
}

/** Rendered by CI to docs/screenshots/<position in order.txt>-GameTutorialReturn.png. */
@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun GameTutorialReturnScreenshot() {
    // All is learned: an arrow shows the way back to the house
    val world = remember {
        GameWorld(Arena(108f, 240f), seed = 7, opening = Opening.Tutorial).also {
            it.start()
            it.jumpToLesson(IntroStage.Return)
            repeat(30) { _ -> it.update(1f / 60f) }
        }
    }
    val state = GameUiState(world = world, phase = world.phase, score = 0, bestScore = 12, isNewBest = false)
    AppTheme { GamePageContent(state = state, onPress = { _, _ -> }, onRelease = {}, onSceneSize = { _, _ -> }, onOpenSettings = {}) }
}

/** Rendered by CI to docs/screenshots/<position in order.txt>-GamePageLeaving.png. */
@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun GamePageLeavingScreenshot() {
    // Back in the house the stones are gone, and an arrow points up the path to the stage
    val world = remember {
        GameWorld(Arena(108f, 240f), seed = 7, opening = Opening.Tutorial).also {
            it.start()
            it.jumpToLesson(IntroStage.Leaving)
            repeat(30) { _ -> it.update(1f / 60f) }
        }
    }
    val state = GameUiState(world = world, phase = world.phase, score = 0, bestScore = 12, isNewBest = false)
    AppTheme { GamePageContent(state = state, onPress = { _, _ -> }, onRelease = {}, onSceneSize = { _, _ -> }, onOpenSettings = {}) }
}

/** Rendered by CI to docs/screenshots/<position in order.txt>-GamePageReturning.png. */
@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun GamePageReturningScreenshot() {
    // After the tutorial: the path up is open, with no stones
    val world = remember {
        GameWorld(Arena(108f, 240f), seed = 7, opening = Opening.Returning).also { repeat(170) { _ -> it.update(1f / 60f) } }
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
