package com.lukeneedham.snake.game

import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

/**
 * The snake always turns, and the only control is the direction of the turn. This is the head: where it is, which way it
 * goes and how it bounces off the wall (a bounce also turns it the other way). The [body] follows the path it travelled.
 */
class Snake(
    arena: Arena,
    bounds: Bounds = arena,
    startX: Float = arena.centerX,
    startY: Float = arena.centerY,
    startHeading: Float = -halfPi,
) {

    var x = startX
        private set
    var y = startY
        private set

    /** In radians: it grows when the snake turns clockwise on the screen. */
    var heading = startHeading
        private set

    /** 1 turns clockwise, -1 anticlockwise. */
    var turnDirection = 1f
        private set

    var totalDistance: Float
        private set

    val body = SnakeBody(x, y, heading)

    /** 0 when the mouth is closed, 1 when it is wide open. */
    var mouthOpen = 0f
        internal set

    /** Counts every bounce off the wall, with where the last one happened. */
    var bounceCount = 0
        private set
    var lastBounceX = 0f
        private set
    var lastBounceY = 0f
        private set

    private val wallBounce = WallBounce(bounds)

    init {
        totalDistance = body.initialDistance
    }

    fun toggleTurn() {
        turnDirection = -turnDirection
    }

    fun grow(sections: Int) = body.grow(sections)

    fun addBulge() = body.addBulge(totalDistance)

    fun step(dt: Float, speedMultiplier: Float) {
        val distance = SnakeTuning.speed * speedMultiplier * dt
        val steps = max(1, ceil(distance / SnakeTuning.trailStep).toInt())
        val each = distance / steps
        repeat(steps) { advance(each) }
        body.update(dt, totalDistance, heading)
    }

    /** Whether the head touches a part of the body that is not the neck. It only kills the snake under the cannibal effect. */
    fun hitsItself(): Boolean = body.touches(x, y)

    private fun advance(distance: Float) {
        heading += turnDirection * distance / SnakeTuning.turnRadius
        x += cos(heading) * distance
        y += sin(heading) * distance
        totalDistance += distance
        wallBounce.apply(x, y, heading)?.let { bounce ->
            x = bounce.x
            y = bounce.y
            heading = bounce.heading
            if (bounce.reflected) {
                // Hitting a wall counts as a tap: the snake turns the other way
                toggleTurn()
                bounceCount++
                lastBounceX = x
                lastBounceY = y
            }
        }
        body.record(x, y, totalDistance)
    }

    private companion object {
        const val halfPi = 1.5707964f
    }
}
