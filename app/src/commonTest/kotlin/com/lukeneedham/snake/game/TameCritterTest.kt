package com.lukeneedham.snake.game

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TameCritterTest {

    private val arena = Arena(108f, 234f)
    private val random = Random(3)

    private fun Critter.play(seconds: Float, snakeHead: Vec2?) {
        repeat((seconds * 60f).toInt()) { update(1f / 60f, arena, random, snakeHead) }
    }

    @Test
    fun aTameMouseStaysWhereItIsEvenWithTheSnakeRightNextToIt() {
        val mouse = Critter.Mouse(1, 54f, 300f, lifetime = 1f, isTame = true)
        mouse.play(20f, Vec2(54f, 296f))
        assertEquals(54f, mouse.x)
        assertEquals(300f, mouse.y)
        assertFalse(mouse.isGone)
        assertFalse(mouse.isFleeing)
        assertTrue(mouse.isEdible)
    }

    @Test
    fun aTameToadDoesNotHopOrLeave() {
        val toad = Critter.Toad(2, ToadKind.Pink, 54f, 300f, lifetime = 1f, isTame = true)
        toad.play(20f, Vec2(54f, 296f))
        assertEquals(54f, toad.x)
        assertEquals(300f, toad.y)
        assertFalse(toad.isGone)
    }

    @Test
    fun aTameBirdFliesInLandsAndStaysWhateverTheSnakeDoes() {
        val bird = Critter.Bird(3, BirdKind.BlueTit, 80f, 250f, 54f, 300f, staySeconds = 1f, isTame = true)
        assertTrue(bird.isAirborne)
        bird.play(30f, Vec2(54f, 296f))
        assertFalse(bird.isAirborne)
        assertEquals(54f, bird.x, 1e-3f)
        assertEquals(300f, bird.y, 1e-3f)
        assertFalse(bird.isFleeing)
        assertFalse(bird.isGone)
        assertTrue(bird.isEdible)
    }

    @Test
    fun shiftingACritterMovesItDown() {
        val mouse = Critter.Mouse(1, 54f, 300f, lifetime = 1f, isTame = true)
        mouse.shiftDown(80f)
        assertEquals(380f, mouse.y)
        val bird = Critter.Bird(3, BirdKind.BlueTit, 80f, 250f, 54f, 300f, staySeconds = 1f, isTame = true)
        bird.shiftDown(80f)
        bird.play(30f, null)
        assertEquals(380f, bird.y, 1e-3f)
    }
}
