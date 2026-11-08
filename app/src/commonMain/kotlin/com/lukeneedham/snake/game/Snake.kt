package com.lukeneedham.snake.game

import kotlin.math.atan2
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * The snake always turns, and the only control is the direction of the turn. The body is the path the head travelled, so it
 * is kept as a trail of points that is sampled again every step.
 */
class Snake(private val arena: Arena) {

    private class TrailPoint(val x: Float, val y: Float, val distance: Float)

    var x = arena.centerX
        private set
    var y = arena.centerY
        private set

    /** In radians: it grows when the snake turns clockwise on the screen. */
    var heading = -HALF_PI
        private set

    /** 1 turns clockwise, -1 anticlockwise. */
    var turnDirection = 1f
        private set

    var totalDistance = 0f
        private set

    var length = GameTuning.START_SECTIONS * GameTuning.SECTION_LENGTH
        private set
    private var targetLength = length

    /** The discs of the body from the head to the tail. */
    var body: List<BodyPoint> = emptyList()
        private set

    /** Where on the path the snake swallowed a mouse: each one is a bulge that travels down the body. */
    val bulges = ArrayList<Float>()

    /** 0 when the mouth is closed, 1 when it is wide open. */
    var mouthOpen = 0f

    /** Counts every bounce off the wall, with where the last one happened. */
    var bounceCount = 0
        private set
    var lastBounceX = 0f
        private set
    var lastBounceY = 0f
        private set

    private val trail = ArrayDeque<TrailPoint>()

    init {
        totalDistance = length
        val points = (length / GameTuning.TRAIL_STEP).toInt()
        for (i in points downTo 0) {
            val back = i * GameTuning.TRAIL_STEP
            trail.addLast(TrailPoint(x - cos(heading) * back, y - sin(heading) * back, totalDistance - back))
        }
        rebuildBody()
    }

    fun toggleTurn() {
        turnDirection = -turnDirection
    }

    fun grow(sections: Int) {
        targetLength += sections * GameTuning.SECTION_LENGTH
    }

    fun addBulge() {
        bulges.add(totalDistance)
    }

    fun step(dt: Float, speedMultiplier: Float) {
        if (length < targetLength) length = min(targetLength, length + GameTuning.GROW_RATE * dt)
        val distance = GameTuning.SNAKE_SPEED * speedMultiplier * dt
        val steps = max(1, ceil(distance / GameTuning.TRAIL_STEP).toInt())
        val each = distance / steps
        repeat(steps) { advance(each) }
        while (trail.size > 2 && trail[1].distance < totalDistance - length - 2f) trail.removeFirst()
        bulges.removeAll { totalDistance - it > length + 8f }
        rebuildBody()
    }

    /** Whether the head touches a part of the body that is not the neck. */
    fun hitsItself(): Boolean {
        val limit = GameTuning.SELF_HIT_DISTANCE * GameTuning.SELF_HIT_DISTANCE
        for (point in body) {
            if (point.distanceFromHead < GameTuning.SELF_HIT_SKIP) continue
            if (point.distanceFromTail < 4f) continue
            val dx = point.x - x
            val dy = point.y - y
            if (dx * dx + dy * dy < limit) return true
        }
        return false
    }

    private fun advance(distance: Float) {
        heading += turnDirection * distance / GameTuning.TURN_RADIUS
        x += cos(heading) * distance
        y += sin(heading) * distance
        totalDistance += distance
        bounceOffTheWall()
        trail.addLast(TrailPoint(x, y, totalDistance))
    }

    /** At the wall the snake does not die: it is pushed back in and its course is mirrored, then it carries on turning. */
    private fun bounceOffTheWall() {
        val limit = -(GameTuning.HEAD_RADIUS + 0.8f)
        val distance = arena.signedDistance(x, y)
        if (distance <= limit) return
        val normal = arena.normal(x, y)
        x -= normal.x * (distance - limit)
        y -= normal.y * (distance - limit)
        val dirX = cos(heading)
        val dirY = sin(heading)
        val dot = dirX * normal.x + dirY * normal.y
        if (dot > 0f) {
            heading = atan2(dirY - 2f * dot * normal.y, dirX - 2f * dot * normal.x)
            bounceCount++
            lastBounceX = x
            lastBounceY = y
        }
    }

    private fun rebuildBody() {
        val spacing = GameTuning.BODY_SPACING
        val tailDistance = totalDistance - length
        val list = ArrayList<BodyPoint>()
        var k = floor(totalDistance / spacing).toInt()
        val last = ceil(tailDistance / spacing).toInt()
        var i = trail.lastIndex - 1
        while (k >= last) {
            val cumulative = k * spacing
            while (i > 0 && trail[i].distance > cumulative) i--
            val from = trail[i]
            val to = trail[i + 1]
            val span = to.distance - from.distance
            val t = if (span > 1e-5f) ((cumulative - from.distance) / span).coerceIn(0f, 1f) else 0f
            var dirX = to.x - from.x
            var dirY = to.y - from.y
            val size = sqrt(dirX * dirX + dirY * dirY)
            if (size > 1e-5f) {
                dirX /= size
                dirY /= size
            } else {
                dirX = cos(heading)
                dirY = sin(heading)
            }
            list.add(
                BodyPoint(
                    x = from.x + (to.x - from.x) * t,
                    y = from.y + (to.y - from.y) * t,
                    dirX = dirX,
                    dirY = dirY,
                    index = k,
                    cumulative = cumulative,
                    distanceFromHead = totalDistance - cumulative,
                    distanceFromTail = cumulative - tailDistance,
                ),
            )
            k--
        }
        body = list
    }

    private companion object {
        const val HALF_PI = 1.5707964f
    }
}
