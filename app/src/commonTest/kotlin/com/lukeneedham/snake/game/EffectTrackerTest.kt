package com.lukeneedham.snake.game

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class EffectTrackerTest {

    @Test
    fun anEffectIsOnUntilItsTimeRunsOut() {
        val effects = EffectTracker()
        effects.apply(ToadKind.Pink)
        assertTrue(effects.isOn(Effect.Confused))
        assertEquals(Effect.Confused.durationSeconds, effects.remaining(Effect.Confused))
        effects.update(1f, isCountingDown = true, isShowing = true)
        assertEquals(Effect.Confused.durationSeconds - 1f, effects.remaining(Effect.Confused))
        effects.update(10f, isCountingDown = true, isShowing = true)
        assertFalse(effects.isOn(Effect.Confused))
    }

    @Test
    fun theTimeDoesNotRunOutWhileItIsNotCountingDown() {
        val effects = EffectTracker()
        effects.apply(ToadKind.Orange)
        effects.update(5f, isCountingDown = false, isShowing = true)
        assertEquals(Effect.Speedy.durationSeconds, effects.remaining(Effect.Speedy))
    }

    @Test
    fun anEffectFadesInAndOutAgain() {
        val effects = EffectTracker()
        effects.apply(ToadKind.Purple)
        repeat(10) { effects.update(0.1f, isCountingDown = true, isShowing = true) }
        assertTrue(effects.level(Effect.Hallucinating) > 0.9f)
        repeat(100) { effects.update(0.1f, isCountingDown = true, isShowing = false) }
        assertEquals(0f, effects.level(Effect.Hallucinating))
    }

    @Test
    fun eatingAToadShowsItsBanner() {
        val effects = EffectTracker()
        effects.apply(ToadKind.Teal)
        assertEquals(ToadKind.Teal, effects.bannerKind)
        assertTrue(effects.bannerTime > 0f)
    }
}
