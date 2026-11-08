package com.lukeneedham.snake.game

import kotlin.math.cos
import kotlin.math.sin
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GameWorldTest {

    private fun newWorld() = GameWorld(Arena(108f, 234f), seed = 11)

    private fun GameWorld.mouthX() = snake.x + cos(snake.heading) * SnakeTuning.mouthReach

    /** Critters are 3 units tall, so their feet are 3 units below the mouth. */
    private fun GameWorld.mouthY() = snake.y + sin(snake.heading) * SnakeTuning.mouthReach + 3f

    private fun GameWorld.placeMouseInFrontOfTheMouth() {
        addCritter(Critter.Mouse(99, mouthX(), mouthY(), lifetime = 100f))
    }

    private fun GameWorld.placeToadInFrontOfTheMouth(kind: ToadKind) {
        addCritter(Critter.Toad(99, kind, mouthX(), mouthY(), lifetime = 100f))
    }

    @Test
    fun theGameStartsWhenStarted() {
        val world = newWorld()
        assertEquals(GamePhase.Ready, world.phase)
        world.start()
        assertEquals(GamePhase.Playing, world.phase)
    }

    @Test
    fun aTapTurnsTheSnakeTheOtherWay() {
        val world = newWorld()
        world.start()
        val before = world.snake.turnDirection
        world.tap()
        assertEquals(-before, world.snake.turnDirection)
    }

    @Test
    fun aTapBeforeTheStartDoesNothing() {
        val world = newWorld()
        val before = world.snake.turnDirection
        world.tap()
        assertEquals(before, world.snake.turnDirection)
    }

    @Test
    fun eatingAMouseScoresAndGrowsTheSnake() {
        val world = newWorld()
        world.start()
        val lengthBefore = world.snake.body.length
        world.placeMouseInFrontOfTheMouth()
        repeat(30) { world.update(1f / 60f) }
        assertEquals(1, world.score)
        assertFalse(world.critters.any { it.id == 99 })
        assertTrue(world.snake.body.length > lengthBefore)
    }

    @Test
    fun eatingAToadStartsItsEffectAndScoresNothing() {
        val world = newWorld()
        world.start()
        world.placeToadInFrontOfTheMouth(ToadKind.Pink)
        world.update(1f / 60f)
        assertEquals(0, world.score)
        assertTrue(world.effects.remaining(Effect.Confused) > 0f)
    }

    @Test
    fun aConfusedSnakeIgnoresTaps() {
        val world = newWorld()
        world.start()
        world.applyEffect(ToadKind.Pink)
        val before = world.snake.turnDirection
        world.tap()
        assertEquals(before, world.snake.turnDirection)
    }

    @Test
    fun anEffectFadesInAndRunsDown() {
        val world = newWorld()
        world.start()
        world.applyEffect(ToadKind.Purple)
        repeat(60) { world.update(1f / 60f) }
        assertTrue(world.effects.level(Effect.Hallucinating) > 0.9f)
        assertTrue(world.effects.remaining(Effect.Hallucinating) < Effect.Hallucinating.durationSeconds - 0.9f)
    }

    @Test
    fun theGameEndsWhenTheSnakeEatsItself() {
        val world = newWorld()
        world.start()
        world.snake.grow(20)
        repeat(60 * 30) { if (world.phase == GamePhase.Playing) world.update(1f / 60f) }
        assertEquals(GamePhase.GameOver, world.phase)
        repeat(60) { world.update(1f / 60f) }
        assertTrue(world.canRestart)
    }

    @Test
    fun aPausedWorldDoesNotMove() {
        val world = newWorld()
        world.start()
        world.update(1f / 60f)
        world.pause()
        val x = world.snake.x
        repeat(60) { world.update(1f / 60f) }
        assertEquals(x, world.snake.x)
        world.resume()
        assertEquals(GamePhase.Playing, world.phase)
    }

    @Test
    fun aQuickPressTurnsTheSnakeWhenTheFingerComesUp() {
        val world = newWorld()
        world.start()
        val before = world.snake.turnDirection
        world.press(1L)
        repeat(3) { world.update(1f / 60f) }
        assertEquals(before, world.snake.turnDirection)
        world.release(1L)
        assertEquals(-before, world.snake.turnDirection)
    }

    @Test
    fun aHeldFingerSprintsAndDoesNotTurn() {
        val world = newWorld()
        world.start()
        val before = world.snake.turnDirection
        world.press(1L)
        repeat(60) { world.update(1f / 60f) }
        assertTrue(world.sprint.level > 0.9f)
        world.release(1L)
        assertEquals(before, world.snake.turnDirection)
    }

    @Test
    fun sprintingGoesFasterAndDrainsTheHungerBarFaster() {
        val resting = newWorld().also { it.start() }
        val sprinting = newWorld().also { it.start() }
        sprinting.press(1L)
        repeat(120) {
            resting.update(1f / 60f)
            sprinting.update(1f / 60f)
        }
        assertTrue(sprinting.snake.totalDistance > resting.snake.totalDistance)
        assertTrue(sprinting.hunger.level < resting.hunger.level)
    }

    @Test
    fun theSnakeGetsHungryAsTimePasses() {
        val world = newWorld()
        world.start()
        repeat(300) { world.update(1f / 60f) }
        assertTrue(world.hunger.level < 1f)
    }

    @Test
    fun eatingAMouseFillsTheHungerBar() {
        val world = newWorld()
        world.start()
        repeat(300) { world.update(1f / 60f) }
        val before = world.hunger.level
        world.placeMouseInFrontOfTheMouth()
        repeat(30) { world.update(1f / 60f) }
        assertTrue(world.hunger.level > before)
    }

    @Test
    fun eatingAToadFillsTheBarOnlyALittle() {
        val world = newWorld()
        world.start()
        repeat(300) { world.update(1f / 60f) }
        val before = world.hunger.level
        world.placeToadInFrontOfTheMouth(ToadKind.Teal)
        world.update(1f / 60f)
        val gained = world.hunger.level - before
        assertTrue(gained > 0f)
        assertTrue(gained < HungerTuning.mouseFills / 2f)
    }

    @Test
    fun aPausedSnakeDoesNotGetHungry() {
        val world = newWorld()
        world.start()
        world.update(1f / 60f)
        world.pause()
        val before = world.hunger.level
        repeat(120) { world.update(1f / 60f) }
        assertEquals(before, world.hunger.level)
    }
}
