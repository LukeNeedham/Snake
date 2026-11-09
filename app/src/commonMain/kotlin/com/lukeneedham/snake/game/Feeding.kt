package com.lukeneedham.snake.game

import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/** The mouth of the snake: it opens when food is near, and takes in what it reaches, anywhere on the [ground] it can go. */
class Feeding(private val ground: Bounds) {

    /**
     * Removes the critters the mouth reaches from [critters] and returns them. Critters hiding in the grass are safe, and so are
     * birds in the air. [reachFactor] is how many times as far the mouth reaches, with the bigger mouth power up. A snake with
     * [isMouthFull] (it carries cheese) keeps its mouth shut and eats nothing.
     */
    fun update(dt: Float, snake: Snake, critters: MutableList<Critter>, reachFactor: Float = 1f, isMouthFull: Boolean = false): List<Critter> {
        if (isMouthFull) {
            snake.mouthOpen += (0f - snake.mouthOpen) * min(1f, dt * openSpeed)
            return emptyList()
        }
        val mouthX = snake.x + cos(snake.heading) * SnakeTuning.mouthReach
        val mouthY = snake.y + sin(snake.heading) * SnakeTuning.mouthReach
        val eatRadius = SnakeTuning.eatRadius * reachFactor
        val eatLimit = eatRadius * eatRadius
        val openLimit = openRange * reachFactor
        val eaten = ArrayList<Critter>()
        var nearest = Float.MAX_VALUE
        for (i in critters.indices.reversed()) {
            val critter = critters[i]
            if (!critter.isEdible || ground.signedDistance(critter.x, critter.y) > -1f) continue
            val dx = critter.x - mouthX
            val dy = critter.y - critterHeight - mouthY
            val distance = dx * dx + dy * dy
            if (distance < eatLimit) {
                eaten.add(critter)
                critters.removeAt(i)
            } else {
                nearest = min(nearest, distance)
            }
        }
        val target = if (nearest < openLimit * openLimit) 1f else 0f
        snake.mouthOpen += (target - snake.mouthOpen) * min(1f, dt * openSpeed)
        return eaten
    }

    private companion object {
        /** A critter's centre is this far above its feet. */
        const val critterHeight = 3f

        /** The mouth opens when food is closer than this. */
        const val openRange = 12f
        const val openSpeed = 14f
    }
}
