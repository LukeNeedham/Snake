package com.lukeneedham.snake.game

import kotlin.math.max
import kotlin.math.min

/**
 * How full the snake is: the bar goes down at a steady rate, faster while it sprints, and the snake dies when it is empty.
 * What was just eaten does not fill the bar at once: it shows as a [gain] on top of the [filled] part for a moment, and then the
 * bar fills up into it, so that it is clear how much the food was worth. The bar does not drain until it has filled up.
 */
class Hunger {
    /** 1 when full, 0 when starved: the real value, which already counts what was just eaten. */
    var level = 1f
        private set

    /** How much of the bar is filled in properly: it is behind [level] while a meal is still being added. */
    var filled = 1f
        private set

    /** The part of the bar that was just added and is not filled in yet. */
    val gain: Float get() = level - filled

    val isStarving: Boolean get() = level <= 0f

    val isLow: Boolean get() = level <= HungerTuning.lowLevel

    private var gainWait = 0f

    /**
     * [sprintLevel] is 0 when the snake does not sprint, 1 when it sprints flat out, and then the bar drains [sprintDrainFactor] times
     * as fast. The bar always drains [drainFactor] times as fast on top of that.
     */
    fun update(dt: Float, sprintLevel: Float, sprintDrainFactor: Float = HungerTuning.sprintDrainFactor, drainFactor: Float = 1f) {
        if (filled < level) {
            fillIn(dt)
        } else {
            val rate = HungerTuning.drainPerSecond * (1f + (sprintDrainFactor - 1f) * sprintLevel) * drainFactor
            level = max(0f, level - rate * dt)
            filled = level
        }
    }

    fun feed(amount: Float) {
        level = min(1f, level + amount)
        gainWait = HungerTuning.gainShowSeconds
    }

    /** The new part stays as it is for a moment, then the filled part grows into it. */
    private fun fillIn(dt: Float) {
        if (gainWait > 0f) {
            gainWait -= dt
        } else {
            filled = min(level, filled + HungerTuning.gainFillPerSecond * dt)
        }
    }
}
