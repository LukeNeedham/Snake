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

    fun update(dt: Float) {
        stageTime += dt
        val length = stage.seconds ?: return
        if (stageTime >= length) {
            stage = IntroStage.entries[stage.ordinal + 1]
            stageTime = 0f
        }
    }

    /** A tap during the story cuts it short, but not while the snake is still asleep, so that a double tap on the title does not skip it. */
    fun skip() {
        if (!stage.isStory || stage == IntroStage.Sleeping) return
        stage = IntroStage.Teaching
        stageTime = 0f
    }

    /** The player turned the snake: that is all there is to learn, so now it is up to them. */
    fun turned() {
        if (stage != IntroStage.Teaching) return
        stage = IntroStage.Steering
        stageTime = 0f
    }

    /** How fast the snake moves, as a part of its normal speed: slowly curled up until the bubble pops, then up to normal. */
    val snakeSpeed: Float
        get() = when (stage) {
            IntroStage.Sleeping, IntroStage.Rumbling, IntroStage.Thinking -> IntroTuning.curledSpeed
            IntroStage.Popped -> {
                val progress = min(1f, stageTime / IntroStage.Popped.seconds!!)
                IntroTuning.curledSpeed + (1f - IntroTuning.curledSpeed) * progress
            }
            IntroStage.Teaching, IntroStage.Steering -> 1f
        }

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
}
