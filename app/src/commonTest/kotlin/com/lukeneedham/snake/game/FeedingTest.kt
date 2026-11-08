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

    /** A bird whose centre is at the mouth: it is on the ground if [isOnTheGround], else it is still in the air. */
    private fun birdAtTheMouth(isOnTheGround: Boolean): Critter {
        val x = snake.x + cos(snake.heading) * SnakeTuning.mouthReach
        val y = snake.y + sin(snake.heading) * SnakeTuning.mouthReach + 3f
        return Critter.Bird(1, BirdKind.Crow, x, y, x, y, staySeconds = 100f, isLanded = isOnTheGround)
    }

    @Test
    fun aBirdInTheAirIsOutOfReach() {
        val critters = mutableListOf(birdAtTheMouth(isOnTheGround = false))
        assertTrue(feeding.update(0.016f, snake, critters).isEmpty())
        assertEquals(1, critters.size)
    }

    @Test
    fun aBirdOnTheGroundIsEaten() {
        val critters = mutableListOf(birdAtTheMouth(isOnTheGround = true))
        assertEquals(1, feeding.update(0.016f, snake, critters).size)
        assertTrue(critters.isEmpty())
    }

    @Test
    fun aBiggerMouthReachesFurther() {
        val normal = mutableListOf(mouseAhead(7f))
        assertTrue(feeding.update(0.016f, snake, normal).isEmpty())
        val bigger = mutableListOf(mouseAhead(7f))
        assertEquals(1, feeding.update(0.016f, snake, bigger, reachFactor = PowerUpTuning.biggerMouthFactor).size)
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
        val critters = mutableListOf<Critter>(Critter.Mouse(1, 54f, 40f, lifetime = 100f))
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
