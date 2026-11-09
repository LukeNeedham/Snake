package com.lukeneedham.snake.game

/** Several shapes that overlap and make up one: the snake may be in any of them. */
class UnionBounds(private val parts: List<Bounds>) : Bounds {

    override fun signedDistance(x: Float, y: Float): Float = parts.minOf { it.signedDistance(x, y) }

    /** The edge that is the nearest decides which way it faces. */
    override fun normal(x: Float, y: Float): Vec2 = parts.minBy { it.signedDistance(x, y) }.normal(x, y)
}
