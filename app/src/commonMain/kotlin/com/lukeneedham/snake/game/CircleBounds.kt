package com.lukeneedham.snake.game

import kotlin.math.sqrt

/** A round shape. */
class CircleBounds(val centerX: Float, val centerY: Float, val radius: Float) : Bounds {

    override fun signedDistance(x: Float, y: Float): Float {
        val dx = x - centerX
        val dy = y - centerY
        return sqrt(dx * dx + dy * dy) - radius
    }

    override fun normal(x: Float, y: Float): Vec2 {
        val dx = x - centerX
        val dy = y - centerY
        val length = sqrt(dx * dx + dy * dy)
        if (length < 1e-6f) return Vec2(0f, -1f)
        return Vec2(dx / length, dy / length)
    }
}
