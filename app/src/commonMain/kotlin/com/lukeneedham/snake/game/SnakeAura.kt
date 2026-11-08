package com.lukeneedham.snake.game

/** What the snake gives off while it has a power up, as the other animals feel it. */
class SnakeAura(
    /** How far away the animals notice the snake, compared to normal: below 1 is sneakier. */
    val noticeScale: Float = 1f,
    /** Whether toads hop away from the snake. */
    val repelsToads: Boolean = false,
) {
    companion object {
        val None = SnakeAura()
    }
}
