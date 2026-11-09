package com.lukeneedham.snake.game

/** A shape the snake stays inside: how far a point is from its edge, and which way the edge faces. */
interface Bounds {
    /** How far the point is from the edge of the shape: negative inside, positive outside. */
    fun signedDistance(x: Float, y: Float): Float

    /** The unit vector that points out of the shape at the point. */
    fun normal(x: Float, y: Float): Vec2
}
