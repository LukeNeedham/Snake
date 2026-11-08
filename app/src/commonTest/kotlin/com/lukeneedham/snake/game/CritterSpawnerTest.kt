package com.lukeneedham.snake.game

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CritterSpawnerTest {

    private val arena = Arena(108f, 240f)
    private val spawner = CritterSpawner(arena, Random(1))
    private val critters = mutableListOf<Critter>()

    @Test
    fun theFirstMouseComesQuickly() {
        spawner.reset()
        spawner.update(0.1f, critters, score = 0)
        assertTrue(critters.isEmpty())
        spawner.update(0.5f, critters, score = 0)
        assertEquals(1, critters.size)
        assertTrue(critters.single() is Critter.Mouse)
    }

    @Test
    fun thereIsOnlyOneMouseAndOneToadAtTheStart() {
        spawner.reset()
        repeat(150) { spawner.update(0.1f, critters, score = 0) }
        assertEquals(1, critters.count { it is Critter.Mouse })
        assertEquals(1, critters.count { it is Critter.Toad })
    }

    @Test
    fun morePointsBringMoreAnimals() {
        spawner.reset()
        repeat(300) { spawner.update(0.1f, critters, score = 30) }
        assertEquals(3, critters.count { it is Critter.Mouse })
        assertEquals(3, critters.count { it is Critter.Toad })
    }

    @Test
    fun thereIsOnlyOneBirdAtTheStartAndTwoWhenTheScoreIsHigh() {
        spawner.reset()
        repeat(400) { spawner.update(0.1f, critters, score = 0, snakeHead = Vec2(arena.centerX, arena.centerY)) }
        assertEquals(1, critters.count { it is Critter.Bird })
        repeat(400) { spawner.update(0.1f, critters, score = 40, snakeHead = Vec2(arena.centerX, arena.centerY)) }
        assertEquals(2, critters.count { it is Critter.Bird })
    }

    @Test
    fun birdsStartOffTheScreen() {
        spawner.reset()
        repeat(400) { spawner.update(0.1f, critters, score = 40) }
        val birds = critters.filterIsInstance<Critter.Bird>()
        assertTrue(birds.isNotEmpty())
        assertTrue(birds.all { it.x < 0f || it.x > arena.width || it.y < 0f || it.y > arena.height })
        assertTrue(birds.all { it.isAirborne })
    }

    @Test
    fun pheromonesBringMoreMice() {
        spawner.reset()
        repeat(300) { spawner.update(0.1f, critters, score = 0, attractsMice = true) }
        assertEquals(1 + PowerUpTuning.pheromoneExtraMice, critters.count { it is Critter.Mouse })
    }

    @Test
    fun animalsStartInTheGrass() {
        spawner.reset()
        repeat(150) { spawner.update(0.1f, critters, score = 0) }
        assertTrue(critters.all { arena.signedDistance(it.x, it.y) > 0f })
    }
}
