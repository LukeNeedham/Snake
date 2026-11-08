package com.lukeneedham.snake.game

import kotlin.math.cos
import kotlin.math.sin
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FeedingTest {

    private val arena = Arena(108f, 240f)
    private val snake = Snake(arena)
    private val feeding = Feeding(arena)

    /** A mouse whose centre is [ahead] units in front of the mouth along the way the snake looks. */
    private fun mouseAhead(ahead: Float): Critter {
        val x = snake.x + cos(snake.heading) * (SnakeTuning.mouthReach + ahead)
        val y = snake.y + sin(snake.heading) * (SnakeTuning.mouthReach + ahead) + 3f
        return Critter.Mouse(1, x, y, lifetime = 100f)
    }

    @Test
    fun theMouthTakesInWhatItReaches() {
        val critters = mutableListOf(mouseAhead(0f))
        val eaten = feeding.update(0.016f, snake, critters)
        assertEquals(1, eaten.size)
        assertTrue(critters.isEmpty())
    }

    @Test
    fun aFarawayCritterIsLeftAloneAndTheMouthStaysShut() {
        val critters = mutableListOf<Critter>(Critter.Mouse(1, 20f, 30f, lifetime = 100f))
        val eaten = feeding.update(0.016f, snake, critters)
        assertTrue(eaten.isEmpty())
        assertEquals(1, critters.size)
        assertEquals(0f, snake.mouthOpen)
    }

    @Test
    fun theMouthOpensWhenFoodIsNear() {
        val critters = mutableListOf(mouseAhead(8f))
        val eaten = feeding.update(0.2f, snake, critters)
        assertTrue(eaten.isEmpty())
        assertTrue(snake.mouthOpen > 0.9f)
    }
}
