package com.lukeneedham.snake.game

/**
 * Tells a tap from a hold. A finger that comes up soon after it went down is a tap; one that stays down sprints for as long
 * as it is held. Fingers are told apart by their id, so that two thumbs can tap in turn.
 */
class SprintControl {
    private val pressedAt = LinkedHashMap<Long, Float>()

    /** 0 to 1: how strongly the snake sprints right now, so that it speeds up and slows down smoothly. */
    var level = 0f
        private set

    fun press(pointerId: Long, now: Float) {
        pressedAt[pointerId] = now
    }

    /** Whether the finger that came up was a tap. A finger that was never pressed (it started the game) is not. */
    fun release(pointerId: Long, now: Float): Boolean {
        val start = pressedAt.remove(pointerId) ?: return false
        return now - start < SprintTuning.holdSeconds
    }

    /** [canSprint] is false while the game is not being played. */
    fun update(dt: Float, now: Float, canSprint: Boolean) {
        val isHeld = pressedAt.values.any { now - it >= SprintTuning.holdSeconds }
        val target = if (canSprint && isHeld) 1f else 0f
        val step = dt * SprintTuning.easeSpeed
        level += (target - level).coerceIn(-step, step)
    }

    fun clear() {
        pressedAt.clear()
    }
}
