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
    fun eatingAToadStartsItsPowerDownAndScoresNothing() {
        val world = newWorld()
        world.start()
        world.placeToadInFrontOfTheMouth(ToadKind.Pink)
        world.update(1f / 60f)
        assertEquals(0, world.score)
        assertTrue(world.powerDowns.remaining(PowerDown.Confused) > 0f)
    }

    @Test
    fun aConfusedSnakeIgnoresTaps() {
        val world = newWorld()
        world.start()
        world.applyPowerDown(ToadKind.Pink)
        val before = world.snake.turnDirection
        world.tap()
        assertEquals(before, world.snake.turnDirection)
    }

    @Test
    fun aPowerDownFadesInAndRunsDown() {
        val world = newWorld()
        world.start()
        world.applyPowerDown(ToadKind.Purple)
        repeat(60) { world.update(1f / 60f) }
        assertTrue(world.powerDowns.level(PowerDown.Hallucinating) > 0.9f)
        assertTrue(world.powerDowns.remaining(PowerDown.Hallucinating) < PowerDown.Hallucinating.durationSeconds - 0.9f)
    }

    /** Runs a long snake round its circle for [seconds], keeping it fed so that only cannibal can kill it; true if it hit itself. */
    private fun GameWorld.runLongSnakeFed(seconds: Int, isCannibal: Boolean): Boolean {
        snake.grow(20)
        var touched = false
        repeat(60 * seconds) {
            if (phase == GamePhase.Playing) {
                hunger.feed(1f)
                // Toad-free keeps the toads away, so that no other power down changes the outcome
                if (isCannibal) applyPowerDown(ToadKind.Brown) else powerUps.apply(BirdKind.Crow)
                update(1f / 60f)
                if (snake.hitsItself()) touched = true
            }
        }
        return touched
    }

    @Test
    fun theSnakeSurvivesTouchingItselfWithoutCannibal() {
        val world = newWorld()
        world.start()
        assertTrue(world.runLongSnakeFed(seconds = 30, isCannibal = false))
        assertEquals(GamePhase.Playing, world.phase)
    }

    @Test
    fun theGameEndsWhenACannibalSnakeTouchesItself() {
        val world = newWorld()
        world.start()
        world.runLongSnakeFed(seconds = 30, isCannibal = true)
        assertEquals(GamePhase.GameOver, world.phase)
        assertEquals(DeathCause.Cannibalism, world.deathCause)
        repeat(60) { world.update(1f / 60f) }
        assertTrue(world.canRestart)
    }

    @Test
    fun aSnakeWithHayfeverDiesWhenItTouchesTheGrass() {
        val world = GameWorld(Arena(60f, 60f), seed = 11)
        world.start()
        repeat(60 * 30) {
            if (world.phase == GamePhase.Playing) {
                world.hunger.feed(1f)
                world.applyPowerDown(ToadKind.Grey)
                world.update(1f / 60f)
            }
        }
        assertEquals(GamePhase.GameOver, world.phase)
        assertEquals(DeathCause.Allergies, world.deathCause)
    }

    @Test
    fun aSnakeThatRunsOutOfHungerDiesOfStarvation() {
        val world = newWorld()
        world.start()
        assertEquals(null, world.deathCause)
        world.hunger.update(100f, sprintLevel = 0f)
        world.update(1f / 60f)
        assertEquals(GamePhase.GameOver, world.phase)
        assertEquals(DeathCause.Starvation, world.deathCause)
    }

    @Test
    fun aGreedySnakeGetsOnlyHalfTheFood() {
        fun hungerAfterAMouse(greedy: Boolean): Float {
            val world = newWorld()
            world.hunger.update(10f, sprintLevel = 0f)
            world.start()
            if (greedy) world.applyPowerDown(ToadKind.Red)
            world.placeMouseInFrontOfTheMouth()
            repeat(30) { world.update(1f / 60f) }
            assertEquals(1, world.score)
            return world.hunger.level
        }
        assertTrue(hungerAfterAMouse(greedy = true) < hungerAfterAMouse(greedy = false))
    }

    @Test
    fun aLazySnakeGoesSlower() {
        fun distanceInASecond(lazy: Boolean): Float {
            val world = GameWorld(Arena(400f, 400f), seed = 11)
            world.start()
            if (lazy) world.applyPowerDown(ToadKind.Olive)
            repeat(60) { world.update(1f / 60f) }
            val start = world.snake.totalDistance
            repeat(60) { world.update(1f / 60f) }
            return world.snake.totalDistance - start
        }
        assertTrue(distanceInASecond(lazy = true) < distanceInASecond(lazy = false) * 0.8f)
    }

    @Test
    fun eatingAGreyToadGivesHayfever() {
        val world = newWorld()
        world.start()
        world.placeToadInFrontOfTheMouth(ToadKind.Grey)
        world.update(1f / 60f)
        assertTrue(world.powerDowns.isOn(PowerDown.Hayfever))
    }

    @Test
    fun aSnakeCarryingCheeseCannotEatAndEatsAgainWhenItIsGone() {
        val world = newWorld()
        world.start()
        world.powerUps.apply(BirdKind.Pigeon)
        world.placeMouseInFrontOfTheMouth()
        repeat(30) { world.update(1f / 60f) }
        assertEquals(0, world.score)
        assertTrue(world.snake.mouthOpen < 0.05f)
        repeat(60 * 10) { world.update(1f / 60f) }
        assertFalse(world.powerUps.isOn(PowerUp.Cheese))
    }

    @Test
    fun eatingTheCrowBlowsUpEveryToadAndLeavesTheMice() {
        val world = newWorld()
        world.start()
        repeat(300) { world.update(1f / 60f) }
        world.addCritter(Critter.Toad(70, ToadKind.Purple, 30f, 60f, lifetime = 100f))
        world.addCritter(Critter.Toad(71, ToadKind.Pink, 80f, 150f, lifetime = 100f))
        world.addCritter(Critter.Mouse(72, 50f, 100f, lifetime = 100f))
        val scoreBefore = world.score
        world.placeBirdInFrontOfTheMouth(BirdKind.Crow)
        repeat(30) { world.update(1f / 60f) }
        assertTrue(world.powerUps.isOn(PowerUp.ToadFree))
        assertFalse(world.critters.any { it is Critter.Toad })
        assertTrue(world.critters.any { it.id == 72 })
        assertTrue(world.score >= scoreBefore + ScoreTuning.bird)
        assertTrue(world.particleSystem.particles.size >= 40)
    }

    @Test
    fun noToadComesOutOfTheGrassWhileToadFreeIsOn() {
        val world = newWorld()
        world.start()
        world.powerUps.apply(BirdKind.Crow)
        repeat(60 * 11) {
            world.hunger.feed(1f)
            world.update(1f / 60f)
            assertFalse(world.critters.any { it is Critter.Toad })
        }
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
        assertFalse(world.powerUps.isOn(PowerUp.ToadFree))
        world.placeBirdInFrontOfTheMouth(BirdKind.Crow)
        repeat(30) { world.update(1f / 60f) }
        assertFalse(world.critters.any { it.id == 99 })
        assertTrue(world.score >= scoreBefore + ScoreTuning.bird)
        assertTrue(world.snake.body.length > lengthBefore)
        assertTrue(world.powerUps.isOn(PowerUp.ToadFree))
        assertTrue(world.powerUps.remaining(PowerUp.ToadFree) > PowerUp.ToadFree.durationSeconds - 1f)
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
