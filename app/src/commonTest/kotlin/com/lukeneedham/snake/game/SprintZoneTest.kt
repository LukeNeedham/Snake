package com.lukeneedham.snake.game

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SprintZoneTest {

    private val arena = Arena(108f, 234f)

    @Test
    fun theBottomRightCornerIsTheSprintZone() {
        assertTrue(SprintZone.contains(arena, 100f, 225f))
        assertTrue(SprintZone.contains(arena, SprintZone.buttonX(arena), SprintZone.buttonY(arena)))
    }

    @Test
    fun everywhereElseTurnsTheSnake() {
        assertFalse(SprintZone.contains(arena, 54f, 117f))
        assertFalse(SprintZone.contains(arena, 5f, 225f))
        assertFalse(SprintZone.contains(arena, 100f, 5f))
        assertFalse(SprintZone.contains(arena, arena.width - SprintZone.size - 1f, arena.height - 1f))
    }
}
