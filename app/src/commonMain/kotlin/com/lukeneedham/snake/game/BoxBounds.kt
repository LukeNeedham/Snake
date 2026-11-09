package com.lukeneedham.snake.game

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sqrt

/** An upright rectangle, [halfWidth] and [halfHeight] from its centre to its edges. */
class BoxBounds(val centerX: Float, val centerY: Float, val halfWidth: Float, val halfHeight: Float) : Bounds {

    override fun signedDistance(x: Float, y: Float): Float {
        val outsideX = abs(x - centerX) - halfWidth
        val outsideY = abs(y - centerY) - halfHeight
        val beyondX = max(outsideX, 0f)
        val beyondY = max(outsideY, 0f)
        return sqrt(beyondX * beyondX + beyondY * beyondY) + minOf(max(outsideX, outsideY), 0f)
    }

    override fun normal(x: Float, y: Float): Vec2 {
        val sideX = if (x >= centerX) 1f else -1f
        val sideY = if (y >= centerY) 1f else -1f
        val outsideX = abs(x - centerX) - halfWidth
        val outsideY = abs(y - centerY) - halfHeight
        if (outsideX > 0f || outsideY > 0f) {
            val beyondX = max(outsideX, 0f)
            val beyondY = max(outsideY, 0f)
            val length = sqrt(beyondX * beyondX + beyondY * beyondY)
            return Vec2(sideX * beyondX / length, sideY * beyondY / length)
        }
        // Inside: the nearest edge faces the way out
        return if (outsideX > outsideY) Vec2(sideX, 0f) else Vec2(0f, sideY)
    }
}
