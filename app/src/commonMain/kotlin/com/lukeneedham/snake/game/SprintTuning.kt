package com.lukeneedham.snake.game

/** The numbers that shape the sprint: a finger held on the screen. */
object SprintTuning {
    /** A finger that stays down this long is a hold, which sprints; a finger that comes up sooner is a tap, which turns. */
    const val holdSeconds = 0.18f

    /** How much faster the snake goes while it sprints flat out. */
    const val speedBoost = 0.8f

    /** How quickly the sprint gets to full strength, and back, per second. */
    const val easeSpeed = 7f
}
