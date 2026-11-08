package com.lukeneedham.snake.game

/** What eating a bird gives the snake for a while: each kind of bird gives its own. They stack with each other and with the effects of the toads. */
enum class PowerUp(val durationSeconds: Float) {
    /** The snake goes faster. */
    Zoom(8f),

    /** Sprinting is faster, and costs less hunger. */
    FastSprint(10f),

    /** Mice and birds only notice the snake when it is closer. */
    Sneaky(12f),

    /** The snake eats from further away. */
    BiggerMouth(10f),

    /** Toads hop away from the snake. */
    ToadRepellent(12f),

    /** More mice come out of the grass. */
    Pheromones(10f),
}
