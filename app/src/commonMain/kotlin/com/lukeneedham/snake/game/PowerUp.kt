package com.lukeneedham.snake.game

/** What eating a bird gives the snake for a while: each kind of bird gives its own. They stack with each other and with the power downs of the toads. */
enum class PowerUp(val durationSeconds: Float) {
    /** The snake goes faster. */
    Zoom(8f),

    /** Sprinting is faster, and costs less hunger. */
    FastSprint(10f),

    /** Mice and birds only notice the snake when it is closer. */
    Sneaky(12f),

    /** The snake eats from further away. */
    BiggerMouth(10f),

    /** Every toad on the screen explodes when it is eaten, and no new toad comes out of the grass while it lasts. */
    ToadFree(12f),

    /**
     * The snake carries a piece of cheese in its mouth. Its mouth is full, so it cannot eat anything until the cheese is gone, but
     * more mice come, and they gather around it instead of running away.
     */
    Cheese(10f),
}
