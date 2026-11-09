package com.lukeneedham.snake.game

import kotlin.math.atan2
import kotlin.math.sin
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GameWorldIntroTest {

    private fun newWorld() = GameWorld(Arena(108f, 234f), seed = 5, hasIntro = true)

    private fun GameWorld.play(seconds: Float) {
        repeat((seconds * 60f).toInt()) { update(1f / 60f) }
    }

    private fun GameWorld.playTheStory() =
        play(IntroTuning.wakeSeconds + IntroTuning.tasteSeconds + IntroTuning.rumbleSeconds + IntroTuning.thinkSeconds + IntroTuning.popSeconds + 0.2f)

    /** Taps whenever the snake turns away from the way to [targetX], [targetY] (which is a place on the world, higher up or lower down). */
    private fun GameWorld.steerToward(targetX: Float, targetY: Float) {
        val wanted = atan2(targetY - snake.y, targetX - snake.x)
        val direction = if (sin(wanted - snake.heading) >= 0f) 1f else -1f
        if (direction != snake.turnDirection) tap()
    }

    private fun GameWorld.steerUpThePath() = steerToward(arena.centerX, snake.y - 30f)

    @Test
    fun theSnakeStartsAsleepInItsDenUnderTheOval() {
        val world = newWorld()
        assertEquals(GamePhase.Ready, world.phase)
        assertTrue(world.den.den.signedDistance(world.snake.x, world.snake.y) < 0f)
        assertEquals(world.den.denCameraY, world.cameraY)
        assertTrue(world.snake.y > world.arena.height * 2f)
        assertTrue(world.intro!!.isAsleep)
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
    fun theSnakeStaysAsleepUntilItIsWokenWithATap() {
        val world = newWorld()
        world.play(10f)
        assertEquals(IntroStage.Sleeping, world.intro!!.stage)
        world.start()
        assertEquals(GamePhase.Intro, world.phase)
        assertEquals(IntroStage.Waking, world.intro!!.stage)
    }

    @Test
    fun theSnakeLiesStillWhileTheStoryPlays() {
        val world = newWorld()
        val x = world.snake.x
        val y = world.snake.y
        world.start()
        world.play(IntroTuning.wakeSeconds + IntroTuning.tasteSeconds + IntroTuning.rumbleSeconds)
        assertEquals(x, world.snake.x)
        assertEquals(y, world.snake.y)
    }

    @Test
    fun aTapDuringTheStoryDoesNothing() {
        val world = newWorld()
        world.start()
        world.play(1f)
        val before = world.snake.turnDirection
        val stage = world.intro!!.stage
        world.tap()
        world.introPress(1L, isInSprintZone = false)
        assertEquals(before, world.snake.turnDirection)
        assertEquals(stage, world.intro!!.stage)
    }

    @Test
    fun theSnakeTurnsClockwiseOnceTheBubbleHasPopped() {
        val world = newWorld()
        world.start()
        world.playTheStory()
        assertEquals(IntroStage.Teaching, world.intro!!.stage)
        assertEquals(1f, world.snake.turnDirection)
        assertTrue(world.den.den.signedDistance(world.snake.x, world.snake.y) < 0f)
        assertEquals(world.den.denCameraY, world.cameraY)
    }

    @Test
    fun theFirstTapOfTheLessonTurnsTheSnakeAndHandsOverToThePlayer() {
        val world = newWorld()
        world.start()
        world.playTheStory()
        val before = world.snake.turnDirection
        world.introPress(1L, isInSprintZone = false)
        assertEquals(-before, world.snake.turnDirection)
        assertEquals(IntroStage.Steering, world.intro!!.stage)
    }

    @Test
    fun theSprintCornerTurnsTheSnakeUntilTheLessonOfTheSprint() {
        val world = newWorld()
        world.start()
        world.playTheStory()
        val before = world.snake.turnDirection
        world.introPress(1L, isInSprintZone = true)
        assertEquals(-before, world.snake.turnDirection)
        assertFalse(world.sprint.isHeld)
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
    fun theSnakeNeverLeavesTheGround() {
        val world = newWorld()
        world.start()
        world.playTheStory()
        repeat(40) {
            world.tap()
            world.play(0.4f)
            assertTrue(world.den.ground.signedDistance(world.snake.x, world.snake.y) < 0f)
        }
    }

    @Test
    fun theSnakeCanLoopInsideTheDenWithoutLeavingIt() {
        val world = newWorld()
        world.start()
        world.playTheStory()
        world.tap()
        // It circles with a radius that fits, and bounces off the wall and turns the other way when it reaches it
        repeat(20) {
            world.play(0.25f)
            if (world.snake.y > world.den.denY - DenTuning.denRadius) {
                assertTrue(world.den.den.signedDistance(world.snake.x, world.snake.y) < 0f)
            }
        }
    }

    @Test
    fun thePathGoesOnAndOnUntilThePlayerHasHeldTheSprintCorner() {
        val world = newWorld()
        world.start()
        world.playTheStory()
        world.tap()
        var seconds = 0f
        while (!world.intro!!.reachedHalfway && seconds < 60f) {
            world.steerUpThePath()
            world.update(1f / 60f)
            seconds += 1f / 60f
        }
        assertTrue(world.intro!!.reachedHalfway)
        // Without sprinting it never reaches the oval, however long it goes on
        repeat(60 * 40) {
            world.steerUpThePath()
            world.update(1f / 60f)
            assertEquals(GamePhase.Intro, world.phase)
            assertTrue(world.snake.y > world.arena.height)
        }
        // A finger held in the sprint corner for long enough gives the path an end
        world.introPress(7L, isInSprintZone = true)
        seconds = 0f
        while (world.phase == GamePhase.Intro && seconds < 60f) {
            world.steerUpThePath()
            world.update(1f / 60f)
            seconds += 1f / 60f
        }
        assertTrue(world.intro!!.hasZoomed)
        assertEquals(GamePhase.Playing, world.phase)
        assertTrue(world.arena.signedDistance(world.snake.x, world.snake.y) < 0f)
        world.sprintRelease(7L)
        world.play(3f)
        assertEquals(0f, world.cameraY)
    }

    @Test
    fun letGoOfTheSprintCornerTooSoonAndThePathStillGoesOn() {
        val world = newWorld()
        world.start()
        world.playTheStory()
        world.tap()
        while (!world.intro!!.reachedHalfway) {
            world.steerUpThePath()
            world.update(1f / 60f)
        }
        repeat(5) {
            world.introPress(7L, isInSprintZone = true)
            repeat((IntroTuning.zoomSeconds * 60f * 0.7f).toInt()) {
                world.steerUpThePath()
                world.update(1f / 60f)
            }
            world.sprintRelease(7L)
            world.play(0.3f)
        }
        assertFalse(world.intro!!.hasZoomed)
        assertEquals(GamePhase.Intro, world.phase)
    }

    @Test
    fun afterTheIntroTheSnakeCanGoBackDownThePathToItsDen() {
        val world = GameWorld(Arena(108f, 234f), seed = 5)
        world.start()
        var seconds = 0f
        // It steers for the bottom of the path, which is in the middle of the bottom of the oval
        while (world.snake.y < world.arena.height + 60f && seconds < 60f) {
            world.steerToward(world.arena.centerX, world.arena.height + 100f)
            world.update(1f / 60f)
            seconds += 1f / 60f
            if (world.phase == GamePhase.GameOver) break
        }
        assertTrue(world.snake.y > world.arena.height + 60f, "the snake only got to ${world.snake.y}")
        assertTrue(world.cameraY > 0f)
    }
}
