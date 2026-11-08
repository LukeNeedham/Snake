package com.lukeneedham.snake.game

/** The numbers that shape how the birds fly, land and take off, and what they are worth. */
object BirdTuning {
    /** How high a bird flies, in art pixels above the ground. */
    const val flightHeight = 18f
    const val cruiseSpeed = 46f

    /** A bird that is closer than this to its landing spot slows down and comes down. */
    const val glideDistance = 30f
    const val landingSpeed = 16f

    /** How fast a bird that takes off goes up, and how fast it flies away once it is up. */
    const val climbSpeed = 36f
    const val leaveSpeed = 58f
    const val takeOffSeconds = 0.5f

    /** A bird that flies in this far outside the screen, and is gone when it flies out this far. */
    const val spawnMargin = 12f
    const val exitMargin = 14f

    /** A bird that sees the snake wait this long, crouched, before it takes off: that is the only time to catch it. */
    const val reactionSeconds = 0.3f

    /** The snake can only eat a bird that is lower than this above the ground. */
    const val eatHeight = 3f

    /** A bird never lands closer than this to the head of the snake, and picks another spot if the snake comes closer while it is still coming down. */
    const val landClearance = 34f

    /** A bird that is lower than this has made up its mind and lands where it was going. */
    const val commitHeight = 6f
    const val landingAttempts = 12

    const val staySecondsMin = 9f
    const val staySecondsMax = 13f

    const val hopHeight = 2.5f
    const val hopSeconds = 0.25f

}
