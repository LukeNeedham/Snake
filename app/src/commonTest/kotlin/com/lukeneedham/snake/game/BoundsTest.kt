package com.lukeneedham.snake.game

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BoundsTest {

    @Test
    fun aCircleIsNegativeInsideAndPositiveOutside() {
        val circle = CircleBounds(10f, 20f, 5f)
        assertEquals(-5f, circle.signedDistance(10f, 20f), 1e-4f)
        assertEquals(-2f, circle.signedDistance(13f, 20f), 1e-4f)
        assertEquals(3f, circle.signedDistance(10f, 28f), 1e-4f)
    }

    @Test
    fun aCircleFacesAwayFromItsMiddle() {
        val normal = CircleBounds(0f, 0f, 5f).normal(0f, 3f)
        assertEquals(0f, normal.x, 1e-4f)
        assertEquals(1f, normal.y, 1e-4f)
    }

    @Test
    fun aBoxIsNegativeInsideAndPositiveOutside() {
        val box = BoxBounds(0f, 0f, 4f, 10f)
        assertEquals(-4f, box.signedDistance(0f, 0f), 1e-4f)
        assertEquals(-1f, box.signedDistance(3f, 0f), 1e-4f)
        assertEquals(2f, box.signedDistance(6f, 0f), 1e-4f)
        assertEquals(5f, box.signedDistance(7f, 14f), 1e-4f)
    }

    @Test
    fun theWallOfABoxFacesOutwardFromTheNearestSide() {
        val box = BoxBounds(0f, 0f, 4f, 10f)
        val side = box.normal(3f, 0f)
        assertEquals(1f, side.x, 1e-4f)
        assertEquals(0f, side.y, 1e-4f)
        val end = box.normal(0f, -9f)
        assertEquals(0f, end.x, 1e-4f)
        assertEquals(-1f, end.y, 1e-4f)
    }

    @Test
    fun aUnionIsInsideWhenAnyPartIs() {
        val union = UnionBounds(listOf(CircleBounds(0f, 0f, 5f), BoxBounds(0f, 20f, 2f, 10f)))
        assertTrue(union.signedDistance(0f, 3f) < 0f)
        assertTrue(union.signedDistance(0f, 25f) < 0f)
        assertTrue(union.signedDistance(10f, 10f) > 0f)
    }

    @Test
    fun aUnionFacesTheWayOfItsNearestPart() {
        val union = UnionBounds(listOf(CircleBounds(0f, 0f, 5f), BoxBounds(0f, 20f, 2f, 10f)))
        val normal = union.normal(1.5f, 20f)
        assertEquals(1f, normal.x, 1e-4f)
        assertEquals(0f, normal.y, 1e-4f)
    }
}
