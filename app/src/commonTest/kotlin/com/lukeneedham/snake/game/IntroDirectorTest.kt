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

    /** A director that has been woken and has played all of the story. */
    private fun taughtDirector(): IntroDirector {
        val director = IntroDirector()
        director.wake()
        director.play(IntroTuning.wakeSeconds + IntroTuning.tasteSeconds + IntroTuning.rumbleSeconds + IntroTuning.thinkSeconds + IntroTuning.popSeconds + 0.1f)
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
    fun theLessonWaitsUntilThePlayerTurnsTheSnake() {
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
        val taught = taughtDirector()
        assertEquals(1f, taught.snakeSpeed)
    }

    @Test
    fun theSprintLessonOnlyStartsHalfWayAlongThePath() {
        val director = taughtDirector()
        director.trackSprint(true, 10f)
        assertFalse(director.hasZoomed)
        director.turned()
        director.halfway()
        assertTrue(director.reachedHalfway)
    }

    @Test
    fun theSprintCornerHasToBeHeldWithoutLettingGo() {
        val director = taughtDirector()
        director.turned()
        director.halfway()
        director.trackSprint(true, IntroTuning.zoomSeconds - 0.5f)
        director.trackSprint(false, 0.1f)
        director.trackSprint(true, IntroTuning.zoomSeconds - 0.5f)
        assertFalse(director.hasZoomed)
        director.trackSprint(true, 1f)
        assertTrue(director.hasZoomed)
    }
}
