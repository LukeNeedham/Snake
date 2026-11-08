package com.lukeneedham.snake.game

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SprintControlTest {

    private val control = SprintControl()

    @Test
    fun aQuickPressIsATap() {
        control.press(1L, now = 0f)
        assertTrue(control.release(1L, now = SprintTuning.holdSeconds / 2f))
    }

    @Test
    fun aLongPressIsAHoldNotATap() {
        control.press(1L, now = 0f)
        assertFalse(control.release(1L, now = SprintTuning.holdSeconds + 0.1f))
    }

    @Test
    fun aFingerThatWasNeverPressedIsNotATap() {
        assertFalse(control.release(5L, now = 1f))
    }

    @Test
    fun theSnakeSprintsOnlyWhileAFingerIsHeld() {
        control.press(1L, now = 0f)
        repeat(30) { control.update(1f / 60f, now = it / 60f, canSprint = true) }
        assertEquals(1f, control.level, 1e-4f)
        control.release(1L, now = 0.5f)
        repeat(30) { control.update(1f / 60f, now = 0.5f + it / 60f, canSprint = true) }
        assertEquals(0f, control.level, 1e-4f)
    }

    @Test
    fun aFingerThatHasJustGoneDownDoesNotSprintYet() {
        control.press(1L, now = 0f)
        control.update(1f / 60f, now = 1f / 60f, canSprint = true)
        assertEquals(0f, control.level)
    }

    @Test
    fun twoFingersAreToldApart() {
        control.press(1L, now = 0f)
        control.press(2L, now = 0.5f)
        assertTrue(control.release(2L, now = 0.55f))
        assertFalse(control.release(1L, now = 0.6f))
    }

    @Test
    fun nothingSprintsWhileNotPlaying() {
        control.press(1L, now = 0f)
        repeat(60) { control.update(1f / 60f, now = it / 60f, canSprint = false) }
        assertEquals(0f, control.level)
    }
}
