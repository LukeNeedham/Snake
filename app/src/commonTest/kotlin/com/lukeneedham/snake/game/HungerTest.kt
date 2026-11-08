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
}
