package com.lukeneedham.snake.game

import kotlin.math.sqrt

/**
 * The oval the snake lives in, inside a band of tall grass that fills the rest of the screen. [width] and [height] are the
 * size of the whole world in art pixels.
 */
class Arena(val width: Float, val height: Float) : Bounds {
    val centerX = width / 2f
    val centerY = height / 2f
    val radiusX = width / 2f - WorldTuning.grassDepth
    val radiusY = height / 2f - WorldTuning.grassDepth

    /** How far the point is from the edge of the oval, along the way out: negative inside, positive in the grass. */
    override fun signedDistance(x: Float, y: Float): Float {
        val dx = x - centerX
        val dy = y - centerY
        val s = sqrt(dx * dx / (radiusX * radiusX) + dy * dy / (radiusY * radiusY))
        if (s < 1e-3f) return -minOf(radiusX, radiusY)
        val gx = dx / (radiusX * radiusX)
        val gy = dy / (radiusY * radiusY)
        val g = sqrt(gx * gx + gy * gy) / s
        return (s - 1f) / g
    }

    /** The unit vector that points out of the oval at the point. */
    override fun normal(x: Float, y: Float): Vec2 {
        val gx = (x - centerX) / (radiusX * radiusX)
        val gy = (y - centerY) / (radiusY * radiusY)
        val length = sqrt(gx * gx + gy * gy)
        if (length < 1e-6f) return Vec2(0f, -1f)
        return Vec2(gx / length, gy / length)
    }
}
