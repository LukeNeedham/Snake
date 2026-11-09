package com.lukeneedham.snake.game

import kotlin.math.atan2
import kotlin.math.sin
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GameWorldIntroTest {

    private fun newWorld() = GameWorld(Arena(108f, 234f), seed = 5, hasIntro = true)

    private fun GameWorld.play(seconds: Float) {
        repeat((seconds * 60f).toInt()) { update(1f / 60f) }
    }

    private fun GameWorld.playTheStory() = play(IntroTuning.sleepSeconds + IntroTuning.rumbleSeconds + IntroTuning.thinkSeconds + IntroTuning.popSeconds + 0.2f)

    @Test
    fun theSnakeStartsCurledUpInItsDenUnderTheOval() {
        val world = newWorld()
        assertEquals(GamePhase.Ready, world.phase)
        assertTrue(world.den.den.signedDistance(world.snake.x, world.snake.y) < 0f)
        assertEquals(world.den.introTop, world.cameraY)
        assertTrue(world.snake.y > world.arena.height)
    }

    @Test
    fun aWorldWithoutAnIntroStartsInTheOval() {
        val world = GameWorld(Arena(108f, 234f), seed = 5)
        assertNull(world.intro)
        assertEquals(0f, world.cameraY)
        world.start()
        assertEquals(GamePhase.Playing, world.phase)
    }

    @Test
    fun startingPlaysTheStoryInsteadOfTheGame() {
        val world = newWorld()
        world.start()
        assertEquals(GamePhase.Intro, world.phase)
        assertNotNull(world.intro)
    }

    @Test
    fun theSnakeStaysInItsDenWhileTheStoryPlays() {
        val world = newWorld()
        world.start()
        world.playTheStory()
        assertEquals(IntroStage.Teaching, world.intro!!.stage)
        assertTrue(world.den.den.signedDistance(world.snake.x, world.snake.y) < 0f)
        assertEquals(world.den.introTop, world.cameraY)
    }

    @Test
    fun aTapWhileTheSnakeSleepsDoesNothing() {
        val world = newWorld()
        world.start()
        val before = world.snake.turnDirection
        world.tap()
        assertEquals(before, world.snake.turnDirection)
        assertEquals(IntroStage.Sleeping, world.intro!!.stage)
    }

    @Test
    fun aTapDuringTheStorySkipsToTheLesson() {
        val world = newWorld()
        world.start()
        world.play(IntroTuning.sleepSeconds + 0.5f)
        val before = world.snake.turnDirection
        world.tap()
        assertEquals(IntroStage.Teaching, world.intro!!.stage)
        assertEquals(before, world.snake.turnDirection)
    }

    @Test
    fun theFirstTapOfTheLessonTurnsTheSnakeAndHandsOverToThePlayer() {
        val world = newWorld()
        world.start()
        world.playTheStory()
        val before = world.snake.turnDirection
        world.tap()
        assertEquals(-before, world.snake.turnDirection)
        assertEquals(IntroStage.Steering, world.intro!!.stage)
    }

    @Test
    fun theMenuPausesTheStoryAndResumingGoesBackToIt() {
        val world = newWorld()
        world.start()
        world.openMenu()
        assertEquals(GamePhase.Paused, world.phase)
        assertTrue(world.isIntro)
        world.closeMenu()
        assertEquals(GamePhase.Intro, world.phase)
    }

    @Test
    fun theSnakeNeverLeavesTheGroundWhileTheStoryPlays() {
        val world = newWorld()
        world.start()
        world.playTheStory()
        repeat(40) {
            world.tap()
            world.play(0.4f)
            if (world.phase == GamePhase.Intro) {
                assertTrue(world.den.ground.signedDistance(world.snake.x, world.snake.y) < 0f)
            }
        }
    }

    /** Taps whenever the snake turns away from straight up the path. */
    private fun GameWorld.steerUp() {
        val wanted = atan2(-20f, arena.centerX - snake.x)
        val direction = if (sin(wanted - snake.heading) >= 0f) 1f else -1f
        if (direction != snake.turnDirection) tap()
    }

    @Test
    fun steeringOutOfTheDenAlongThePathLeadsIntoTheOval() {
        val world = newWorld()
        world.start()
        world.playTheStory()
        world.tap()
        var seconds = 0f
        while (world.phase == GamePhase.Intro && seconds < 60f) {
            world.steerUp()
            world.update(1f / 60f)
            seconds += 1f / 60f
        }
        assertEquals(GamePhase.Playing, world.phase)
        assertTrue(world.arena.signedDistance(world.snake.x, world.snake.y) < 0f)
        world.play(3f)
        assertEquals(0f, world.cameraY)
        assertFalse(world.phase == GamePhase.Intro)
    }
}
