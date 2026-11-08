package com.lukeneedham.snake.game

import kotlin.math.PI
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

/** A toad sits still, then hops a short way in a random direction. A toad that smells the snake's repellent hops away from it instead. */
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

    /** [repeller] is the head of a snake that toads keep away from, or null if this toad does not mind the snake. */
    fun update(dt: Float, arena: Arena, random: Random, repeller: Vec2?) {
        timer -= dt
        val scaredOf = repeller?.takeIf { visit.mode == CritterMode.Roaming && course.isNear(it.x, it.y, CritterTuning.repelDistance) }
        if (scaredOf != null) timer = min(timer, scaredWait)
        if (isHopping) hop(dt, random) else if (timer <= 0f) startHop(arena, random, scaredOf)
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

    private fun startHop(arena: Arena, random: Random, scaredOf: Vec2?) {
        course.chooseDirection(visit.mode, arena, random)
        if (scaredOf != null) {
            course.runAwayFrom(scaredOf.x, scaredOf.y)
            if (course.isHeadingOut(arena) && arena.signedDistance(course.x, course.y) > -turnBackDepth) course.aimInside(arena, random)
        }
        val distance = if (scaredOf != null) 9f else if (visit.mode == CritterMode.Roaming) 5f + random.nextFloat() * 7f else 9f
        hopSpeed = distance / CritterTuning.hopDuration
        hopTime = 0f
        isHopping = true
    }

    private companion object {
        /** A toad that smells the repellent hops away after at most this long. */
        const val scaredWait = 0.1f

        /** A scared toad that hops closer to the wall than this turns back in. */
        const val turnBackDepth = 10f
    }
}
