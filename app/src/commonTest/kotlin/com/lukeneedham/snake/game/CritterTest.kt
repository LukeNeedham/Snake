package com.lukeneedham.snake.game

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CritterTest {

    private val arena = Arena(108f, 234f)

    @Test
    fun aMouseRunsOutOfTheGrassAndIntoTheOval() {
        val mouse = Critter.Mouse(1, arena.centerX + arena.radiusX + 5f, arena.centerY, lifetime = 100f)
        val random = Random(3)
        var entered = false
        repeat(60 * 10) {
            mouse.update(1f / 60f, arena, random)
            if (mouse.mode == CritterMode.Roaming) entered = true
        }
        assertTrue(entered)
    }

    @Test
    fun aMouseGoesBackIntoTheGrassAfterItsVisit() {
        val mouse = Critter.Mouse(1, arena.centerX + arena.radiusX + 5f, arena.centerY, lifetime = 3f)
        val random = Random(5)
        repeat(60 * 60) { if (!mouse.isGone) mouse.update(1f / 60f, arena, random) }
        assertTrue(mouse.isGone)
    }

    @Test
    fun aToadHopsInTheAirAndSitsStill() {
        val toad = Critter.Toad(1, ToadKind.Teal, arena.centerX, arena.centerY, lifetime = 100f)
        val random = Random(9)
        var hopped = false
        var sat = false
        repeat(60 * 20) {
            toad.update(1f / 60f, arena, random)
            if (toad.height > 0.5f) hopped = true
            if (!toad.isMoving) sat = true
        }
        assertTrue(hopped)
        assertTrue(sat)
    }

    @Test
    fun aMouseRunsFromASnakeInFrontOfIt() {
        val mouse = Critter.Mouse(1, arena.centerX, arena.centerY, lifetime = 100f)
        val snakeHead = Vec2(arena.centerX + 10f, arena.centerY)
        mouse.update(1f / 60f, arena, Random(1), snakeHead)
        assertTrue(mouse.isFleeing)
        assertTrue(mouse.x < arena.centerX)
    }

    @Test
    fun aMouseDoesNotNoticeASnakeBehindIt() {
        val mouse = Critter.Mouse(1, arena.centerX, arena.centerY, lifetime = 100f)
        // A new mouse looks to the right
        val snakeHead = Vec2(arena.centerX - 10f, arena.centerY)
        mouse.update(1f / 60f, arena, Random(1), snakeHead)
        assertFalse(mouse.isFleeing)
    }

    @Test
    fun aMouseDoesNotNoticeASnakeThatIsFarAway() {
        val mouse = Critter.Mouse(1, arena.centerX, arena.centerY, lifetime = 100f)
        val snakeHead = Vec2(arena.centerX + CritterTuning.noticeDistance + 5f, arena.centerY)
        mouse.update(1f / 60f, arena, Random(1), snakeHead)
        assertFalse(mouse.isFleeing)
    }

    @Test
    fun aMouseStillInTheGrassDoesNotRunFromTheSnake() {
        val mouse = Critter.Mouse(1, arena.centerX + arena.radiusX + 5f, arena.centerY, lifetime = 100f)
        val snakeHead = Vec2(mouse.x + 10f, mouse.y)
        mouse.update(1f / 60f, arena, Random(1), snakeHead)
        assertFalse(mouse.isFleeing)
    }

    @Test
    fun aFleeingMouseStopsRunningAfterAWhile() {
        val mouse = Critter.Mouse(1, arena.centerX, arena.centerY, lifetime = 100f)
        mouse.update(1f / 60f, arena, Random(1), Vec2(arena.centerX + 10f, arena.centerY))
        assertTrue(mouse.isFleeing)
        repeat(60) { mouse.update(1f / 60f, arena, Random(1)) }
        assertFalse(mouse.isFleeing)
    }
}
