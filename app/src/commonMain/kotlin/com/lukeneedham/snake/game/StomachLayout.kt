package com.lukeneedham.snake.game

/** Where the stomach is in the top right corner, in world units: the drawing and the touches both use it. */
object StomachLayout {
    /** The stomach is this many pixels wide and high. */
    const val size = 15
    private const val margin = 4
    private const val touchSlack = 6f

    fun left(arena: Arena): Int = arena.width.toInt() - margin - size

    fun top(topInset: Int): Int = topInset + 3

    /** Whether a finger at [x], [y] is on the stomach. */
    fun contains(arena: Arena, topInset: Int, x: Float, y: Float): Boolean =
        x >= left(arena) - touchSlack && y <= top(topInset) + size + touchSlack
}
