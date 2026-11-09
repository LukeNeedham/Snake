package com.lukeneedham.snake.game

import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

/** Sends mice and toads out of the grass and birds flying in: more mice and toads as the score goes up, and never more than one bird, which only comes once the score is high enough. */
class CritterSpawner(private val arena: Arena, private val random: Random, private val level: LevelConfig = LevelConfig.first) {
    private var nextId = 1
    private var mouseTimer = 0f
    private var toadTimer = 0f
    private var birdTimer = 0f

    /** Called when the game starts: the first mouse comes quickly, the first toad later, and the first bird later still. */
    fun reset() {
        mouseTimer = level.mice.spawn.firstDelay
        toadTimer = level.toads.spawn.firstDelay
        birdTimer = level.birds.firstDelay
    }

    /**
     * [snakeHead] is where the head of the snake is, so that a bird does not pick a landing spot close to it. [attractsMice] is
     * the cheese power up: more mice come, and faster. [allowsToads] is false while the toad-free power up is on: no toad comes out of
     * the grass, and the toad timer waits until it is over.
     */
    fun update(
        dt: Float,
        critters: MutableList<Critter>,
        score: Int,
        snakeHead: Vec2? = null,
        attractsMice: Boolean = false,
        allowsToads: Boolean = true,
    ) {
        mouseTimer -= if (attractsMice) dt * level.powerUps.cheeseSpawnFactor else dt
        if (allowsToads) toadTimer -= dt
        birdTimer -= dt
        val mice = critters.count { it is Critter.Mouse }
        val toads = critters.count { it is Critter.Toad }
        val birds = critters.count { it is Critter.Bird }
        val extraMice = if (attractsMice) level.powerUps.cheeseExtraMice else 0
        if (mice < min(1 + score / level.mice.spawn.pointsPerExtra, level.mice.spawn.most) + extraMice && mouseTimer <= 0f) {
            critters.add(newMouse())
            mouseTimer = between(level.mice.spawn.delayMin, level.mice.spawn.delayMax)
        }
        if (allowsToads && toads < min(1 + score / level.toads.spawn.pointsPerExtra, level.toads.spawn.most) && toadTimer <= 0f) {
            critters.add(newToad())
            toadTimer = between(level.toads.spawn.delayMin, level.toads.spawn.delayMax)
        }
        if (birds == 0 && score >= level.birds.firstScore && birdTimer <= 0f) {
            critters.add(newBird(snakeHead))
            birdTimer = between(level.birds.delayMin, level.birds.delayMax)
        }
    }

    private fun newMouse(): Critter {
        val spot = spotInTheGrass()
        return Critter.Mouse(nextId++, spot.x, spot.y, lifetime(level.mice.spawn), rules = level.mice)
    }

    private fun newToad(): Critter {
        val spot = spotInTheGrass()
        val kind = level.toads.kinds[random.nextInt(level.toads.kinds.size)]
        return Critter.Toad(nextId++, kind, spot.x, spot.y, lifetime(level.toads.spawn), rules = level.toads)
    }

    /** A bird starts off the screen and flies to a spot in the oval that is not close to the snake. */
    private fun newBird(snakeHead: Vec2?): Critter {
        val kind = BirdKind.entries[random.nextInt(BirdKind.entries.size)]
        val from = spotOffTheScreen()
        val landing = BirdLanding.spot(arena, random, snakeHead, level.birds.landClearance)
        val stay = between(level.birds.staySecondsMin, level.birds.staySecondsMax)
        return Critter.Bird(nextId++, kind, from.x, from.y, landing.x, landing.y, stay, rules = level.birds)
    }

    private fun spotOffTheScreen(): Vec2 {
        val margin = BirdTuning.spawnMargin
        val along = random.nextFloat()
        return when (random.nextInt(4)) {
            0 -> Vec2(-margin, arena.height * along)
            1 -> Vec2(arena.width + margin, arena.height * along)
            2 -> Vec2(arena.width * along, -margin)
            else -> Vec2(arena.width * along, arena.height + margin)
        }
    }

    private fun lifetime(rate: CritterRate): Float = between(rate.lifetimeMin, rate.lifetimeMax)

    private fun between(least: Float, most: Float): Float = least + random.nextFloat() * (most - least)

    /** Whether the critters stay away from the way in at the bottom of the oval, while it is open: once it is sealed the oval is a perfect oval. */
    var avoidsPath = true

    /** A spot in the grass around the oval, but never on the path that is cut through the grass at the bottom. */
    private fun spotInTheGrass(): Vec2 {
        val reach = WorldTuning.grassDepth * 0.75f
        var spot: Vec2
        do {
            val angle = random.nextFloat() * 2f * PI.toFloat()
            spot = Vec2(
                arena.centerX + (arena.radiusX + reach) * cos(angle),
                arena.centerY + (arena.radiusY + reach) * sin(angle),
            )
        } while (avoidsPath && spot.y > arena.centerY && abs(spot.x - arena.centerX) < DenTuning.pathHalfWidth + pathMargin)
        return spot
    }

    private companion object {
        /** How far from the path a critter comes out of the grass at the least. */
        const val pathMargin = 3f
    }
}
