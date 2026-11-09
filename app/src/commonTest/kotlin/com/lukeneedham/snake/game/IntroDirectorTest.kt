package com.lukeneedham.snake.game

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class IntroDirectorTest {

    private fun IntroDirector.play(seconds: Float) {
        var left = seconds
        while (left > 0f) {
            val step = minOf(left, 0.05f)
            update(step)
            left -= step
        }
    }

    /** A director that has been woken and has played all of the story, and is waiting for the tap that turns the snake. */
    private fun taughtDirector(): IntroDirector {
        val director = IntroDirector()
        director.wake()
        director.play(IntroTuning.wakeSeconds + IntroTuning.tasteSeconds + IntroTuning.rumbleSeconds + IntroTuning.thinkSeconds + IntroTuning.popSeconds + 0.1f)
        return director
    }

    /** A director whose snake has turned and gone into the room of the tutorial: the lesson of the hunger. */
    private fun roomDirector(): IntroDirector {
        val director = taughtDirector()
        director.turned()
        director.enteredRoom()
        return director
    }

    /** A director in the lesson of the sprint: the bar has shown and run down. */
    private fun sprintDirector(): IntroDirector {
        val director = roomDirector()
        director.play(IntroTuning.hungerTextSeconds + 0.1f)
        director.hungerShown(IntroTuning.hungerDemoLevel)
        return director
    }

    @Test
    fun theSnakeSleepsUntilItIsWoken() {
        val director = IntroDirector()
        director.play(60f)
        assertEquals(IntroStage.Sleeping, director.stage)
        assertTrue(director.isAsleep)
        assertTrue(director.eyesClosed)
        assertEquals(0f, director.snakeSpeed)
    }

    @Test
    fun theStoryPlaysFromOnePartToTheNext() {
        val director = IntroDirector()
        director.wake()
        assertEquals(IntroStage.Waking, director.stage)
        director.play(IntroTuning.wakeSeconds + 0.1f)
        assertEquals(IntroStage.Tasting, director.stage)
        director.play(IntroTuning.tasteSeconds)
        assertEquals(IntroStage.Rumbling, director.stage)
        director.play(IntroTuning.rumbleSeconds)
        assertEquals(IntroStage.Thinking, director.stage)
        director.play(IntroTuning.thinkSeconds)
        assertEquals(IntroStage.Popped, director.stage)
        director.play(IntroTuning.popSeconds)
        assertEquals(IntroStage.Teaching, director.stage)
    }

    @Test
    fun theLessonOfTheTurnWaitsUntilThePlayerTurnsTheSnake() {
        val director = taughtDirector()
        director.play(60f)
        assertEquals(IntroStage.Teaching, director.stage)
        director.turned()
        assertEquals(IntroStage.Steering, director.stage)
    }

    @Test
    fun turningDuringTheStoryDoesNothing() {
        val director = IntroDirector()
        director.wake()
        director.turned()
        assertEquals(IntroStage.Waking, director.stage)
    }

    @Test
    fun theSnakeBlinksWhileItWakesUpAndThenKeepsItsEyesOpen() {
        val director = IntroDirector()
        director.wake()
        assertTrue(director.eyesClosed)
        var blinks = 0
        var wasClosed = true
        repeat(((IntroTuning.wakeSeconds - 0.1f) / 0.01f).toInt()) {
            director.update(0.01f)
            if (director.eyesClosed && !wasClosed) blinks++
            wasClosed = director.eyesClosed
        }
        assertTrue(blinks >= 3, "blinked $blinks times")
        director.play(0.2f)
        assertFalse(director.eyesClosed)
    }

    @Test
    fun theTongueOnlyComesOutWhileTasting() {
        val director = IntroDirector()
        director.wake()
        assertEquals(0f, director.tongueProgress)
        director.play(IntroTuning.wakeSeconds + IntroTuning.tasteSeconds / 2f)
        assertTrue(director.tongueProgress > 0f)
        director.play(IntroTuning.tasteSeconds)
        assertEquals(0f, director.tongueProgress)
    }

    @Test
    fun theBubblesComeUpOneAfterTheOther() {
        val director = IntroDirector()
        director.wake()
        director.play(IntroTuning.wakeSeconds + IntroTuning.tasteSeconds + IntroTuning.rumbleSeconds + 0.1f)
        assertEquals(IntroStage.Thinking, director.stage)
        assertTrue(director.bubbleGrowth(0) > 0f)
        assertEquals(0f, director.bubbleGrowth(1))
        director.play(IntroTuning.bubbleGap * 2f + IntroTuning.bubbleGrowSeconds)
        assertEquals(1f, director.bubbleGrowth(0), 1e-4f)
        assertEquals(1f, director.bubbleGrowth(2), 1e-4f)
    }

    @Test
    fun theStomachOnlyRumblesWhileItRumbles() {
        val director = IntroDirector()
        director.wake()
        assertEquals(0f, director.rumble)
        director.play(IntroTuning.wakeSeconds + IntroTuning.tasteSeconds + IntroTuning.rumbleSeconds / 2f)
        assertEquals(1f, director.rumble, 1e-4f)
        director.play(IntroTuning.rumbleSeconds)
        assertEquals(0f, director.rumble)
    }

    @Test
    fun theSnakeLiesStillUntilTheBubblePopsAndThenSpeedsUp() {
        val director = IntroDirector()
        director.wake()
        director.play(IntroTuning.wakeSeconds + IntroTuning.tasteSeconds + IntroTuning.rumbleSeconds + 1f)
        assertEquals(0f, director.snakeSpeed)
        assertEquals(1f, taughtDirector().snakeSpeed)
    }

    @Test
    fun theLessonsOnlyStartOnceTheSnakeIsInTheRoom() {
        val director = taughtDirector()
        director.enteredRoom()
        assertEquals(IntroStage.Teaching, director.stage)
        director.turned()
        assertEquals(IntroStage.Steering, director.stage)
        assertFalse(director.showsHunger)
        director.enteredRoom()
        assertEquals(IntroStage.Hunger, director.stage)
        assertTrue(director.showsHunger)
        assertFalse(director.showsSprintButton)
    }

    @Test
    fun theLessonOfTheSprintComesOnceTheBellyHasShownAndRunDown() {
        val director = roomDirector()
        director.hungerShown(0.3f)
        assertEquals(IntroStage.Hunger, director.stage)
        director.play(IntroTuning.hungerTextSeconds + 0.1f)
        director.hungerShown(1f)
        assertEquals(IntroStage.Hunger, director.stage)
        director.hungerShown(IntroTuning.hungerDemoLevel)
        assertEquals(IntroStage.Sprint, director.stage)
        assertTrue(director.showsSprintButton)
    }

    @Test
    fun theSprintCornerHasToBeHeldWithoutLettingGo() {
        val director = sprintDirector()
        director.trackSprint(true, IntroTuning.zoomSeconds - 0.5f)
        director.trackSprint(false, 0.1f)
        director.trackSprint(true, IntroTuning.zoomSeconds - 0.5f)
        assertEquals(IntroStage.Sprint, director.stage)
        assertFalse(director.hasZoomed)
        director.trackSprint(true, 1f)
        assertTrue(director.hasZoomed)
        assertEquals(IntroStage.Zoomed, director.stage)
    }

    @Test
    fun theSprintCornerDoesNothingBeforeTheLessonOfTheSprint() {
        val director = roomDirector()
        director.trackSprint(true, 10f)
        assertEquals(IntroStage.Hunger, director.stage)
    }

    @Test
    fun theMouseComesAfterTheWordsThatZoomingMakesTheSnakeHungry() {
        val director = sprintDirector()
        director.trackSprint(true, IntroTuning.zoomSeconds + 0.1f)
        assertEquals(IntroStage.Zoomed, director.stage)
        director.play(IntroTuning.zoomedSeconds - 0.5f)
        assertEquals(IntroStage.Zoomed, director.stage)
        director.play(1f)
        assertEquals(IntroStage.Mouse, director.stage)
    }

    @Test
    fun anAnimalComesInOnceAfterItsDelay() {
        val director = IntroDirector()
        director.jumpTo(IntroStage.Mouse)
        assertFalse(director.takeAnimalDue())
        director.play(IntroTuning.mouseDelay + 0.1f)
        assertTrue(director.takeAnimalDue())
        assertFalse(director.takeAnimalDue())
        director.jumpTo(IntroStage.Toad)
        director.play(IntroTuning.toadDelay - 0.5f)
        assertFalse(director.takeAnimalDue())
        director.play(1f)
        assertTrue(director.takeAnimalDue())
    }

    @Test
    fun theLessonsGoFromTheMouseToTheToadAndTheBirdAndThePowersAndHome() {
        val director = IntroDirector()
        director.jumpTo(IntroStage.Mouse)
        // Eating anything but the mouse does not learn the lesson of the mouse
        director.ate(Critter.Toad(1, ToadKind.Pink, 0f, 0f, 10f))
        assertEquals(IntroStage.Mouse, director.stage)
        director.ate(Critter.Mouse(2, 0f, 0f, 10f))
        assertEquals(IntroStage.Toad, director.stage)
        director.ate(Critter.Toad(3, ToadKind.Pink, 0f, 0f, 10f))
        assertEquals(IntroStage.Dizzy, director.stage)
        director.dizzy(true)
        director.dizzy(false)
        assertEquals(IntroStage.Bird, director.stage)
        director.ate(Critter.Bird(4, BirdKind.BlueTit, 0f, 0f, 0f, 0f, 10f))
        assertEquals(IntroStage.Powers, director.stage)
        director.play(IntroTuning.powersSeconds + 0.1f)
        assertEquals(IntroStage.Return, director.stage)
        director.arrivedHome()
        assertEquals(IntroStage.Leaving, director.stage)
    }

    @Test
    fun theSnakeHasToBeDizzyAndGetOverItBeforeTheBird() {
        val director = IntroDirector()
        director.jumpTo(IntroStage.Dizzy)
        director.dizzy(false)
        assertEquals(IntroStage.Dizzy, director.stage)
        director.dizzy(true)
        assertEquals(IntroStage.Dizzy, director.stage)
        director.dizzy(false)
        assertEquals(IntroStage.Bird, director.stage)
    }

    @Test
    fun withoutTheTutorialTheSnakeJustWakesUpAndGoes() {
        val director = IntroDirector(isTutorial = false)
        director.wake()
        director.play(IntroTuning.wakeSeconds + 0.1f)
        assertEquals(IntroStage.Steering, director.stage)
        assertEquals(1f, director.snakeSpeed)
        assertFalse(director.showsHunger)
        assertFalse(director.showsSprintButton)
        director.enteredRoom()
        assertEquals(IntroStage.Steering, director.stage)
    }
}
