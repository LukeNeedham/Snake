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
    fun animalsStartInTheGrass() {
        spawner.reset()
        repeat(150) { spawner.update(0.1f, critters, score = 0) }
        assertTrue(critters.all { arena.signedDistance(it.x, it.y) > 0f })
    }
}
