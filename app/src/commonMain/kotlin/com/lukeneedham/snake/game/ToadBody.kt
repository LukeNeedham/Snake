package com.lukeneedham.snake.game

import kotlin.math.PI
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

/**
 * The body of a toad: it sits still, then hops a short way in a random direction. A toad with a [tameTarget] (the one of the lesson of
 * the poison) only hops to it, and stays there.
 */
internal class ToadBody(x: Float, y: Float, lifetime: Float, private val tameTarget: Vec2? = null) : CritterBody {
    private val course = CritterCourse(x, y)
    private val visit = CritterVisit(lifetime)
    private val gait = ToadGait(course, visit)

    private var isTameHopping = false
    private var tameHeight = 0f
    private var tameHopTime = 0f
    private var tameHopSpeed = 0f
    private var tameRest = 0.2f
    private var tameLanded = false

    override val x: Float get() = course.x
    override val y: Float get() = course.y
    override val facing: Int get() = course.facing
    override val mode: CritterMode get() = if (tameTarget != null) CritterMode.Roaming else visit.mode
    override val isMoving: Boolean get() = if (tameTarget != null) isTameHopping else gait.isHopping
    override val isFleeing: Boolean get() = false
    override val height: Float get() = if (tameTarget != null) tameHeight else gait.height
    override var landed: Boolean
        get() = if (tameTarget != null) tameLanded else gait.landed
        set(value) {
            if (tameTarget != null) tameLanded = value else gait.landed = value
        }
    override val isGone: Boolean get() = visit.isGone

    override fun update(dt: Float, arena: Arena, random: Random, snakeHead: Vec2?, aura: SnakeAura) {
        if (tameTarget != null) {
            hopTo(tameTarget, dt)
        } else if (visit.update(dt, arena.signedDistance(course.x, course.y))) {
            gait.update(dt, arena, random)
        }
    }

    /** Hops towards the target, a short hop at a time with a rest in between, until it is there. */
    private fun hopTo(target: Vec2, dt: Float) {
        if (isTameHopping) {
            tameHopTime += dt
            course.moveAlong(tameHopSpeed, dt)
            val progress = min(1f, tameHopTime / CritterTuning.hopDuration)
            tameHeight = sin(PI.toFloat() * progress) * CritterTuning.hopHeight
            if (progress >= 1f) {
                isTameHopping = false
                tameHeight = 0f
                tameLanded = true
                tameRest = restBetweenHops
            }
            return
        }
        val dx = target.x - course.x
        val dy = target.y - course.y
        val distance = sqrt(dx * dx + dy * dy)
        if (distance <= arrivedDistance) return
        tameRest -= dt
        if (tameRest > 0f) return
        course.aimAt(target.x, target.y)
        tameHopSpeed = min(hopLength, distance) / CritterTuning.hopDuration
        tameHopTime = 0f
        isTameHopping = true
    }

    private companion object {
        const val arrivedDistance = 0.5f
        const val hopLength = 7f
        const val restBetweenHops = 0.4f
    }
}
