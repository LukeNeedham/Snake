package com.lukeneedham.snake.game

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SprintControlTest {

    private val control = SprintControl()

    private fun SprintControl.run(seconds: Float, canSprint: Boolean = true) =
        repeat((seconds * 60f).toInt()) { update(1f / 60f, canSprint) }

    @Test
    fun theSnakeSprintsForAsLongAsAFingerIsHeld() {
        control.press(1L)
        control.run(1f)
        assertEquals(1f, control.level, 1e-4f)
        control.release(1L)
        control.run(1f)
        assertEquals(0f, control.level, 1e-4f)
    }

    @Test
    fun theSprintSpeedsUpSmoothly() {
        control.press(1L)
        control.update(1f / 60f, canSprint = true)
        assertTrue(control.level > 0f)
        assertTrue(control.level < 1f)
    }

    @Test
    fun theSnakeKeepsSprintingWhileOneOfTwoFingersIsStillDown() {
        control.press(1L)
        control.press(2L)
        control.release(1L)
        control.run(1f)
        assertEquals(1f, control.level, 1e-4f)
        control.release(2L)
        control.run(1f)
        assertEquals(0f, control.level, 1e-4f)
    }

    @Test
    fun nothingSprintsWhileNotPlaying() {
        control.press(1L)
        control.run(1f, canSprint = false)
        assertEquals(0f, control.level)
    }

    @Test
    fun clearingLetsGoOfEveryFinger() {
        control.press(1L)
        control.clear()
        control.run(1f)
        assertEquals(0f, control.level)
    }
}
