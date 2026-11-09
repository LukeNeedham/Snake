package com.lukeneedham.snake.game

/** What happens to the hunger bar in a room. */
enum class HungerMode {
    /** It is full, and stays full: nothing in the room makes the snake hungry. */
    Full,

    /** It does not change by itself: whatever happens in the room changes it by hand. */
    Manual,

    /** It goes down as the game is played. */
    Drains,
}
