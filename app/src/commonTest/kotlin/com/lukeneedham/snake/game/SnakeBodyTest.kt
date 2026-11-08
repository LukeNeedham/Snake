package com.lukeneedham.snake.game

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SnakeBodyTest {

    private val headX = 54f
    private val headY = 120f
    private val heading = -1.5707964f

    @Test
    fun aNewBodyStretchesStraightBehindTheHead() {
        val body = SnakeBody(headX, headY, heading)
        assertEquals(SnakeTuning.startSections * SnakeTuning.sectionLength, body.length)
        val expected = (body.length / SnakeTuning.bodySpacing).toInt()
        assertTrue(body.points.size in expected - 1..expected + 1, "${body.points.size} discs, expected about $expected")
        assertFalse(body.touches(headX, headY))
    }

    @Test
    fun theDiscsAreOrderedFromTheHeadToTheTail() {
        val body = SnakeBody(headX, headY, heading)
        val distances = body.points.map { it.distanceFromHead }
        assertEquals(distances.sorted(), distances)
    }

    @Test
    fun theBodyGrowsTowardsItsTargetLength() {
        val body = SnakeBody(headX, headY, heading)
        val start = body.length
        body.grow(2)
        body.update(0.1f, body.initialDistance, heading)
        assertTrue(body.length > start)
        repeat(100) { body.update(0.1f, body.initialDistance, heading) }
        assertEquals(start + 2 * SnakeTuning.sectionLength, body.length)
    }

    @Test
    fun aBulgeIsForgottenOnceItIsPastTheTail() {
        val body = SnakeBody(headX, headY, heading)
        body.addBulge(body.initialDistance)
        assertEquals(1, body.bulges.size)
        body.update(0.1f, body.initialDistance + body.length + 20f, heading)
        assertTrue(body.bulges.isEmpty())
    }
}
