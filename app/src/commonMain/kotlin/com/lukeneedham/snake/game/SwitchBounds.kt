package com.lukeneedham.snake.game

/** The [open] shape, or the [closed] one while [isClosed] says so: the map with a path, and the same map with that path sealed off. */
class SwitchBounds(private val open: Bounds, private val closed: Bounds, private val isClosed: () -> Boolean) : Bounds {

    private fun current(): Bounds = if (isClosed()) closed else open

    override fun signedDistance(x: Float, y: Float): Float = current().signedDistance(x, y)

    override fun normal(x: Float, y: Float): Vec2 = current().normal(x, y)
}
