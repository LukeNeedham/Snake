package com.lukeneedham.snake.game

import kotlin.math.min

/**
 * Plays the story at the start from one [IntroStage] to the next: it only knows what time it is in the story, and the world and the
 * drawing ask it what to do. Nothing here moves the snake.
 */
class IntroDirector {

    var stage = IntroStage.Sleeping
        private set

    /** Seconds since the current [stage] began. */
    var stageTime = 0f
        private set

    /** Whether the snake is half way along the path: the lesson of the sprint starts there. */
    var reachedHalfway = false
        private set

    /** Whether the player has held the sprint corner for long enough: the path that goes on and on has an end from then on. */
    var hasZoomed = false
        private set

    /** How long the sprint corner has been held without a break. */
    private var zoomHeldSeconds = 0f

    fun update(dt: Float) {
        stageTime += dt
        val length = stage.seconds ?: return
        if (stageTime >= length) enter(IntroStage.entries[stage.ordinal + 1])
    }

    /** The player tapped to wake the snake. */
    fun wake() {
        if (stage == IntroStage.Sleeping) enter(IntroStage.Waking)
    }

    /** The player turned the snake: that is all there is to learn about turning, so now it is up to them. */
    fun turned() {
        if (stage == IntroStage.Teaching) enter(IntroStage.Steering)
    }

    fun halfway() {
        if (stage == IntroStage.Steering) reachedHalfway = true
    }

    /** Called every step: [isHeld] is whether a finger is in the sprint corner. Only counts once the lesson of the sprint has begun. */
    fun trackSprint(isHeld: Boolean, dt: Float) {
        if (!reachedHalfway || hasZoomed) return
        zoomHeldSeconds = if (isHeld) zoomHeldSeconds + dt else 0f
        if (zoomHeldSeconds >= IntroTuning.zoomSeconds) hasZoomed = true
    }

    private fun enter(next: IntroStage) {
        stage = next
        stageTime = 0f
    }

    /** How fast the snake moves, as a part of its normal speed: it lies still until the bubble pops, and then it speeds up. */
    val snakeSpeed: Float
        get() = when (stage) {
            IntroStage.Sleeping, IntroStage.Waking, IntroStage.Tasting, IntroStage.Rumbling, IntroStage.Thinking -> 0f
            IntroStage.Popped -> min(1f, stageTime / IntroStage.Popped.seconds!!)
            IntroStage.Teaching, IntroStage.Steering -> 1f
        }

    val isAsleep: Boolean get() = stage == IntroStage.Sleeping

    /** Whether the eyes are shut: all the time while asleep, and in three blinks while it wakes up. */
    val eyesClosed: Boolean
        get() = when (stage) {
            IntroStage.Sleeping -> true
            IntroStage.Waking -> stageTime < openingAt || (stageTime >= firstBlink && (stageTime - firstBlink) % blinkCycle < blinkLength)
            else -> false
        }

    /** How far the tongue's flicks are, from 0 to 1, while it sticks it out; 0 at any other time. */
    val tongueProgress: Float
        get() = if (stage == IntroStage.Tasting) min(1f, stageTime / IntroStage.Tasting.seconds!!) else 0f

    /** How strongly the stomach rumbles, from 0 to 1: it builds up and dies down. */
    val rumble: Float
        get() {
            if (stage != IntroStage.Rumbling) return 0f
            val fadeIn = stageTime / IntroTuning.rumbleFadeSeconds
            val fadeOut = (IntroStage.Rumbling.seconds!! - stageTime) / IntroTuning.rumbleFadeSeconds
            return minOf(fadeIn, fadeOut, 1f).coerceAtLeast(0f)
        }

    /** How much of its size the [index]th thought bubble has grown to, from 0 (not there yet) to 1. */
    fun bubbleGrowth(index: Int): Float {
        if (stage != IntroStage.Thinking) return 0f
        val age = stageTime - index * IntroTuning.bubbleGap
        if (age <= 0f) return 0f
        val progress = min(1f, age / IntroTuning.bubbleGrowSeconds)
        return 1f - (1f - progress) * (1f - progress)
    }

    /** How far the pop is, from 0 to 1, while the big bubble pops; 0 at any other time. */
    val popProgress: Float
        get() = if (stage == IntroStage.Popped) min(1f, stageTime / IntroStage.Popped.seconds!!) else 0f

    private companion object {
        /** The eyes open for the first time, then close in a blink every [blinkCycle] seconds for [blinkLength], starting at [firstBlink]. */
        const val openingAt = 0.3f
        const val firstBlink = 0.6f
        const val blinkCycle = 0.45f
        const val blinkLength = 0.15f
    }
}
