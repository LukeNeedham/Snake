package com.lukeneedham.snake.game

import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SnakeTest {

    @Test
    fun theSnakeStartsWithoutHittingItself() {
        val snake = Snake(Arena(108f, 234f))
        assertFalse(snake.hitsItself())
    }

    @Test
    fun theBodyHasTheLengthOfTheSnake() {
        val snake = Snake(Arena(108f, 234f))
        repeat(120) { snake.step(1f / 60f, 1f) }
        val expected = (snake.body.length / SnakeTuning.bodySpacing).toInt()
        assertTrue(abs(snake.body.points.size - expected) <= 2, "body ${snake.body.points.size}, expected about $expected")
    }

    @Test
    fun turningTheOtherWayChangesTheDirectionOfTheTurn() {
        val snake = Snake(Arena(108f, 234f))
        val before = snake.turnDirection
        snake.toggleTurn()
        assertEquals(-before, snake.turnDirection)
    }

    @Test
    fun theSnakeBouncesOffTheWallAndStaysInside() {
        val arena = Arena(60f, 60f)
        val snake = Snake(arena)
        repeat(60 * 60) {
            snake.step(1f / 60f, 1f)
            assertTrue(arena.signedDistance(snake.x, snake.y) <= -3f, "left the oval at ${snake.x}, ${snake.y}")
        }
        assertTrue(snake.bounceCount > 0)
    }

    @Test
    fun aSnakeLongerThanItsCircleHitsItself() {
        val snake = Snake(Arena(108f, 234f))
        snake.grow(20)
        var hit = false
        repeat(60 * 30) {
            if (!hit) {
                snake.step(1f / 60f, 1f)
                hit = snake.hitsItself()
            }
        }
        assertTrue(hit)
    }
}
