package com.lukeneedham.snake.game

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

/** Sends mice and toads out of the grass: more of them as the score goes up. */
class CritterSpawner(private val arena: Arena, private val random: Random) {
    private var nextId = 1
    private var mouseTimer = 0f
    private var toadTimer = 0f

    /** Called when the game starts: the first mouse comes quickly, the first toad later. */
    fun reset() {
        mouseTimer = firstMouseDelay
        toadTimer = firstToadDelay
    }

    fun update(dt: Float, critters: MutableList<Critter>, score: Int) {
        mouseTimer -= dt
        toadTimer -= dt
        val mice = critters.count { it is Critter.Mouse }
        val toads = critters.count { it is Critter.Toad }
        if (mice < min(2 + score / 5, 5) && mouseTimer <= 0f) {
            critters.add(newMouse())
            mouseTimer = 1f + random.nextFloat() * 1.2f
        }
        if (toads < min(1 + score / 6, 3) && toadTimer <= 0f) {
            critters.add(newToad())
            toadTimer = 6f + random.nextFloat() * 4f
        }
    }

    private fun newMouse(): Critter {
        val spot = spotInTheGrass()
        return Critter.Mouse(nextId++, spot.x, spot.y, lifetime())
    }

    private fun newToad(): Critter {
        val spot = spotInTheGrass()
        val kind = ToadKind.entries[random.nextInt(ToadKind.entries.size)]
        return Critter.Toad(nextId++, kind, spot.x, spot.y, lifetime())
    }

    private fun lifetime(): Float = 14f + random.nextFloat() * 8f

    private fun spotInTheGrass(): Vec2 {
        val angle = random.nextFloat() * 2f * PI.toFloat()
        val reach = WorldTuning.grassDepth * 0.75f
        return Vec2(
            arena.centerX + (arena.radiusX + reach) * cos(angle),
            arena.centerY + (arena.radiusY + reach) * sin(angle),
        )
    }

    private companion object {
        const val firstMouseDelay = 0.5f
        const val firstToadDelay = 7f
    }
}
