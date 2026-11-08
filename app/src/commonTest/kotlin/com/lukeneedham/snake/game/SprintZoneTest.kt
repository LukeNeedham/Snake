package com.lukeneedham.snake.game

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SprintZoneTest {

    private val arena = Arena(108f, 234f)

    @Test
    fun theLeftZoneIsTheBottomLeftCorner() {
        assertTrue(SprintZone.contains(arena, SprintSide.Left, 5f, 225f))
        assertTrue(SprintZone.contains(arena, SprintSide.Left, SprintZone.buttonX(arena, SprintSide.Left), SprintZone.buttonY(arena)))
        assertFalse(SprintZone.contains(arena, SprintSide.Left, 100f, 225f))
    }

    @Test
    fun theRightZoneIsTheBottomRightCorner() {
        assertTrue(SprintZone.contains(arena, SprintSide.Right, 100f, 225f))
        assertTrue(SprintZone.contains(arena, SprintSide.Right, SprintZone.buttonX(arena, SprintSide.Right), SprintZone.buttonY(arena)))
        assertFalse(SprintZone.contains(arena, SprintSide.Right, 5f, 225f))
    }

    @Test
    fun everywhereElseTurnsTheSnake() {
        for (side in SprintSide.entries) {
            assertFalse(SprintZone.contains(arena, side, 54f, 117f))
            assertFalse(SprintZone.contains(arena, side, 54f, 225f))
            assertFalse(SprintZone.contains(arena, side, if (side == SprintSide.Left) 5f else 100f, 5f))
        }
    }

    @Test
    fun theButtonIsInsideItsZone() {
        for (side in SprintSide.entries) {
            val x = SprintZone.buttonX(arena, side)
            assertTrue(SprintZone.contains(arena, side, x - SprintZone.buttonRadius, SprintZone.buttonY(arena) + SprintZone.buttonRadius))
            assertTrue(SprintZone.contains(arena, side, x + SprintZone.buttonRadius, SprintZone.buttonY(arena) - SprintZone.buttonRadius))
        }
    }
}
