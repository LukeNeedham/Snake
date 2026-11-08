package com.lukeneedham.snake.game

import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

/** A toad sits still, then hops a short way in a random direction. */
internal class ToadGait(private val course: CritterCourse, private val visit: CritterVisit) {

    var isHopping = false
        private set

    /** How high the toad is above the ground. */
    var height = 0f
        private set

    /** Set when the toad lands, for the dust: the reader clears it. */
    var landed = false

    private var timer = 0.2f
    private var hopTime = 0f
    private var hopSpeed = 0f

    fun update(dt: Float, arena: Arena, random: Random) {
        timer -= dt
        if (isHopping) hop(dt, random) else if (timer <= 0f) startHop(arena, random)
    }

    private fun hop(dt: Float, random: Random) {
        hopTime += dt
        course.moveAlong(hopSpeed, dt)
        val progress = (hopTime / CritterTuning.hopDuration).coerceAtMost(1f)
        height = sin(PI.toFloat() * progress) * CritterTuning.hopHeight
        if (progress >= 1f) {
            isHopping = false
            height = 0f
            landed = true
            timer = if (visit.mode == CritterMode.Roaming) 0.8f + random.nextFloat() * 1.6f else 0.3f
        }
    }

    private fun startHop(arena: Arena, random: Random) {
        course.chooseDirection(visit.mode, arena, random)
        val distance = if (visit.mode == CritterMode.Roaming) 5f + random.nextFloat() * 7f else 9f
        hopSpeed = distance / CritterTuning.hopDuration
        hopTime = 0f
        isHopping = true
    }
}
