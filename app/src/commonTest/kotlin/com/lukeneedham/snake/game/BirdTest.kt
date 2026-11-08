package com.lukeneedham.snake.game

import kotlin.math.sqrt
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class BirdTest {

    private val arena = Arena(108f, 234f)
    private val dt = 1f / 60f

    /** A bird that comes in from the left of the screen and lands in the middle of the oval. */
    private fun newBird(kind: BirdKind = BirdKind.Robin, staySeconds: Float = 100f) =
        Critter.Bird(1, kind, -BirdTuning.spawnMargin, arena.centerY, arena.centerX, arena.centerY, staySeconds)

    private fun Critter.run(seconds: Float, random: Random = Random(1), head: Vec2? = null, aura: SnakeAura = SnakeAura.None) {
        repeat((seconds / dt).toInt()) { update(dt, arena, random, head, aura) }
    }

    private fun Critter.land(): Critter {
        var frames = 0
        while (mode != CritterMode.Roaming && frames++ < 60 * 20) update(dt, arena, Random(1))
        return this
    }

    @Test
    fun aBirdFliesInHighAboveTheGroundAndCannotBeEaten() {
        val bird = newBird()
        assertTrue(bird.isAirborne)
        assertEquals(CritterMode.Entering, bird.mode)
        assertTrue(bird.height > BirdTuning.eatHeight)
        assertFalse(bird.isEdible)
        bird.run(0.5f)
        assertTrue(bird.isAirborne)
        assertFalse(bird.isEdible)
    }

    @Test
    fun aBirdLandsOnItsSpotAndCanThenBeEaten() {
        val bird = newBird()
        var landedFlag = false
        var frames = 0
        while (bird.mode != CritterMode.Roaming && frames++ < 60 * 20) {
            bird.update(dt, arena, Random(1))
            if (bird.landed) landedFlag = true
        }
        assertEquals(CritterMode.Roaming, bird.mode)
        assertTrue(landedFlag)
        assertFalse(bird.isAirborne)
        assertEquals(0f, bird.height)
        assertEquals(arena.centerX, bird.x, 1.5f)
        assertEquals(arena.centerY, bird.y, 1.5f)
        assertTrue(bird.isEdible)
    }

    @Test
    fun aBirdThatIsComingDownPicksAnotherSpotIfTheSnakeIsCloseToItsSpot() {
        val head = Vec2(arena.centerX, arena.centerY)
        val bird = newBird()
        val random = Random(5)
        var frames = 0
        while (bird.mode != CritterMode.Roaming && frames++ < 60 * 20) bird.update(dt, arena, random, head)
        assertEquals(CritterMode.Roaming, bird.mode)
        val dx = bird.x - head.x
        val dy = bird.y - head.y
        assertTrue(sqrt(dx * dx + dy * dy) >= BirdTuning.landClearance - 1f)
    }

    @Test
    fun aBirdOnTheGroundFliesOffWhenTheSnakeComesClose() {
        val bird = newBird().land()
        val head = Vec2(bird.x + 8f, bird.y)
        bird.update(dt, arena, Random(1), head)
        assertTrue(bird.isFleeing)
        // It crouches for a moment, and can still be eaten then
        assertTrue(bird.isEdible)
        assertFalse(bird.isAirborne)
        bird.run(BirdTuning.reactionSeconds + 0.3f, head = head)
        assertEquals(CritterMode.Leaving, bird.mode)
        assertTrue(bird.isAirborne)
        assertFalse(bird.isEdible)
    }

    @Test
    fun aBirdThatTookOffFliesAwayFromTheSnakeAndOutOfTheScreen() {
        val bird = newBird().land()
        val startX = bird.x
        // The snake is to the right of the bird, so the bird flies off to the left
        val head = Vec2(bird.x + 8f, bird.y)
        bird.run(1.5f, head = head)
        assertTrue(bird.x < startX - 5f)
        bird.run(6f, head = head)
        assertTrue(bird.isGone)
    }

    @Test
    fun aBirdDoesNotMindASnakeThatIsFarAway() {
        val bird = newBird().land()
        bird.run(2f, head = Vec2(bird.x + 60f, bird.y))
        assertEquals(CritterMode.Roaming, bird.mode)
        assertFalse(bird.isFleeing)
    }

    @Test
    fun aSneakySnakeCanGetCloserBeforeABirdFliesOff() {
        val normal = newBird().land()
        val sneaked = newBird().land()
        // Closer than the alarm distance of a robin, but not by the sneaky snake's much smaller one
        val head = Vec2(normal.x + 10f, normal.y)
        normal.update(dt, arena, Random(1), head)
        sneaked.update(dt, arena, Random(1), Vec2(sneaked.x + 10f, sneaked.y), SnakeAura(noticeScale = PowerUpTuning.sneakyNoticeFactor))
        assertTrue(normal.isFleeing)
        assertFalse(sneaked.isFleeing)
    }

    @Test
    fun aBirdFliesOffByItselfAfterItsStay() {
        val bird = newBird(staySeconds = 3f)
        bird.run(40f)
        assertTrue(bird.isGone)
    }

    @Test
    fun aBirdHopsAboutWhileItSits() {
        val bird = newBird().land()
        var hopped = false
        var sat = false
        val random = Random(2)
        repeat(60 * 20) {
            bird.update(dt, arena, random)
            if (bird.height > 0.5f) hopped = true
            if (!bird.isMoving) sat = true
        }
        assertTrue(hopped)
        assertTrue(sat)
        assertNotEquals(CritterMode.Leaving, bird.mode)
    }

    @Test
    fun everyKindOfBirdGivesItsOwnPowerUp() {
        assertEquals(BirdKind.entries.size, BirdKind.entries.map { it.powerUp }.toSet().size)
        assertEquals(PowerUp.entries.toSet(), BirdKind.entries.map { it.powerUp }.toSet())
    }

    @Test
    fun aBirdNeverLandsRightInFrontOfTheSnake() {
        val head = Vec2(arena.centerX, arena.centerY)
        val random = Random(8)
        repeat(200) {
            val spot = BirdLanding.spot(arena, random, head)
            val dx = spot.x - head.x
            val dy = spot.y - head.y
            assertTrue(sqrt(dx * dx + dy * dy) >= BirdTuning.landClearance)
            assertTrue(arena.signedDistance(spot.x, spot.y) < 0f)
        }
    }
}
