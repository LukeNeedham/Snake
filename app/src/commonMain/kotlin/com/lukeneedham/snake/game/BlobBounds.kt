package com.lukeneedham.snake.game

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin
import kotlin.math.sqrt

/** A round shape that is a little misshapen: its radius is [radius] plus some [wobbles] that make it bulge here and pinch there. */
class BlobBounds(val centerX: Float, val centerY: Float, val radius: Float, private val wobbles: List<Wobble>) : Bounds {

    /** How far from the middle the edge is, in the direction of [angle]. */
    fun radiusAt(angle: Float): Float = radius * (1f + wobbles.sumOf { (it.amplitude * sin(it.lobes * angle + it.phase)).toDouble() }.toFloat())

    /** How fast the edge moves away from the middle as the [angle] turns. */
    private fun slopeAt(angle: Float): Float =
        radius * wobbles.sumOf { (it.amplitude * it.lobes * cos(it.lobes * angle + it.phase)).toDouble() }.toFloat()

    /** Close to the edge this is the distance to it: the edge is only mildly bumpy, so the slope of the gradient is small. */
    override fun signedDistance(x: Float, y: Float): Float {
        val dx = x - centerX
        val dy = y - centerY
        val distance = max(sqrt(dx * dx + dy * dy), 1f)
        val angle = atan2(dy, dx)
        val tilt = slopeAt(angle) / distance
        return (distance - radiusAt(angle)) / sqrt(1f + tilt * tilt)
    }

    override fun normal(x: Float, y: Float): Vec2 {
        val dx = x - centerX
        val dy = y - centerY
        val distance = max(sqrt(dx * dx + dy * dy), 1f)
        val angle = atan2(dy, dx)
        val tilt = slopeAt(angle) / distance
        val nx = cos(angle) + tilt * sin(angle)
        val ny = sin(angle) - tilt * cos(angle)
        val length = sqrt(nx * nx + ny * ny)
        return Vec2(nx / length, ny / length)
    }
}
