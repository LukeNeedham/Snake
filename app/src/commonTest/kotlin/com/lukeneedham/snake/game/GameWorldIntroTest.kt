package com.lukeneedham.snake.game

import kotlin.math.atan2
import kotlin.math.sin
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GameWorldIntroTest {

    private fun newWorld() = GameWorld(Arena(108f, 234f), seed = 5, opening = Opening.Tutorial)

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
        // A finger held in the sprint corner for long enough is the end of the lesson of the sprint, and the next lesson begins
        world.introPress(7L, isInSprintZone = true)
        seconds = 0f
        while (!world.intro!!.hasZoomed && seconds < 60f) {
            world.steerUpThePath()
            world.update(1f / 60f)
            seconds += 1f / 60f
        }
        assertTrue(world.intro!!.hasZoomed)
        assertEquals(IntroStage.Hunger, world.intro!!.stage)
        assertEquals(GamePhase.Intro, world.phase)
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

    /** Steers for the animal of the lesson if there is one, and up the path if not. */
    private fun GameWorld.steerThroughTheLessons() {
        val animal = critters.firstOrNull()
        if (animal != null) steerToward(animal.x, animal.y) else steerUpThePath()
    }

    private fun GameWorld.playUntil(limitSeconds: Float, isDone: () -> Boolean) {
        var seconds = 0f
        while (!isDone() && seconds < limitSeconds) {
            steerThroughTheLessons()
            update(1f / 60f)
            seconds += 1f / 60f
        }
    }

    @Test
    fun theHungerBarRunsDownAndThenAMouseSitsOnThePath() {
        val world = newWorld()
        world.start()
        world.jumpToLesson(IntroStage.Hunger)
        assertEquals(1f, world.hunger.level)
        assertTrue(world.critters.isEmpty())
        world.play(IntroTuning.hungerTextSeconds + 1f)
        assertEquals(IntroStage.Mouse, world.intro!!.stage)
        assertTrue(world.hunger.level <= IntroTuning.hungerDemoLevel + 0.01f)
        val mouse = world.critters.single()
        assertTrue(mouse is Critter.Mouse)
        val x = mouse.x
        world.play(3f)
        assertEquals(x, world.critters.single().x)
    }

    @Test
    fun eatingTheMouseFillsTheBarAndLeadsOn() {
        val world = newWorld()
        world.start()
        world.jumpToLesson(IntroStage.Hunger)
        world.play(IntroTuning.hungerTextSeconds + 1f)
        val before = world.hunger.level
        world.playUntil(60f) { world.intro!!.stage != IntroStage.Mouse }
        assertEquals(IntroStage.WalkToToad, world.intro!!.stage)
        assertTrue(world.hunger.level > before)
        assertTrue(world.critters.isEmpty())
        assertEquals(GamePhase.Intro, world.phase)
    }

    @Test
    fun theToadComesLaterAndItsPoisonHasToRunOutBeforeTheBird() {
        val world = newWorld()
        world.start()
        world.jumpToLesson(IntroStage.WalkToToad)
        world.playUntil(60f) { world.intro!!.stage == IntroStage.Toad }
        val toad = world.critters.single() as Critter.Toad
        assertEquals(ToadKind.Pink, toad.kind)
        world.playUntil(60f) { world.intro!!.stage == IntroStage.Dizzy }
        assertTrue(world.effects.isOn(Effect.Confused))
        // A dizzy snake ignores a tap
        val direction = world.snake.turnDirection
        world.tap()
        assertEquals(direction, world.snake.turnDirection)
        world.playUntil(60f) { world.intro!!.stage == IntroStage.WalkToBird }
        assertFalse(world.effects.isOn(Effect.Confused))
    }

    @Test
    fun theBirdFliesInAndStaysUntilItIsEaten() {
        val world = newWorld()
        world.start()
        world.jumpToLesson(IntroStage.Bird)
        val bird = world.critters.single() as Critter.Bird
        assertTrue(bird.isAirborne)
        // It does not fly off, whatever the snake does
        world.play(8f)
        assertEquals(1, world.critters.size)
        assertEquals(IntroStage.Bird, world.intro!!.stage)
    }

    @Test
    fun theWholeTutorialEndsInTheOvalAndIsDone() {
        val world = newWorld()
        world.start()
        world.jumpToLesson(IntroStage.Hunger)
        world.playUntil(400f) { world.phase == GamePhase.Playing }
        assertEquals(GamePhase.Playing, world.phase)
        assertTrue(world.hasCompletedTutorial)
        assertEquals(0, world.score)
        assertTrue(world.critters.isEmpty())
        assertTrue(world.arena.signedDistance(world.snake.x, world.snake.y) < 0f)
    }

    @Test
    fun theLessonsKeepThePathGoingOn() {
        val world = newWorld()
        world.start()
        world.jumpToLesson(IntroStage.WalkToBird)
        repeat(60 * 30) {
            world.steerUpThePath()
            world.update(1f / 60f)
            if (world.intro!!.stage == IntroStage.WalkToBird || world.intro!!.stage == IntroStage.Bird) {
                assertEquals(GamePhase.Intro, world.phase)
                assertTrue(world.snake.y > world.arena.height)
            }
        }
    }

    @Test
    fun aShortPathLeadsStraightFromTheDenToTheOvalWithoutLessons() {
        val world = GameWorld(Arena(108f, 234f), seed = 5, opening = Opening.ShortPath)
        assertTrue(world.den.worldHeight < world.arena.height * 2f)
        assertTrue(world.den.hasDoor)
        world.start()
        world.play(IntroTuning.wakeSeconds + 0.2f)
        assertEquals(IntroStage.Steering, world.intro!!.stage)
        world.playUntil(60f) { world.phase == GamePhase.Playing }
        assertEquals(GamePhase.Playing, world.phase)
        assertFalse(world.hasCompletedTutorial)
    }

    @Test
    fun theLongPathOfTheTutorialHasNoDoor() {
        assertFalse(newWorld().den.hasDoor)
        assertFalse(GameWorld(Arena(108f, 234f)).den.isLong)
    }

    @Test
    fun theSnakeOpensTheDoorOfTheDenAndTheTutorialCanStartAgain() {
        val world = GameWorld(Arena(108f, 234f), seed = 5, opening = Opening.ShortPath)
        world.start()
        world.play(IntroTuning.wakeSeconds + 0.2f)
        world.playUntil(60f) { world.phase == GamePhase.Playing }
        assertFalse(world.wantsTutorial)
        // It goes back down the path and into the den, to the door in the wall at the bottom of it
        var seconds = 0f
        while (!world.wantsTutorial && seconds < 90f && world.phase == GamePhase.Playing) {
            world.steerToward(world.den.doorX, world.den.doorY)
            world.update(1f / 60f)
            seconds += 1f / 60f
        }
        assertTrue(world.wantsTutorial, "the door stayed shut at ${world.snake.x}, ${world.snake.y}, ${world.doorOpen}")
    }
}
