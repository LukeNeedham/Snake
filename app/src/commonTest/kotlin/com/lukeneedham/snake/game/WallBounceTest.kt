package com.lukeneedham.snake.game

import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class WallBounceTest {

    private val arena = Arena(108f, 240f)
    private val wallBounce = WallBounce(arena)
    private val rightEdge = arena.centerX + arena.radiusX

    @Test
    fun aSnakeInsideTheOvalIsLeftAlone() {
        assertNull(wallBounce.apply(arena.centerX, arena.centerY, 0f))
    }

    @Test
    fun aSnakeGoingIntoTheWallIsMirroredAndPushedBackIn() {
        val bounce = assertNotNull(wallBounce.apply(rightEdge, arena.centerY, 0f))
        assertTrue(bounce.reflected)
        assertTrue(abs(abs(bounce.heading) - 3.1415927f) < 1e-3f, "heading ${bounce.heading}")
        assertTrue(bounce.x < rightEdge)
    }

    @Test
    fun aSnakeAlreadyGoingBackInKeepsItsCourse() {
        val heading = 3.1415927f
        val bounce = assertNotNull(wallBounce.apply(rightEdge, arena.centerY, heading))
        assertFalse(bounce.reflected)
        assertEquals(heading, bounce.heading)
        assertTrue(bounce.x < rightEdge)
    }
}
