package com.lukeneedham.snake.game

/** The numbers that shape how the mice and the toads move. (The birds have their own, in [BirdTuning].) */
object CritterTuning {
    const val mouseSpeed = 26f
    const val hopHeight = 5f
    const val hopDuration = 0.42f

    /** A mouse sees the snake in front of it from this far away. */
    const val noticeDistance = 26f

    /** A mouse that runs from the snake is a little faster than the snake itself, so that it can get away. */
    const val fleeSpeed = 34f
    const val fleeSeconds = 0.9f

    /** A mouse smells the cheese of the snake from this far away, in any direction, and runs towards it. */
    const val cheeseSmellDistance = 90f
    const val cheeseSpeed = 30f

    /** A mouse that came to the cheese stops this close to the snake and stands around it. */
    const val cheeseCrowdDistance = 6f

    /** Mice that came to the cheese stay put for this long after it is gone, instead of running away at once. */
    const val cheeseDazeSeconds = 1.5f
}
