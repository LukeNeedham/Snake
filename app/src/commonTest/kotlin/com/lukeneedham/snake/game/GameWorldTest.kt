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
        assertEquals(ScoreTuning.mouse, world.score)
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
    fun aFingerHeldInTheSprintZoneSprintsAndDoesNotTurn() {
        val world = newWorld()
        world.start()
        val before = world.snake.turnDirection
        world.sprintPress(1L)
        repeat(60) { world.update(1f / 60f) }
        assertTrue(world.sprint.level > 0.9f)
        assertEquals(before, world.snake.turnDirection)
    }

    @Test
    fun theSprintStopsAsSoonAsTheFingerComesUp() {
        val world = newWorld()
        world.start()
        world.sprintPress(1L)
        repeat(60) { world.update(1f / 60f) }
        world.sprintRelease(1L)
        repeat(60) { world.update(1f / 60f) }
        assertEquals(0f, world.sprint.level, 1e-4f)
    }

    @Test
    fun aSprintingThumbAndATappingThumbWorkTogether() {
        val world = newWorld()
        world.start()
        val before = world.snake.turnDirection
        world.sprintPress(1L)
        repeat(30) { world.update(1f / 60f) }
        world.tap()
        assertEquals(-before, world.snake.turnDirection)
        assertTrue(world.sprint.level > 0.5f)
    }

    @Test
    fun aFingerPressedBeforeTheStartDoesNotSprint() {
        val world = newWorld()
        world.sprintPress(1L)
        world.start()
        repeat(60) { world.update(1f / 60f) }
        assertEquals(0f, world.sprint.level)
    }

    @Test
    fun pausingStopsTheSprint() {
        val world = newWorld()
        world.start()
        world.sprintPress(1L)
        repeat(30) { world.update(1f / 60f) }
        world.pause()
        world.resume()
        repeat(60) { world.update(1f / 60f) }
        assertEquals(0f, world.sprint.level, 1e-4f)
    }

    @Test
    fun sprintingGoesFasterAndDrainsTheHungerBarFaster() {
        val resting = newWorld().also { it.start() }
        val sprinting = newWorld().also { it.start() }
        sprinting.sprintPress(1L)
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

    @Test
    fun pausingOpensTheMenuAndResumingClosesIt() {
        val world = newWorld()
        world.start()
        world.openMenu()
        assertEquals(GamePhase.Paused, world.phase)
        assertTrue(world.isMenuOpen)
        assertEquals(listOf(MenuItem.Resume, MenuItem.Restart, MenuItem.Settings), world.menuItems)
        world.closeMenu()
        assertEquals(GamePhase.Playing, world.phase)
        assertFalse(world.isMenuOpen)
    }

    @Test
    fun theMenuOfTheTitleScreenOnlyHasResumeAndSettings() {
        val world = newWorld()
        world.openMenu()
        assertEquals(GamePhase.Ready, world.phase)
        assertTrue(world.isMenuOpen)
        assertEquals(listOf(MenuItem.Resume, MenuItem.Settings), world.menuItems)
        world.closeMenu()
        assertFalse(world.isMenuOpen)
        assertEquals(GamePhase.Ready, world.phase)
    }

    @Test
    fun theSprintCornerIsOnTheLeftUnlessSetOtherwise() {
        assertEquals(SprintSide.Left, newWorld().sprintSide)
    }

    @Test
    fun openingAndClosingTheMenuMakesTheSceneDrawAgain() {
        // A paused world is not stepped, so nothing else tells the drawing that the menu is there
        val world = newWorld()
        world.start()
        world.update(1f / 60f)
        val beforeOpening = world.tick
        world.openMenu()
        assertTrue(world.tick > beforeOpening)
        world.update(1f / 60f)
        val beforeClosing = world.tick
        world.closeMenu()
        assertTrue(world.tick > beforeClosing)
    }

    @Test
    fun theMenuOfTheTitleScreenMakesTheSceneDrawAgainToo() {
        val world = newWorld()
        val before = world.tick
        world.openMenu()
        assertTrue(world.tick > before)
    }

    private fun GameWorld.placeBirdInFrontOfTheMouth(kind: BirdKind) {
        // It is on the ground already: it sees the snake in its first step, but crouches before it takes off, and the mouth takes it then
        addCritter(Critter.Bird(99, kind, mouthX(), mouthY(), mouthX(), mouthY(), staySeconds = 100f, isLanded = true))
    }

    @Test
    fun eatingABirdScoresMoreThanAMouseAndGivesItsPowerUp() {
        val world = newWorld()
        world.start()
        repeat(300) { world.update(1f / 60f) }
        val scoreBefore = world.score
        val lengthBefore = world.snake.body.length
        assertFalse(world.powerUps.isOn(PowerUp.ToadRepellent))
        world.placeBirdInFrontOfTheMouth(BirdKind.Crow)
        repeat(30) { world.update(1f / 60f) }
        assertFalse(world.critters.any { it.id == 99 })
        assertTrue(world.score >= scoreBefore + ScoreTuning.bird)
        assertTrue(world.snake.body.length > lengthBefore)
        assertTrue(world.powerUps.isOn(PowerUp.ToadRepellent))
        assertTrue(world.powerUps.remaining(PowerUp.ToadRepellent) > PowerUp.ToadRepellent.durationSeconds - 1f)
    }

    @Test
    fun aBirdFillsTheBarALittleMoreThanAMouse() {
        assertTrue(HungerTuning.birdFills > HungerTuning.mouseFills)
        val world = newWorld()
        world.start()
        repeat(300) { world.update(1f / 60f) }
        val before = world.hunger.level
        world.placeBirdInFrontOfTheMouth(BirdKind.Robin)
        repeat(30) { world.update(1f / 60f) }
        assertTrue(world.hunger.level > before)
    }

    @Test
    fun aPowerUpRunsDownWhilePlayingAndNotWhilePaused() {
        val world = newWorld()
        world.start()
        world.powerUps.apply(BirdKind.Sparrow)
        repeat(60) { world.update(1f / 60f) }
        val remaining = world.powerUps.remaining(PowerUp.Sneaky)
        assertTrue(remaining < PowerUp.Sneaky.durationSeconds - 0.9f)
        world.pause()
        repeat(120) { world.update(1f / 60f) }
        assertEquals(remaining, world.powerUps.remaining(PowerUp.Sneaky))
    }

    @Test
    fun zoomMakesTheSnakeFaster() {
        val normal = newWorld().also { it.start() }
        val zoomed = newWorld().also {
            it.start()
            it.powerUps.apply(BirdKind.BlueTit)
        }
        repeat(60) {
            normal.update(1f / 60f)
            zoomed.update(1f / 60f)
        }
        assertTrue(zoomed.snake.totalDistance > normal.snake.totalDistance)
    }

    @Test
    fun theFasterSprintGoesFasterAndCostsLessHunger() {
        val normal = newWorld().also { it.start() }
        val faster = newWorld().also {
            it.start()
            it.powerUps.apply(BirdKind.Robin)
        }
        normal.sprintPress(1L)
        faster.sprintPress(1L)
        repeat(120) {
            normal.update(1f / 60f)
            faster.update(1f / 60f)
        }
        assertTrue(faster.snake.totalDistance > normal.snake.totalDistance)
        assertTrue(faster.hunger.level > normal.hunger.level)
    }
}
