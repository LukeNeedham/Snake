package com.lukeneedham.snake.game

/** The fingers held in the [SprintZone]: the snake sprints for as long as there is one, and speeds up and slows down smoothly. */
class SprintControl {
    private val fingers = HashSet<Long>()

    /** Whether a finger is held in the zone right now. */
    val isHeld: Boolean get() = fingers.isNotEmpty()

    /** 0 to 1: how strongly the snake sprints right now. */
    var level = 0f
        private set

    fun press(pointerId: Long) {
        fingers.add(pointerId)
    }

    fun release(pointerId: Long) {
        fingers.remove(pointerId)
    }

    /** [canSprint] is false while the game is not being played. */
    fun update(dt: Float, canSprint: Boolean) {
        val target = if (canSprint && fingers.isNotEmpty()) 1f else 0f
        val step = dt * SprintTuning.easeSpeed
        level += (target - level).coerceIn(-step, step)
    }

    fun clear() {
        fingers.clear()
    }
}
