package com.lukeneedham.snake.game

/** What the snake died of: the game over screen says it. */
enum class DeathCause {
    /** The hunger bar ran empty. */
    Starvation,

    /** The snake touched the grass with hayfever. */
    Allergies,

    /** The snake touched itself as a cannibal. */
    Cannibalism,
}
