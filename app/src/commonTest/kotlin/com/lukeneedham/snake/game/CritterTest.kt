package com.lukeneedham.snake.game

import kotlin.random.Random
import kotlin.test.Test
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
}
