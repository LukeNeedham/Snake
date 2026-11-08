package com.lukeneedham.snake.game

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/** Where the snake ends up after it reached the wall of the oval. [reflected] is whether its course was mirrored. */
class Bounce(val x: Float, val y: Float, val heading: Float, val reflected: Boolean)

/** At the wall the snake does not die: it is pushed back in and its course is mirrored, then it carries on turning. */
class WallBounce(private val arena: Arena) {

    /** The new place and course of a snake at [x], [y] going along [heading], or null if it is not at the wall. */
    fun apply(x: Float, y: Float, heading: Float): Bounce? {
        val limit = -(SnakeTuning.headRadius + SnakeTuning.wallMargin)
        val distance = arena.signedDistance(x, y)
        if (distance <= limit) return null
        val normal = arena.normal(x, y)
        val pushedX = x - normal.x * (distance - limit)
        val pushedY = y - normal.y * (distance - limit)
        val directionX = cos(heading)
        val directionY = sin(heading)
        val dot = directionX * normal.x + directionY * normal.y
        if (dot <= 0f) return Bounce(pushedX, pushedY, heading, reflected = false)
        val mirrored = atan2(directionY - 2f * dot * normal.y, directionX - 2f * dot * normal.x)
        return Bounce(pushedX, pushedY, mirrored, reflected = true)
    }
}
