package com.lukeneedham.snake.game

/**
 * The oval the snake lives in, inside a band of tall grass that fills the rest of the screen. [width] and [height] are the
 * size of the screen in art pixels: the oval is in the middle of it.
 */
class Arena(val width: Float, val height: Float) : Bounds {
    val centerX = width / 2f
    val centerY = height / 2f
    val radiusX = width / 2f - WorldTuning.grassDepth
    val radiusY = height / 2f - WorldTuning.grassDepth

    private val oval = OvalBounds(centerX, centerY, radiusX, radiusY)

    /** How far the point is from the edge of the oval, along the way out: negative inside, positive in the grass. */
    override fun signedDistance(x: Float, y: Float): Float = oval.signedDistance(x, y)

    /** The unit vector that points out of the oval at the point. */
    override fun normal(x: Float, y: Float): Vec2 = oval.normal(x, y)
}
