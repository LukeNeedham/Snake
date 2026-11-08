package com.lukeneedham.snake.game

import kotlin.math.max
import kotlin.math.min

/** How full the snake is: the bar goes down at a steady rate, faster while it sprints, and the snake dies when it is empty. */
class Hunger {
    /** 1 when full, 0 when starved. */
    var level = 1f
        private set

    val isStarving: Boolean get() = level <= 0f

    val isLow: Boolean get() = level <= HungerTuning.lowLevel

    /** [sprintLevel] is 0 when the snake does not sprint, 1 when it sprints flat out. */
    fun update(dt: Float, sprintLevel: Float) {
        val rate = HungerTuning.drainPerSecond * (1f + (HungerTuning.sprintDrainFactor - 1f) * sprintLevel)
        level = max(0f, level - rate * dt)
    }

    fun feed(amount: Float) {
        level = min(1f, level + amount)
    }
}
