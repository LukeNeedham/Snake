package com.lukeneedham.snake.game

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

/** Where a bird lands: anywhere in the oval, but never right in front of the snake's mouth. */
internal object BirdLanding {

    /** A spot in the oval that is at least [BirdTuning.landClearance] from [head] if one is found in a few tries, else the furthest one tried. */
    fun spot(arena: Arena, random: Random, head: Vec2?): Vec2 {
        var best = candidate(arena, random)
        if (head == null) return best
        var bestDistance = distance(best.x, best.y, head)
        var attempts = 0
        while (bestDistance < BirdTuning.landClearance && attempts < BirdTuning.landingAttempts) {
            val next = candidate(arena, random)
            val nextDistance = distance(next.x, next.y, head)
            if (nextDistance > bestDistance) {
                best = next
                bestDistance = nextDistance
            }
            attempts++
        }
        return best
    }

    /** Whether the snake's [head] is too close to the spot for a bird to land there. */
    fun isTooClose(x: Float, y: Float, head: Vec2): Boolean = distance(x, y, head) < BirdTuning.landClearance

    private fun candidate(arena: Arena, random: Random): Vec2 {
        val angle = random.nextFloat() * 2f * PI.toFloat()
        val reach = sqrt(random.nextFloat()) * reachInside
        return Vec2(arena.centerX + arena.radiusX * reach * cos(angle), arena.centerY + arena.radiusY * reach * sin(angle))
    }

    private fun distance(x: Float, y: Float, head: Vec2): Float {
        val dx = x - head.x
        val dy = y - head.y
        return sqrt(dx * dx + dy * dy)
    }

    /** Birds land in the inner part of the oval, away from the wall. */
    private const val reachInside = 0.8f
}
