package com.lukeneedham.snake.game

import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * The body of the snake is the path its head travelled, so it is kept as a trail of points that is sampled again every step
 * into the discs it is drawn with.
 */
class SnakeBody(headX: Float, headY: Float, heading: Float, private val rules: SnakeRules = SnakeRules.standard) {

    private class TrailPoint(val x: Float, val y: Float, val distance: Float)

    var length = rules.startSections * rules.sectionLength
        private set

    /** How far the head has travelled when the snake is new: the body behind it is already there. */
    val initialDistance = length

    /** Where on the path the snake swallowed a mouse: each one is a bulge that travels down the body. */
    val bulges: List<Float> get() = mutableBulges

    /** The discs of the body from the head to the tail. */
    var points: List<BodyPoint> = emptyList()
        private set

    private val mutableBulges = ArrayList<Float>()
    private var targetLength = length
    private val trail = ArrayDeque<TrailPoint>()

    init {
        val count = (length / SnakeTuning.trailStep).toInt()
        for (i in count downTo 0) {
            val back = i * SnakeTuning.trailStep
            trail.addLast(TrailPoint(headX - cos(heading) * back, headY - sin(heading) * back, initialDistance - back))
        }
        rebuild(initialDistance, heading)
    }

    /** Records where the head is, after it travelled [totalDistance] in all. */
    fun record(x: Float, y: Float, totalDistance: Float) {
        trail.addLast(TrailPoint(x, y, totalDistance))
    }

    fun grow(sections: Int) {
        targetLength += sections * rules.sectionLength
    }

    fun addBulge(totalDistance: Float) {
        mutableBulges.add(totalDistance)
    }

    /** Lets the body grow towards its length, forgets what is behind the tail, and samples the discs again. */
    fun update(dt: Float, totalDistance: Float, heading: Float) {
        if (length < targetLength) length = min(targetLength, length + rules.growRate * dt)
        while (trail.size > 2 && trail[1].distance < totalDistance - length - 2f) trail.removeFirst()
        mutableBulges.removeAll { totalDistance - it > length + 8f }
        rebuild(totalDistance, heading)
    }

    /** Whether the head at [headX], [headY] touches a part of the body that is not the neck. */
    fun touches(headX: Float, headY: Float): Boolean {
        val limit = SnakeTuning.selfHitDistance * SnakeTuning.selfHitDistance
        for (point in points) {
            if (point.distanceFromHead < SnakeTuning.selfHitSkip) continue
            if (point.distanceFromTail < 4f) continue
            val dx = point.x - headX
            val dy = point.y - headY
            if (dx * dx + dy * dy < limit) return true
        }
        return false
    }

    private fun rebuild(totalDistance: Float, heading: Float) {
        val spacing = SnakeTuning.bodySpacing
        val tailDistance = totalDistance - length
        val list = ArrayList<BodyPoint>()
        var k = floor(totalDistance / spacing).toInt()
        val last = ceil(tailDistance / spacing).toInt()
        var i = trail.lastIndex - 1
        while (k >= last) {
            val cumulative = k * spacing
            while (i > 0 && trail[i].distance > cumulative) i--
            list.add(samplePoint(trail[i], trail[i + 1], k, cumulative, totalDistance, tailDistance, heading))
            k--
        }
        points = list
    }

    private fun samplePoint(
        from: TrailPoint,
        to: TrailPoint,
        index: Int,
        cumulative: Float,
        totalDistance: Float,
        tailDistance: Float,
        heading: Float,
    ): BodyPoint {
        val span = to.distance - from.distance
        val t = if (span > 1e-5f) ((cumulative - from.distance) / span).coerceIn(0f, 1f) else 0f
        var directionX = to.x - from.x
        var directionY = to.y - from.y
        val size = sqrt(directionX * directionX + directionY * directionY)
        if (size > 1e-5f) {
            directionX /= size
            directionY /= size
        } else {
            directionX = cos(heading)
            directionY = sin(heading)
        }
        return BodyPoint(
            x = from.x + (to.x - from.x) * t,
            y = from.y + (to.y - from.y) * t,
            dirX = directionX,
            dirY = directionY,
            index = index,
            cumulative = cumulative,
            distanceFromHead = totalDistance - cumulative,
            distanceFromTail = cumulative - tailDistance,
        )
    }
}
