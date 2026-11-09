package com.lukeneedham.snake.game

import kotlin.math.cos
import kotlin.math.sin
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BoundsTest {

    private val blob = BlobBounds(10f, 20f, 40f, listOf(Wobble(0.06f, 2, 0.7f), Wobble(0.05f, 3, 2.1f)))

    @Test
    fun aBlobIsNegativeInsideAndPositiveOutside() {
        assertTrue(blob.signedDistance(10f, 20f) < -30f)
        assertTrue(blob.signedDistance(10f + 60f, 20f) > 10f)
        assertTrue(blob.signedDistance(10f, 20f + 60f) > 10f)
    }

    @Test
    fun theEdgeOfABlobIsWhereItsRadiusSays() {
        for (step in 0 until 12) {
            val angle = step * 0.5236f
            val edge = blob.radiusAt(angle)
            assertEquals(0f, blob.signedDistance(10f + cos(angle) * edge, 20f + sin(angle) * edge), 1e-2f)
        }
    }

    @Test
    fun aBlobIsNotRound() {
        val radii = (0 until 24).map { blob.radiusAt(it * 0.2618f) }
        assertTrue(radii.max() - radii.min() > 5f)
    }

    @Test
    fun aBlobFacesAwayFromItsMiddle() {
        for (step in 0 until 12) {
            val angle = step * 0.5236f
            val edge = blob.radiusAt(angle)
            val normal = blob.normal(10f + cos(angle) * edge, 20f + sin(angle) * edge)
            // The wall faces mostly outward: the bumps are mild
            assertTrue(normal.x * cos(angle) + normal.y * sin(angle) > 0.8f)
        }
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
        val union = UnionBounds(listOf(BlobBounds(0f, 0f, 5f, emptyList()), BoxBounds(0f, 20f, 2f, 10f)))
        assertTrue(union.signedDistance(0f, 3f) < 0f)
        assertTrue(union.signedDistance(0f, 25f) < 0f)
        assertTrue(union.signedDistance(10f, 10f) > 0f)
    }

    @Test
    fun aUnionFacesTheWayOfItsNearestPart() {
        val union = UnionBounds(listOf(BlobBounds(0f, 0f, 5f, emptyList()), BoxBounds(0f, 20f, 2f, 10f)))
        val normal = union.normal(1.5f, 20f)
        assertEquals(1f, normal.x, 1e-4f)
        assertEquals(0f, normal.y, 1e-4f)
    }

    @Test
    fun anOvalIsNegativeInsideAndPositiveOutside() {
        val oval = OvalBounds(0f, 0f, 40f, 20f)
        assertTrue(oval.signedDistance(0f, 0f) < 0f)
        assertEquals(0f, oval.signedDistance(40f, 0f), 1e-3f)
        assertTrue(oval.signedDistance(50f, 0f) > 5f)
        assertTrue(oval.signedDistance(0f, 30f) > 5f)
    }

    @Test
    fun theStonesOfAGateCutItOutOfTheGroundWhileItIsClosed() {
        var isClosed = true
        val path = BoxBounds(0f, 0f, 10f, 100f)
        val gate = BoxBounds(0f, 20f, 13f, 7f)
        val ground = GateBounds(path, gate) { isClosed }
        // On the stones it is outside the ground, and on both sides it is still inside
        assertTrue(ground.signedDistance(0f, 20f) > 0f)
        assertTrue(ground.signedDistance(0f, 60f) < 0f)
        assertTrue(ground.signedDistance(0f, -20f) < 0f)
        isClosed = false
        assertTrue(ground.signedDistance(0f, 20f) < 0f)
    }

    @Test
    fun theWallOfAClosedGateFacesTheStones() {
        val path = BoxBounds(0f, 0f, 10f, 100f)
        val gate = BoxBounds(0f, 20f, 13f, 7f)
        val ground = GateBounds(path, gate) { true }
        // Below the stones, close to them: the wall faces up, into them
        val below = ground.normal(0f, 30f)
        assertEquals(-1f, below.y, 1e-3f)
        val above = ground.normal(0f, 10f)
        assertEquals(1f, above.y, 1e-3f)
    }
}
