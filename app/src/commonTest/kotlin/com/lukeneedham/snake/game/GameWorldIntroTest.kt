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

    private fun returningWorld() = GameWorld(Arena(108f, 234f), seed = 5, opening = Opening.Returning)

    private fun GameWorld.play(seconds: Float) {
        repeat((seconds * 60f).toInt()) { update(1f / 60f) }
    }

    private fun GameWorld.playTheStory() =
        play(IntroTuning.wakeSeconds + IntroTuning.tasteSeconds + IntroTuning.rumbleSeconds + IntroTuning.thinkSeconds + IntroTuning.popSeconds + 0.2f)

    /** Taps whenever the snake turns away from the way to [targetX], [targetY]. */
    private fun GameWorld.steerToward(targetX: Float, targetY: Float) {
        val wanted = atan2(targetY - snake.y, targetX - snake.x)
        val direction = if (sin(wanted - snake.heading) >= 0f) 1f else -1f
        if (direction != snake.turnDirection) tap()
    }

    private fun GameWorld.steerUpThePath() = steerToward(arena.centerX, arena.centerY)

    /** Steers for the animal of the lesson if there is one, holds the sprint corner in the lesson of the sprint, and otherwise steers for where the tutorial leads next. */
    private fun GameWorld.steerThroughTheTutorial() {
        if (intro?.stage == IntroStage.Sprint && !sprint.isHeld) introPress(7L, isInSprintZone = true)
        val animal = critters.firstOrNull()
        when {
            animal != null -> steerToward(animal.x, animal.y)
            intro?.stage == IntroStage.Return -> steerToward(layout.houseX, layout.houseY)
            intro?.stage == IntroStage.Steering || intro?.stage == IntroStage.Teaching || intro?.stage?.isLesson == true ->
                steerToward(layout.room.centerX, layout.room.centerY)
            else -> steerUpThePath()
        }
    }

    private fun GameWorld.playUntil(limitSeconds: Float, isDone: () -> Boolean) {
        var seconds = 0f
        while (!isDone() && seconds < limitSeconds) {
            steerThroughTheTutorial()
            update(1f / 60f)
            seconds += 1f / 60f
        }
    }

    @Test
    fun theSnakeStartsAsleepInItsHouseAScreenUnderTheStage() {
        val world = newWorld()
        assertEquals(GamePhase.Ready, world.phase)
        assertTrue(world.layout.house.signedDistance(world.snake.x, world.snake.y) < 0f)
        assertEquals(world.layout.maxCameraY, world.cameraY)
        assertEquals(0f, world.cameraX)
        assertTrue(world.snake.y > world.arena.height * 2f)
        assertTrue(world.intro!!.isAsleep)
    }

    @Test
    fun aWorldWithoutAnIntroStartsInTheStage() {
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
        assertTrue(world.layout.house.signedDistance(world.snake.x, world.snake.y) < 0f)
        assertEquals(world.layout.maxCameraY, world.cameraY)
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
            assertTrue(world.layout.ground.signedDistance(world.snake.x, world.snake.y) < 0f)
        }
    }

    @Test
    fun theStonesBlockThePathUpSoTheSnakeCannotLeaveThatWay() {
        val world = newWorld()
        assertTrue(world.layout.isGateClosed)
        world.start()
        world.playTheStory()
        world.tap()
        // It keeps trying to go up the path, for a long time
        repeat(60 * 40) {
            world.steerUpThePath()
            world.update(1f / 60f)
            assertTrue(world.snake.y > world.layout.gate.centerY, "the snake got past the stones to ${world.snake.y}")
        }
        assertEquals(GamePhase.Intro, world.phase)
    }

    @Test
    fun thePathToTheLeftLeadsToTheRoomAndTheLessonOfTheHungerBegins() {
        val world = newWorld()
        world.start()
        world.playTheStory()
        world.tap()
        world.playUntil(60f) { world.intro!!.stage == IntroStage.Hunger }
        assertEquals(IntroStage.Hunger, world.intro!!.stage)
        assertTrue(world.layout.room.signedDistance(world.snake.x, world.snake.y) < 0f)
        // The camera has gone over to the room
        world.play(2f)
        assertEquals(world.layout.roomCameraX, world.cameraX)
        assertEquals(world.layout.maxCameraY, world.cameraY)
    }

    @Test
    fun theBellyRunsDownByTwoStepsAndThenTheLessonOfTheSprintBegins() {
        val world = newWorld()
        world.start()
        world.jumpToLesson(IntroStage.Hunger)
        assertEquals(1f, world.hunger.level)
        assertTrue(world.critters.isEmpty())
        world.play(IntroTuning.hungerTextSeconds + 1.5f)
        assertEquals(IntroStage.Sprint, world.intro!!.stage)
        assertEquals(IntroTuning.hungerDemoLevel, world.hunger.level, 0.02f)
        assertTrue(world.critters.isEmpty())
    }

    @Test
    fun holdingTheSprintCornerForLongEnoughShowsThatZoomingMakesTheSnakeHungry() {
        val world = newWorld()
        world.start()
        world.jumpToLesson(IntroStage.Sprint)
        val before = world.hunger.level
        world.introPress(7L, isInSprintZone = true)
        world.play(IntroTuning.zoomSeconds - 0.5f)
        assertEquals(IntroStage.Sprint, world.intro!!.stage)
        world.play(1f)
        assertEquals(IntroStage.Zoomed, world.intro!!.stage)
        assertTrue(world.hunger.level < before, "the belly did not run down")
        world.sprintRelease(7L)
        // And then the mouse
        world.play(IntroTuning.zoomedSeconds + IntroTuning.mouseDelay + 0.5f)
        assertEquals(IntroStage.Mouse, world.intro!!.stage)
        assertTrue(world.critters.single() is Critter.Mouse)
    }

    @Test
    fun letGoOfTheSprintCornerTooSoonAndTheLessonStays() {
        val world = newWorld()
        world.start()
        world.jumpToLesson(IntroStage.Sprint)
        repeat(5) {
            world.introPress(7L, isInSprintZone = true)
            world.play(IntroTuning.zoomSeconds * 0.7f)
            world.sprintRelease(7L)
            world.play(0.3f)
        }
        assertEquals(IntroStage.Sprint, world.intro!!.stage)
    }

    @Test
    fun aMouseRunsInFromTheGrassToTheMiddleOfTheRoomAndWaits() {
        val world = newWorld()
        world.start()
        world.jumpToLesson(IntroStage.Mouse)
        world.play(IntroTuning.mouseDelay + 0.2f)
        val mouse = world.critters.single()
        assertTrue(mouse is Critter.Mouse)
        // It starts in the grass, outside the oval of the room
        assertTrue(world.layout.room.signedDistance(mouse.x, mouse.y) > 0f)
        world.play(10f)
        // Unless the snake ate it on the way, it is in the middle and stays there
        val middle = world.layout.room
        assertTrue(kotlin.math.abs(world.critters.single().x - middle.centerX) < 1f)
        assertTrue(kotlin.math.abs(world.critters.single().y - middle.centerY) < 1f)
    }

    @Test
    fun eatingTheMouseFillsTheBarAndLeadsToTheToad() {
        val world = newWorld()
        world.start()
        world.jumpToLesson(IntroStage.Mouse)
        world.play(IntroTuning.mouseDelay + 0.5f)
        val before = world.hunger.level
        world.playUntil(60f) { world.intro!!.stage != IntroStage.Mouse }
        assertEquals(IntroStage.Toad, world.intro!!.stage)
        assertTrue(world.hunger.level > before)
        assertEquals(GamePhase.Intro, world.phase)
    }

    @Test
    fun theToadHopsInAndItsPoisonHasToRunOutBeforeTheBird() {
        val world = newWorld()
        world.start()
        world.jumpToLesson(IntroStage.Toad)
        world.playUntil(60f) { world.critters.isNotEmpty() }
        val toad = world.critters.single() as Critter.Toad
        assertEquals(ToadKind.Pink, toad.kind)
        world.playUntil(120f) { world.intro!!.stage == IntroStage.Dizzy }
        assertTrue(world.effects.isOn(Effect.Confused))
        // A dizzy snake ignores a tap
        val direction = world.snake.turnDirection
        world.tap()
        assertEquals(direction, world.snake.turnDirection)
        world.playUntil(60f) { world.intro!!.stage == IntroStage.Bird }
        assertFalse(world.effects.isOn(Effect.Confused))
    }

    @Test
    fun theBirdFliesInAndStaysUntilItIsEaten() {
        val world = newWorld()
        world.start()
        world.jumpToLesson(IntroStage.Bird)
        world.playUntil(10f) { world.critters.isNotEmpty() }
        val bird = world.critters.single() as Critter.Bird
        assertTrue(bird.isAirborne)
        // It lands in the middle and does not fly off, whatever the snake does
        world.sprintRelease(0L)
        repeat(60 * 8) { world.update(1f / 60f) }
        if (world.intro!!.stage == IntroStage.Bird) {
            assertEquals(1, world.critters.size)
            assertFalse((world.critters.single() as Critter.Bird).isAirborne)
        }
    }

    @Test
    fun afterTheBirdAnArrowShowsTheWayHomeAndThePathUpOpensWhenTheSnakeIsThere() {
        val world = newWorld()
        world.start()
        world.jumpToLesson(IntroStage.Bird)
        world.playUntil(120f) { world.intro!!.stage == IntroStage.Powers }
        assertEquals(IntroStage.Powers, world.intro!!.stage)
        world.playUntil(10f) { world.intro!!.stage == IntroStage.Return }
        assertEquals(IntroStage.Return, world.intro!!.stage)
        assertTrue(world.layout.isGateClosed)
        world.playUntil(60f) { world.intro!!.stage == IntroStage.Leaving }
        assertEquals(IntroStage.Leaving, world.intro!!.stage)
        assertFalse(world.layout.isGateClosed)
        assertTrue(world.layout.house.signedDistance(world.snake.x, world.snake.y) < 0f)
    }

    @Test
    fun theWholeTutorialEndsInTheStageAndIsDone() {
        val world = newWorld()
        world.start()
        world.jumpToLesson(IntroStage.Hunger)
        world.playUntil(500f) { world.phase == GamePhase.Playing }
        assertEquals(GamePhase.Playing, world.phase)
        assertTrue(world.hasCompletedTutorial)
        assertEquals(0, world.score)
        assertTrue(world.critters.isEmpty())
        assertTrue(world.arena.signedDistance(world.snake.x, world.snake.y) < 0f)
    }

    @Test
    fun afterTheTutorialThePathUpLeadsStraightFromTheHouseToTheStageWithNoLessons() {
        val world = returningWorld()
        assertFalse(world.layout.isGateClosed)
        world.start()
        world.play(IntroTuning.wakeSeconds + 0.2f)
        assertEquals(IntroStage.Steering, world.intro!!.stage)
        world.playUntil(90f) { world.phase == GamePhase.Playing }
        assertEquals(GamePhase.Playing, world.phase)
        assertFalse(world.hasCompletedTutorial)
    }

    @Test
    fun goingIntoTheRoomFromTheHouseAtTheStartOfTheAppStartsTheTutorial() {
        val world = returningWorld()
        world.start()
        world.play(IntroTuning.wakeSeconds + 0.2f)
        // Steer it into the room, to the left of the house
        var seconds = 0f
        while (world.intro?.isReplay != true && seconds < 30f) {
            world.steerToward(world.layout.room.centerX, world.layout.room.centerY)
            world.update(1f / 60f)
            seconds += 1f / 60f
        }
        assertTrue(world.intro!!.isReplay)
        assertEquals(1f, world.hunger.level)
    }

    @Test
    fun leavingTheStageResetsTheStateOfTheSnake() {
        val world = returningWorld()
        world.start()
        world.play(IntroTuning.wakeSeconds + 0.2f)
        world.playUntil(90f) { world.phase == GamePhase.Playing }
        world.play(5f)
        world.powerUps.apply(BirdKind.BlueTit)
        world.completeStage()
        var seconds = 0f
        while (world.arena.signedDistance(world.snake.x, world.snake.y) <= 1f && seconds < 30f) {
            world.steerToward(world.layout.houseX, world.layout.houseY)
            world.update(1f / 60f)
            seconds += 1f / 60f
        }
        assertEquals(1f, world.hunger.level)
        assertFalse(world.powerUps.isOn(BirdKind.BlueTit.powerUp))
    }

    @Test
    fun goingIntoTheRoomOfTheTutorialAgainTeachesTheLessonsAgain() {
        val world = returningWorld()
        world.start()
        world.play(IntroTuning.wakeSeconds + 0.2f)
        world.playUntil(90f) { world.phase == GamePhase.Playing }
        assertEquals(GamePhase.Playing, world.phase)
        // The way in is sealed behind it until the stage is won: once it is, the way is open, and the snake goes back down the path, to the house,
        // and out to the left, into the room
        world.play(5f)
        assertTrue(world.layout.isStageSealed)
        world.completeStage()
        assertFalse(world.layout.isStageSealed)
        var seconds = 0f
        while (world.phase == GamePhase.Playing && seconds < 90f) {
            val goal = if (world.snake.x > 20f) world.layout.houseX to world.layout.houseY else world.layout.room.centerX to world.layout.room.centerY
            world.steerToward(goal.first, goal.second)
            world.update(1f / 60f)
            seconds += 1f / 60f
        }
        assertEquals(GamePhase.Intro, world.phase)
        assertTrue(world.intro!!.isReplay)
        assertEquals(IntroStage.Hunger, world.intro!!.stage)
        // And when the bird is eaten the snake plays on
        world.jumpToLesson(IntroStage.Bird)
        world.playUntil(120f) { world.phase == GamePhase.Playing }
        assertEquals(GamePhase.Playing, world.phase)
        // It does not start again while the snake stays in the room
        world.play(5f)
        assertEquals(GamePhase.Playing, world.phase)
    }

    @Test
    fun aGameThatStartsInTheStageStartsInAPerfectOvalWithTheWayInSealed() {
        val world = GameWorld(Arena(108f, 234f), seed = 5)
        assertTrue(world.layout.isStageSealed)
        assertEquals(1f, world.sealProgress)
        world.start()
        // However it steers for the way out, it stays in the oval, and so do the mice
        repeat(60 * 40) {
            world.steerToward(world.layout.houseX, world.layout.houseY)
            world.update(1f / 60f)
            if (world.phase == GamePhase.Playing) {
                assertTrue(world.arena.signedDistance(world.snake.x, world.snake.y) < 0f, "the snake left the oval at ${world.snake.y}")
            }
        }
        assertEquals(0f, world.cameraY)
    }

    @Test
    fun theWayInGrowsShutBehindTheSnakeOnceItIsAllInTheStageAndOpensWhenTheStageIsWon() {
        val world = returningWorld()
        world.start()
        world.play(IntroTuning.wakeSeconds + 0.2f)
        world.playUntil(90f) { world.phase == GamePhase.Playing }
        assertEquals(GamePhase.Playing, world.phase)
        // It is in, but its tail may still be on the path: the grass grows once the tail is in too
        world.play(5f)
        assertTrue(world.layout.isStageSealed)
        assertEquals(1f, world.sealProgress)
        // Now the stage is a perfect oval: it cannot leave
        repeat(60 * 20) {
            world.steerToward(world.layout.houseX, world.layout.houseY)
            world.update(1f / 60f)
            assertTrue(world.arena.signedDistance(world.snake.x, world.snake.y) < 0f)
        }
        // And when the stage is won the grass goes away, and the snake can leave
        world.completeStage()
        assertTrue(world.isStageComplete)
        world.play(DenTuning.sealSeconds + 0.5f)
        assertEquals(0f, world.sealProgress)
        var seconds = 0f
        while (world.snake.y < world.arena.height + 60f && seconds < 90f && world.phase == GamePhase.Playing) {
            world.steerToward(world.layout.houseX, world.layout.houseY)
            world.update(1f / 60f)
            seconds += 1f / 60f
        }
        assertTrue(world.snake.y > world.arena.height + 60f, "the snake only got to ${world.snake.y}")
        assertTrue(world.cameraY > 0f)
    }

    @Test
    fun theOvalCarriesOnAsNormalAfterTheSnakeDies() {
        val world = GameWorld(Arena(108f, 234f), seed = 5)
        world.start()
        world.endGame(DeathCause.Starvation)
        assertEquals(GamePhase.GameOver, world.phase)
        val x = world.snake.x
        val y = world.snake.y
        var mostAtOnce = 0
        var seenMouse = false
        repeat(60 * 90) {
            world.update(1f / 60f)
            mostAtOnce = maxOf(mostAtOnce, world.critters.size)
            seenMouse = seenMouse || world.critters.any { it is Critter.Mouse }
        }
        // Mice come out of the grass, and the dead snake does not eat them or move
        assertTrue(seenMouse, "no mouse ever came")
        assertEquals(x, world.snake.x)
        assertEquals(y, world.snake.y)
        assertEquals(GamePhase.GameOver, world.phase)
    }

    @Test
    fun goingIntoTheRoomAgainStartsTheLessonsOverAsTheyWereAtTheStart() {
        val world = newWorld()
        world.start()
        world.jumpToLesson(IntroStage.Hunger)
        // Some way into the lessons: the belly has run down, the snake is poisoned and has a power up
        world.play(IntroTuning.hungerTextSeconds + 1.5f)
        world.applyEffect(ToadKind.Pink)
        world.powerUps.apply(BirdKind.BlueTit)
        assertTrue(world.hunger.level < 1f)
        assertEquals(IntroStage.Sprint, world.intro!!.stage)
        // It leaves the room, by the path to the house...
        var seconds = 0f
        while (world.layout.room.signedDistance(world.snake.x, world.snake.y) < 4f && seconds < 60f) {
            world.steerToward(world.layout.houseX, world.layout.houseY)
            world.update(1f / 60f)
            seconds += 1f / 60f
        }
        assertTrue(world.layout.room.signedDistance(world.snake.x, world.snake.y) >= 4f, "the snake did not get out")
        // ...and goes back in: everything is as it was at the start of the lessons
        seconds = 0f
        while (world.layout.room.signedDistance(world.snake.x, world.snake.y) > -DenTuning.roomDepth - 1f && seconds < 60f) {
            world.steerToward(world.layout.room.centerX, world.layout.room.centerY)
            world.update(1f / 60f)
            seconds += 1f / 60f
        }
        assertEquals(IntroStage.Hunger, world.intro!!.stage)
        assertFalse(world.hasMoreThanTheStart())
    }

    /** Whether anything of the lessons is left over: a belly that is not full, a poison, a power up, an animal. */
    private fun GameWorld.hasMoreThanTheStart(): Boolean =
        hunger.level < 0.95f || Effect.entries.any { effects.isOn(it) } || PowerUp.entries.any { powerUps.isOn(it) } || critters.isNotEmpty()
}
