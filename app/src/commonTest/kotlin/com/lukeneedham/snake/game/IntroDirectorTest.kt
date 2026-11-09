package com.lukeneedham.snake.game

import kotlin.test.Test
import kotlin.test.assertEquals
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

    @Test
    fun theStoryPlaysFromOnePartToTheNext() {
        val director = IntroDirector()
        assertEquals(IntroStage.Sleeping, director.stage)
        director.play(IntroTuning.sleepSeconds + 0.1f)
        assertEquals(IntroStage.Rumbling, director.stage)
        director.play(IntroTuning.rumbleSeconds)
        assertEquals(IntroStage.Thinking, director.stage)
        director.play(IntroTuning.thinkSeconds)
        assertEquals(IntroStage.Popped, director.stage)
        director.play(IntroTuning.popSeconds)
        assertEquals(IntroStage.Teaching, director.stage)
    }

    @Test
    fun theLessonWaitsForever() {
        val director = IntroDirector()
        director.play(60f)
        assertEquals(IntroStage.Teaching, director.stage)
    }

    @Test
    fun aTapWhileTheSnakeSleepsDoesNotSkip() {
        val director = IntroDirector()
        director.skip()
        assertEquals(IntroStage.Sleeping, director.stage)
    }

    @Test
    fun aTapDuringTheStoryJumpsToTheLesson() {
        val director = IntroDirector()
        director.play(IntroTuning.sleepSeconds + 0.5f)
        director.skip()
        assertEquals(IntroStage.Teaching, director.stage)
    }

    @Test
    fun turningTheSnakeEndsTheLessonOnlyOnceItIsOn() {
        val director = IntroDirector()
        director.turned()
        assertEquals(IntroStage.Sleeping, director.stage)
        director.play(60f)
        director.turned()
        assertEquals(IntroStage.Steering, director.stage)
    }

    @Test
    fun theBubblesComeUpOneAfterTheOther() {
        val director = IntroDirector()
        director.play(IntroTuning.sleepSeconds + IntroTuning.rumbleSeconds + 0.1f)
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
        assertEquals(0f, director.rumble)
        director.play(IntroTuning.sleepSeconds + IntroTuning.rumbleSeconds / 2f)
        assertEquals(1f, director.rumble, 1e-4f)
        director.play(IntroTuning.rumbleSeconds)
        assertEquals(0f, director.rumble)
    }

    @Test
    fun theSnakeSpeedsUpFromCurledToNormal() {
        val director = IntroDirector()
        assertEquals(IntroTuning.curledSpeed, director.snakeSpeed)
        director.play(60f)
        assertEquals(1f, director.snakeSpeed)
    }
}
