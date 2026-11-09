package com.lukeneedham.snake.game

/** What the snake is like in a level: how fast it is, how tight it turns, how far its mouth reaches, how long it is and how fast it grows. */
class SnakeRules(
    /** How fast the snake is, as a part of its normal speed. */
    val speedFactor: Float = 1f,
    val turnRadius: Float = SnakeTuning.turnRadius,
    val eatRadius: Float = SnakeTuning.eatRadius,
    val startSections: Int = SnakeTuning.startSections,
    /** The length one mouse adds to the snake, and how fast the snake grows into it. */
    val sectionLength: Float = SnakeTuning.sectionLength,
    val growRate: Float = SnakeTuning.growRate,
    /** How much faster the snake goes while it sprints flat out. */
    val sprintBoost: Float = SprintTuning.speedBoost,
) {
    companion object {
        val standard = SnakeRules()
    }
}
