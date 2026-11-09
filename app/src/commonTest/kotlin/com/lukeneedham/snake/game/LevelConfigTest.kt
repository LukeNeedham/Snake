package com.lukeneedham.snake.game

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LevelConfigTest {

    private val screen = Arena(108f, 234f)

    private fun GameWorld.play(seconds: Float) {
        repeat((seconds * 60f).toInt()) { update(1f / 60f) }
    }

    @Test
    fun aLevelSetsHowFastTheSnakeGetsHungry() {
        val slow = GameWorld(screen, seed = 3)
        val fast = GameWorld(screen, seed = 3, level = LevelConfig(hunger = HungerRules(drainPerSecond = HungerTuning.drainPerSecond * 4f)))
        slow.start()
        fast.start()
        slow.play(2f)
        fast.play(2f)
        assertTrue(fast.hunger.level < slow.hunger.level - 0.1f)
    }

    @Test
    fun aLevelSetsHowManyPointsAMouseIsWorth() {
        val world = GameWorld(screen, seed = 3, level = LevelConfig(score = ScoreRules(mouse = 7)))
        world.start()
        val head = world.snake
        world.addCritter(Critter.Mouse(1, head.x + 4.6f * kotlin.math.cos(head.heading), head.y + 4.6f * kotlin.math.sin(head.heading) + 3f, lifetime = 100f))
        world.play(0.1f)
        assertEquals(7, world.score)
    }

    @Test
    fun aLevelSetsWhichToadsComeOutOfTheGrass() {
        val level = LevelConfig(toads = ToadRules(kinds = listOf(ToadKind.Pink)))
        val spawner = CritterSpawner(screen, Random(1), level)
        val critters = mutableListOf<Critter>()
        spawner.reset()
        repeat(3000) {
            spawner.update(0.1f, critters, score = 5000)
            critters.removeAll { it !is Critter.Toad }
        }
        val toads = critters.filterIsInstance<Critter.Toad>()
        assertTrue(toads.isNotEmpty())
        assertTrue(toads.all { it.kind == ToadKind.Pink })
    }

    @Test
    fun aLevelSetsHowLongAPowerDownLasts() {
        val tracker = PowerDownTracker(PowerDownRules(durations = mapOf(PowerDown.Confused to 20f)))
        tracker.apply(ToadKind.Pink)
        assertEquals(20f, tracker.remaining(PowerDown.Confused))
        assertEquals(20f, tracker.duration(PowerDown.Confused))
        assertEquals(PowerDown.Lazy.durationSeconds, tracker.duration(PowerDown.Lazy))
    }

    @Test
    fun aLevelSetsHowBigTheStageIs() {
        val world = GameWorld(screen, seed = 3, level = LevelConfig(stageShape = StageShape(widthFraction = 0.6f, heightFraction = 0.5f)))
        assertEquals(screen.radiusX * 0.6f, world.arena.radiusX, 1e-3f)
        assertEquals(screen.radiusY * 0.5f, world.arena.radiusY, 1e-3f)
        // The room of the tutorial stays as big as the screen allows
        assertEquals(screen.radiusX, world.layout.room.radiusX, 1e-3f)
        // The way in still reaches the stage
        assertTrue(world.layout.terrain.signedDistance(world.arena.centerX, world.arena.ovalBottom - 3f) < 0f)
        assertTrue(world.layout.terrain.signedDistance(world.arena.centerX, world.arena.ovalBottom + 10f) < 0f)
    }
}
