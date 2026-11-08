package com.lukeneedham.snake.game

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class HungerTest {

    @Test
    fun theSnakeStartsFull() {
        assertEquals(1f, Hunger().level)
    }

    @Test
    fun theBarGoesDownAtASteadyRate() {
        val hunger = Hunger()
        hunger.update(10f, sprintLevel = 0f)
        assertEquals(1f - 10f * HungerTuning.drainPerSecond, hunger.level, 1e-4f)
    }

    @Test
    fun sprintingDrainsTheBarFaster() {
        val resting = Hunger().apply { update(5f, sprintLevel = 0f) }
        val sprinting = Hunger().apply { update(5f, sprintLevel = 1f) }
        assertTrue(sprinting.level < resting.level)
        assertEquals(1f - 5f * HungerTuning.drainPerSecond * HungerTuning.sprintDrainFactor, sprinting.level, 1e-4f)
    }

    @Test
    fun theSnakeStarvesWhenTheBarIsEmpty() {
        val hunger = Hunger()
        hunger.update(1f, sprintLevel = 0f)
        assertFalse(hunger.isStarving)
        hunger.update(100f, sprintLevel = 0f)
        assertTrue(hunger.isStarving)
        assertEquals(0f, hunger.level)
    }

    @Test
    fun aMouseFillsMoreThanAToadAndTheBarNeverOverflows() {
        assertTrue(HungerTuning.mouseFills > HungerTuning.toadFills)
        val hunger = Hunger()
        hunger.update(20f, sprintLevel = 0f)
        val hungry = hunger.level
        hunger.feed(HungerTuning.mouseFills)
        assertEquals(hungry + HungerTuning.mouseFills, hunger.level, 1e-4f)
        hunger.feed(5f)
        assertEquals(1f, hunger.level)
    }

    @Test
    fun theBarIsLowBeforeTheSnakeStarves() {
        val hunger = Hunger()
        hunger.update(1f / HungerTuning.drainPerSecond * (1f - HungerTuning.lowLevel + 0.05f), sprintLevel = 0f)
        assertTrue(hunger.isLow)
        assertFalse(hunger.isStarving)
    }

    @Test
    fun aMealShowsAsAGainBeforeTheBarFillsIn() {
        val hunger = Hunger()
        hunger.update(20f, sprintLevel = 0f)
        val before = hunger.level
        hunger.feed(HungerTuning.mouseFills)
        assertEquals(before + HungerTuning.mouseFills, hunger.level, 1e-4f)
        assertEquals(before, hunger.filled, 1e-4f)
        assertEquals(HungerTuning.mouseFills, hunger.gain, 1e-4f)
    }

    @Test
    fun theGainStaysForAMomentAndThenFillsIn() {
        val hunger = Hunger()
        hunger.update(20f, sprintLevel = 0f)
        val before = hunger.filled
        hunger.feed(HungerTuning.mouseFills)
        hunger.update(HungerTuning.gainShowSeconds / 2f, sprintLevel = 0f)
        assertEquals(before, hunger.filled, 1e-3f)
        hunger.update(HungerTuning.gainShowSeconds, sprintLevel = 0f)
        hunger.update(0.1f, sprintLevel = 0f)
        assertTrue(hunger.filled > before)
        repeat(100) { hunger.update(0.1f, sprintLevel = 0f) }
        assertEquals(0f, hunger.gain, 1e-4f)
    }

    @Test
    fun theFilledPartNeverGoesPastTheRealLevel() {
        val hunger = Hunger()
        hunger.update(25f, sprintLevel = 0f)
        hunger.feed(HungerTuning.toadFills)
        repeat(100) {
            hunger.update(0.1f, sprintLevel = 0f)
            assertTrue(hunger.filled <= hunger.level + 1e-6f)
        }
    }

    @Test
    fun aSecondMealAddsToTheGain() {
        val hunger = Hunger()
        hunger.update(25f, sprintLevel = 0f)
        hunger.feed(0.1f)
        hunger.feed(0.1f)
        assertEquals(0.2f, hunger.gain, 1e-4f)
    }
}
