package com.lukeneedham.snake.game

enum class Direction(val dx: Int, val dy: Int) {
    Up(0, -1),
    Down(0, 1),
    Left(-1, 0),
    Right(1, 0);

    /** A snake can never turn back on itself, so the opposite of its direction is ignored. */
    fun isOppositeOf(other: Direction): Boolean = dx == -other.dx && dy == -other.dy
}
