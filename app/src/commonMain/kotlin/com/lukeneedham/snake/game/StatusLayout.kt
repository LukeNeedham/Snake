package com.lukeneedham.snake.game

/** Where the arrows of the effects and power ups are, in world units: the drawing and the touches both use it. */
object StatusLayout {
    /** The distance between two arrows, from the top of one to the top of the next. */
    const val rowHeight = 11

    /** How far the left side of an arrow is from the right edge of the screen. */
    private const val arrowInset = 9

    /** A finger is bigger than the arrows, so a touch this far from them still counts. */
    private const val touchSlack = 6f

    fun arrowLeft(arena: Arena): Int = arena.width.toInt() - arrowInset

    /** The top of the arrow of the [row]th entry, counted up from the bottom: above the sprint button when that is on the right. */
    fun arrowTop(arena: Arena, side: SprintSide, row: Int): Int = arena.height.toInt() - rowHeight - buttonRoom(side) - row * rowHeight

    /** Whether a finger at [x], [y] is on the column of [count] arrows. */
    fun contains(arena: Arena, side: SprintSide, count: Int, x: Float, y: Float): Boolean {
        if (count <= 0) return false
        if (x < arrowLeft(arena) - touchSlack) return false
        val top = arrowTop(arena, side, count - 1) - touchSlack
        val bottom = arena.height - buttonRoom(side)
        return y >= top && y < bottom
    }

    private fun buttonRoom(side: SprintSide): Int = if (side == SprintSide.Right) SprintZone.size.toInt() else 0
}
