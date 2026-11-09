package com.lukeneedham.snake.game

import kotlin.math.min

/**
 * Plays the story and the lessons at the start from one [IntroStage] to the next: it only knows what time it is in the story and what
 * the player has done, and the world and the drawing ask it what to do. Nothing here moves the snake. Without [isTutorial] there is no
 * story and there are no lessons: the snake only wakes up and goes. A [isReplay] is the lessons again, from the sprint on, in the room
 * of the tutorial, for a player who knows the way: it is over once the bird is eaten.
 */
class IntroDirector(val isTutorial: Boolean = true, val isReplay: Boolean = false) {

    var stage = IntroStage.Sleeping
        private set

    /** Seconds since the current [stage] began. */
    var stageTime = 0f
        private set

    /** Whether the player has held the sprint corner for long enough. */
    var hasZoomed = false
        private set

    /** How long the sprint corner has been held without a break. */
    private var zoomHeldSeconds = 0f

    /** Whether the snake has been dizzy in the lesson of the toad: it must stay that way until it is over. */
    private var wasDizzy = false

    /** Whether the animal of the lesson has been sent in already. */
    private var animalSent = false

    /** Whether the hunger bar is on the screen: from the lesson of the hunger on. */
    val showsHunger: Boolean get() = isTutorial && stage >= IntroStage.Hunger

    /** Whether the sprint button is on the screen: from the lesson of the sprint on. */
    val showsSprintButton: Boolean get() = isTutorial && stage >= IntroStage.Sprint

    fun update(dt: Float) {
        stageTime += dt
        val length = stage.seconds ?: return
        if (stageTime >= length) enter(next())
    }

    /** The part that follows this one: without the tutorial the snake goes straight from waking up to steering. */
    private fun next(): IntroStage =
        if (stage == IntroStage.Waking && !isTutorial) IntroStage.Steering else IntroStage.entries[stage.ordinal + 1]

    /** The player tapped to wake the snake. */
    fun wake() {
        if (stage == IntroStage.Sleeping) enter(IntroStage.Waking)
    }

    /** The player turned the snake: that is all there is to learn about turning, so now it is up to them. */
    fun turned() {
        if (stage == IntroStage.Teaching) enter(IntroStage.Steering)
    }

    /** The snake is in the room of the tutorial: the lesson of the sprint begins. */
    fun enteredRoom() {
        if (stage == IntroStage.Steering && isTutorial) enter(IntroStage.Sprint)
    }

    /** Called every step: [isHeld] is whether a finger is in the sprint corner. Only counts in the lesson of the sprint. */
    fun trackSprint(isHeld: Boolean, dt: Float) {
        if (stage != IntroStage.Sprint) return
        zoomHeldSeconds = if (isHeld) zoomHeldSeconds + dt else 0f
        if (zoomHeldSeconds >= IntroTuning.zoomSeconds) {
            hasZoomed = true
            enter(IntroStage.Hunger)
        }
    }

    /** Called every step in the lesson of the hunger, with how full the snake is: once the bar has shown for a while and run down, the mouse comes. */
    fun hungerShown(level: Float) {
        if (stage == IntroStage.Hunger && stageTime >= IntroTuning.hungerTextSeconds && level <= IntroTuning.hungerDemoLevel) enter(IntroStage.Mouse)
    }

    /** True once, when it is time for the animal of the lesson to come in from the grass. */
    fun takeAnimalDue(): Boolean {
        val delay = when (stage) {
            IntroStage.Mouse -> IntroTuning.mouseDelay
            IntroStage.Toad -> IntroTuning.toadDelay
            IntroStage.Bird -> IntroTuning.birdDelay
            else -> return false
        }
        if (animalSent || stageTime < delay) return false
        animalSent = true
        return true
    }

    /** The snake ate [critter]: the lesson of that animal is learned. */
    fun ate(critter: Critter) {
        when {
            stage == IntroStage.Mouse && critter is Critter.Mouse -> enter(IntroStage.Toad)
            stage == IntroStage.Toad && critter is Critter.Toad -> enter(IntroStage.Dizzy)
            stage == IntroStage.Bird && critter is Critter.Bird -> enter(IntroStage.Return)
        }
    }

    /** Called every step with whether the snake is dizzy: the lesson of the toad is over once it has been dizzy and is not any more. */
    fun dizzy(isDizzy: Boolean) {
        if (stage != IntroStage.Dizzy) return
        if (isDizzy) wasDizzy = true else if (wasDizzy) enter(IntroStage.Bird)
    }

    /** The snake is home again, after the lessons. */
    fun arrivedHome() {
        if (stage == IntroStage.Return) enter(IntroStage.Leaving)
    }

    /** Jumps to a part of the lessons, as if everything before it was done: for the tests and the screenshots. */
    internal fun jumpTo(target: IntroStage) {
        hasZoomed = target >= IntroStage.Hunger
        enter(target)
    }

    private fun enter(next: IntroStage) {
        stage = next
        stageTime = 0f
        animalSent = false
    }

    /** How fast the snake moves, as a part of its normal speed: it lies still until the bubble pops, and then it speeds up. */
    val snakeSpeed: Float
        get() = when (stage) {
            IntroStage.Sleeping, IntroStage.Waking, IntroStage.Tasting, IntroStage.Rumbling, IntroStage.Thinking -> 0f
            IntroStage.Popped -> min(1f, stageTime / IntroStage.Popped.seconds!!)
            else -> 1f
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
