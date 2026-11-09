package com.lukeneedham.snake.game

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PowerDownTrackerTest {

    @Test
    fun aPowerDownIsOnUntilItsTimeRunsOut() {
        val powerDowns = PowerDownTracker()
        powerDowns.apply(ToadKind.Pink)
        assertTrue(powerDowns.isOn(PowerDown.Confused))
        assertEquals(PowerDown.Confused.durationSeconds, powerDowns.remaining(PowerDown.Confused))
        powerDowns.update(1f, isCountingDown = true, isShowing = true)
        assertEquals(PowerDown.Confused.durationSeconds - 1f, powerDowns.remaining(PowerDown.Confused))
        powerDowns.update(10f, isCountingDown = true, isShowing = true)
        assertFalse(powerDowns.isOn(PowerDown.Confused))
    }

    @Test
    fun theTimeDoesNotRunOutWhileItIsNotCountingDown() {
        val powerDowns = PowerDownTracker()
        powerDowns.apply(ToadKind.Pink)
        powerDowns.update(5f, isCountingDown = false, isShowing = true)
        assertEquals(PowerDown.Confused.durationSeconds, powerDowns.remaining(PowerDown.Confused))
    }

    @Test
    fun aPowerDownFadesInAndOutAgain() {
        val powerDowns = PowerDownTracker()
        powerDowns.apply(ToadKind.Purple)
        repeat(10) { powerDowns.update(0.1f, isCountingDown = true, isShowing = true) }
        assertTrue(powerDowns.level(PowerDown.Hallucinating) > 0.9f)
        repeat(100) { powerDowns.update(0.1f, isCountingDown = true, isShowing = false) }
        assertEquals(0f, powerDowns.level(PowerDown.Hallucinating))
    }

    @Test
    fun eatingAToadShowsItsBanner() {
        val powerDowns = PowerDownTracker()
        powerDowns.apply(ToadKind.Teal)
        assertEquals(ToadKind.Teal, powerDowns.bannerKind)
        assertTrue(powerDowns.bannerTime > 0f)
    }
}
