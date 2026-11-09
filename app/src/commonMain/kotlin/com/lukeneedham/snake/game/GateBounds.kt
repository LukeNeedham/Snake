package com.lukeneedham.snake.game

/** The [base] shape, but with the [gate] cut out of it while [isClosed] says so: a pile of stones that the snake cannot cross. */
class GateBounds(private val base: Bounds, private val gate: Bounds, private val isClosed: () -> Boolean) : Bounds {

    override fun signedDistance(x: Float, y: Float): Float {
        val inBase = base.signedDistance(x, y)
        return if (isClosed()) maxOf(inBase, -gate.signedDistance(x, y)) else inBase
    }

    /** Beside the stones the wall faces the stones: out of the ground, and into the gate. */
    override fun normal(x: Float, y: Float): Vec2 {
        if (isClosed() && -gate.signedDistance(x, y) > base.signedDistance(x, y)) {
            val into = gate.normal(x, y)
            return Vec2(-into.x, -into.y)
        }
        return base.normal(x, y)
    }
}
