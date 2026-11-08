package com.lukeneedham.snake.game

import kotlin.math.abs
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PowerUpTest {

    private val arena = Arena(108f, 234f)
    private val dt = 1f / 60f

    @Test
    fun aPowerUpIsOnUntilItsTimeRunsOut() {
        val powerUps = PowerUpTracker()
        powerUps.apply(BirdKind.BlueTit)
        assertTrue(powerUps.isOn(PowerUp.Zoom))
        assertEquals(PowerUp.Zoom.durationSeconds, powerUps.remaining(PowerUp.Zoom))
        powerUps.update(1f, isCountingDown = true)
        assertEquals(PowerUp.Zoom.durationSeconds - 1f, powerUps.remaining(PowerUp.Zoom))
        powerUps.update(100f, isCountingDown = true)
        assertFalse(powerUps.isOn(PowerUp.Zoom))
    }

    @Test
    fun theTimeDoesNotRunOutWhileItIsNotCountingDown() {
        val powerUps = PowerUpTracker()
        powerUps.apply(BirdKind.Robin)
        powerUps.update(5f, isCountingDown = false)
        assertEquals(PowerUp.FastSprint.durationSeconds, powerUps.remaining(PowerUp.FastSprint))
    }

    @Test
    fun powerUpsRunAtTheSameTimeAndRunOutOnTheirOwn() {
        val powerUps = PowerUpTracker()
        powerUps.apply(BirdKind.BlueTit)
        powerUps.apply(BirdKind.Crow)
        powerUps.update(PowerUp.Zoom.durationSeconds + 0.5f, isCountingDown = true)
        assertFalse(powerUps.isOn(PowerUp.Zoom))
        assertTrue(powerUps.isOn(PowerUp.ToadFree))
    }

    @Test
    fun eatingTheSameBirdAgainStartsTheTimeOver() {
        val powerUps = PowerUpTracker()
        powerUps.apply(BirdKind.Sparrow)
        powerUps.update(5f, isCountingDown = true)
        powerUps.apply(BirdKind.Sparrow)
        assertEquals(PowerUp.Sneaky.durationSeconds, powerUps.remaining(PowerUp.Sneaky))
    }

    @Test
    fun eatingABirdShowsItsBanner() {
        val powerUps = PowerUpTracker()
        powerUps.apply(BirdKind.Pigeon)
        assertEquals(BirdKind.Pigeon, powerUps.bannerKind)
        assertTrue(powerUps.bannerTime > 0f)
    }

    @Test
    fun theBoostsFollowThePowerUpsThatAreOn() {
        val powerUps = PowerUpTracker()
        val boosts = PowerUpBoosts(powerUps)
        assertEquals(1f, boosts.speedFactor)
        assertEquals(1f, boosts.mouthFactor)
        assertEquals(SprintTuning.speedBoost, boosts.sprintBoost)
        assertFalse(boosts.attractsMice)
        assertFalse(boosts.mouthIsFull)
        assertFalse(boosts.keepsToadsAway)
        assertFalse(boosts.aura.attractsMice)
        assertEquals(1f, boosts.aura.noticeScale)
        powerUps.apply(BirdKind.BlueTit)
        powerUps.apply(BirdKind.Robin)
        powerUps.apply(BirdKind.Sparrow)
        powerUps.apply(BirdKind.Blackbird)
        powerUps.apply(BirdKind.Crow)
        powerUps.apply(BirdKind.Pigeon)
        assertEquals(PowerUpTuning.zoomSpeedFactor, boosts.speedFactor)
        assertEquals(PowerUpTuning.fastSprintBoost, boosts.sprintBoost)
        assertEquals(PowerUpTuning.fastSprintDrainFactor, boosts.sprintDrainFactor)
        assertEquals(PowerUpTuning.biggerMouthFactor, boosts.mouthFactor)
        assertEquals(PowerUpTuning.sneakyNoticeFactor, boosts.aura.noticeScale)
        assertTrue(boosts.keepsToadsAway)
        assertTrue(boosts.attractsMice)
        assertTrue(boosts.mouthIsFull)
        assertTrue(boosts.aura.attractsMice)
    }

    @Test
    fun aSneakySnakeIsNotNoticedByAMouseFromAsFarAway() {
        val mouse = Critter.Mouse(1, arena.centerX, arena.centerY, lifetime = 100f)
        // A mouse looks to the right, and a snake 20 units away is closer than it notices normally
        val head = Vec2(arena.centerX + 20f, arena.centerY)
        mouse.update(dt, arena, Random(1), head, SnakeAura(noticeScale = PowerUpTuning.sneakyNoticeFactor))
        assertFalse(mouse.isFleeing)
        mouse.update(dt, arena, Random(1), head)
        assertTrue(mouse.isFleeing)
    }

    private val cheeseAura = SnakeAura(attractsMice = true)

    @Test
    fun aMouseRunsTowardsASnakeWithCheeseEvenFromBehindItsBack() {
        val mouse = Critter.Mouse(1, arena.centerX, arena.centerY, lifetime = 100f)
        // A new mouse looks to the right, and this snake is behind it and far away
        val head = Vec2(arena.centerX - 50f, arena.centerY)
        repeat(30) { mouse.update(dt, arena, Random(1), head, cheeseAura) }
        assertFalse(mouse.isFleeing)
        assertTrue(mouse.x < arena.centerX - 5f)
    }

    @Test
    fun miceStandAroundASnakeWithCheeseInsteadOfRunningThroughIt() {
        val mouse = Critter.Mouse(1, arena.centerX, arena.centerY, lifetime = 100f)
        val head = Vec2(arena.centerX + 30f, arena.centerY)
        repeat(60 * 4) { mouse.update(dt, arena, Random(1), head, cheeseAura) }
        assertTrue(mouse.x > head.x - CritterTuning.cheeseCrowdDistance - 1f)
        assertTrue(mouse.x < head.x + 1f)
        assertFalse(mouse.isMoving)
    }

    @Test
    fun miceThatGatheredStayDazedForAMomentWhenTheCheeseIsGone() {
        val mouse = Critter.Mouse(1, arena.centerX, arena.centerY, lifetime = 100f)
        val head = Vec2(arena.centerX + 3f, arena.centerY)
        repeat(10) { mouse.update(dt, arena, Random(1), head, cheeseAura) }
        // Without the cheese the snake is close to the mouse, which would flee from it, but it is dazed
        repeat(30) {
            mouse.update(dt, arena, Random(1), head)
            assertFalse(mouse.isFleeing)
        }
    }

    @Test
    fun aToadIgnoresASnakeWithCheese() {
        val toad = Critter.Toad(1, ToadKind.Teal, arena.centerX, arena.centerY, lifetime = 100f)
        val head = Vec2(arena.centerX + 10f, arena.centerY)
        val random = Random(4)
        repeat(60) { toad.update(dt, arena, random, head, cheeseAura) }
        assertTrue(abs(toad.x - arena.centerX) < 14f)
    }
}
