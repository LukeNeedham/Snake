package com.lukeneedham.snake.game

/** How long a critter stays: it enters the oval, roams it for its [lifetime], then leaves into the grass. */
internal class CritterVisit(private val lifetime: Float) {
    var mode = CritterMode.Entering
        private set

    var isGone = false
        private set

    private var age = 0f

    /** Ages the visit; [distance] is how far the critter is from the edge of the oval. False once it is gone. */
    fun update(dt: Float, distance: Float): Boolean {
        age += dt
        if (mode == CritterMode.Entering && distance < -enteredDepth) mode = CritterMode.Roaming
        if (mode == CritterMode.Entering && age > giveUpAge) mode = CritterMode.Leaving
        if (mode == CritterMode.Roaming && age > lifetime) mode = CritterMode.Leaving
        if (mode == CritterMode.Leaving && distance > WorldTuning.grassDepth - 1f) isGone = true
        return !isGone
    }

    private companion object {
        /** How far inside the oval a critter has to be to have entered it. */
        const val enteredDepth = 5f

        /** A critter that still has not entered the oval after this many seconds leaves. */
        const val giveUpAge = 25f
    }
}
