package com.lukeneedham.snake.game

import kotlin.math.abs
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TameCritterTest {

    private val arena = Arena(108f, 234f)
    private val random = Random(3)
    private val target = Vec2(-54f, 500f)

    private fun Critter.play(seconds: Float, snakeHead: Vec2?) {
        repeat((seconds * 60f).toInt()) { update(1f / 60f, arena, random, snakeHead) }
    }

    @Test
    fun aTameMouseRunsToItsTargetAndStaysThereWhateverTheSnakeDoes() {
        val mouse = Critter.Mouse(1, -110f, 500f, lifetime = 1f, tameTarget = target)
        mouse.play(0.5f, Vec2(-54f, 496f))
        assertTrue(mouse.isMoving)
        assertTrue(mouse.x > -110f)
        mouse.play(20f, Vec2(-54f, 496f))
        assertEquals(target.x, mouse.x, 1e-3f)
        assertEquals(target.y, mouse.y, 1e-3f)
        assertFalse(mouse.isMoving)
        assertFalse(mouse.isGone)
        assertFalse(mouse.isFleeing)
        assertTrue(mouse.isEdible)
    }

    @Test
    fun aTameToadHopsToItsTargetAndStaysThere() {
        val toad = Critter.Toad(2, ToadKind.Pink, -110f, 500f, lifetime = 1f, tameTarget = target)
        var wasHopping = false
        var wasInTheAir = false
        repeat(60 * 30) {
            toad.update(1f / 60f, arena, random, Vec2(-54f, 496f))
            wasHopping = wasHopping || toad.isMoving
            wasInTheAir = wasInTheAir || toad.height > 1f
        }
        assertTrue(wasHopping)
        assertTrue(wasInTheAir)
        assertTrue(abs(toad.x - target.x) < 1f, "the toad stopped at ${toad.x}")
        assertTrue(abs(toad.y - target.y) < 1f)
        assertFalse(toad.isGone)
    }

    @Test
    fun aTameBirdFliesInLandsAndStaysWhateverTheSnakeDoes() {
        val bird = Critter.Bird(3, BirdKind.BlueTit, -114f, 375f, target.x, target.y, staySeconds = 1f, isTame = true)
        assertTrue(bird.isAirborne)
        bird.play(30f, Vec2(-54f, 496f))
        assertFalse(bird.isAirborne)
        assertEquals(target.x, bird.x, 1e-3f)
        assertEquals(target.y, bird.y, 1e-3f)
        assertFalse(bird.isFleeing)
        assertFalse(bird.isGone)
        assertTrue(bird.isEdible)
    }
}
