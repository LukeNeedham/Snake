package com.lukeneedham.snake.game

import kotlin.math.sqrt

/** An oval: [radiusX] wide and [radiusY] high from its middle. */
class OvalBounds(val centerX: Float, val centerY: Float, val radiusX: Float, val radiusY: Float) : Bounds {

    /** How far the point is from the edge of the oval, along the way out: negative inside, positive outside. */
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
