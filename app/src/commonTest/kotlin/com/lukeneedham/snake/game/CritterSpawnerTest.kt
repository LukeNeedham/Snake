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
    fun birdsAreRareAndTheFirstOneComesLate() {
        spawner.reset()
        repeat(((SpawnTuning.firstBirdDelay - 1f) * 10).toInt()) { spawner.update(0.1f, critters, score = 0, snakeHead = Vec2(arena.centerX, arena.centerY)) }
        assertEquals(0, critters.count { it is Critter.Bird })
        repeat(30) { spawner.update(0.1f, critters, score = 0, snakeHead = Vec2(arena.centerX, arena.centerY)) }
        assertEquals(1, critters.count { it is Critter.Bird })
    }

    @Test
    fun thereIsNeverMoreThanOneBirdEvenWithAHighScore() {
        spawner.reset()
        val head = Vec2(arena.centerX, arena.centerY)
        repeat(6000) {
            // Nothing in this test moves the birds, so the first one stays for the whole ten minutes
            spawner.update(0.1f, critters, score = 100, snakeHead = head)
            assertTrue(critters.count { it is Critter.Bird } <= 1)
        }
        assertEquals(1, critters.count { it is Critter.Bird })
    }

    @Test
    fun birdsStartOffTheScreen() {
        spawner.reset()
        repeat(((SpawnTuning.firstBirdDelay + 1f) * 10).toInt()) { spawner.update(0.1f, critters, score = 40) }
        val birds = critters.filterIsInstance<Critter.Bird>()
        assertTrue(birds.isNotEmpty())
        assertTrue(birds.all { it.x < 0f || it.x > arena.width || it.y < 0f || it.y > arena.height })
        assertTrue(birds.all { it.isAirborne })
    }

    @Test
    fun noToadComesWhileToadsAreNotAllowedAndTheirTimerWaits() {
        spawner.reset()
        repeat(300) { spawner.update(0.1f, critters, score = 0, allowsToads = false) }
        assertEquals(0, critters.count { it is Critter.Toad })
        // The toad timer did not run while it was off, so the first toad still needs its whole delay
        spawner.update(SpawnTuning.firstToadDelay - 1f, critters, score = 0)
        assertEquals(0, critters.count { it is Critter.Toad })
        spawner.update(1.5f, critters, score = 0)
        assertEquals(1, critters.count { it is Critter.Toad })
    }

    @Test
    fun cheeseBringsMoreMice() {
        spawner.reset()
        repeat(300) { spawner.update(0.1f, critters, score = 0, attractsMice = true) }
        assertEquals(1 + PowerUpTuning.cheeseExtraMice, critters.count { it is Critter.Mouse })
    }

    @Test
    fun animalsStartInTheGrass() {
        spawner.reset()
        repeat(150) { spawner.update(0.1f, critters, score = 0) }
        assertTrue(critters.all { arena.signedDistance(it.x, it.y) > 0f })
    }
}
