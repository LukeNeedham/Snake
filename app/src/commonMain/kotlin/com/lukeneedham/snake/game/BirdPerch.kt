package com.lukeneedham.snake.game

import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

/** A bird on the ground: it looks around and hops a little way now and then, until its stay is over. */
internal class BirdPerch(private val course: CritterCourse, private val staySeconds: Float) {

    var isHopping = false
        private set

    /** How high the bird is above the ground. */
    var height = 0f
        private set

    /** Set once the stay is over and the bird should fly off. */
    var isDone = false
        private set

    private var age = 0f
    private var timer = 0.5f
    private var lookTimer = 0.4f
    private var hopTime = 0f
    private var hopSpeed = 0f

    fun update(dt: Float, arena: Arena, random: Random) {
        age += dt
        if (age > staySeconds) isDone = true
        if (isHopping) hop(dt, random) else standAndLook(dt, arena, random)
    }

    /** Back on the ground at once, for a bird that takes off. */
    fun freeze() {
        isHopping = false
        height = 0f
    }

    private fun hop(dt: Float, random: Random) {
        hopTime += dt
        course.moveAlong(hopSpeed, dt)
        val progress = (hopTime / BirdTuning.hopSeconds).coerceAtMost(1f)
        height = sin(PI.toFloat() * progress) * BirdTuning.hopHeight
        if (progress >= 1f) {
            freeze()
            timer = 0.5f + random.nextFloat() * 1.5f
        }
    }

    private fun standAndLook(dt: Float, arena: Arena, random: Random) {
        timer -= dt
        lookTimer -= dt
        if (lookTimer <= 0f) {
            course.lookTheOtherWay()
            lookTimer = 0.3f + random.nextFloat() * 0.8f
        }
        if (timer <= 0f) {
            course.chooseDirection(CritterMode.Roaming, arena, random)
            hopSpeed = (3f + random.nextFloat() * 3f) / BirdTuning.hopSeconds
            hopTime = 0f
            isHopping = true
        }
    }
}
