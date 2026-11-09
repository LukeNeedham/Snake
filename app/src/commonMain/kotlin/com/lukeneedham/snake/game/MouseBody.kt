package com.lukeneedham.snake.game

import kotlin.math.min
import kotlin.math.sqrt
import kotlin.random.Random

/**
 * The body of a mouse: it runs fast for a moment, stands still and looks around, then runs again. A mouse with a [tameTarget] (the one of the
 * lesson of the hunger) only runs to it, and stays there: it does not mind the snake.
 */
internal class MouseBody(x: Float, y: Float, lifetime: Float, private val tameTarget: Vec2? = null) : CritterBody {
    private val course = CritterCourse(x, y)
    private val visit = CritterVisit(lifetime)
    private val gait = MouseGait(course, visit)
    private var isWalking = false

    override val x: Float get() = course.x
    override val y: Float get() = course.y
    override val facing: Int get() = course.facing
    override val mode: CritterMode get() = if (tameTarget != null) CritterMode.Roaming else visit.mode
    override val isMoving: Boolean get() = if (tameTarget != null) isWalking else gait.isRunning
    override val isFleeing: Boolean get() = gait.isFleeing
    override val height: Float get() = 0f
    override var landed = false
    override val isGone: Boolean get() = visit.isGone

    override fun update(dt: Float, arena: Arena, random: Random, snakeHead: Vec2?, aura: SnakeAura) {
        if (tameTarget != null) {
            runTo(tameTarget, dt)
        } else if (visit.update(dt, arena.signedDistance(course.x, course.y))) {
            gait.update(dt, arena, random, snakeHead, aura.noticeScale, aura.attractsMice)
        }
    }

    private fun runTo(target: Vec2, dt: Float) {
        val dx = target.x - course.x
        val dy = target.y - course.y
        val distance = sqrt(dx * dx + dy * dy)
        isWalking = distance > arrivedDistance
        if (!isWalking) return
        course.aimAt(target.x, target.y)
        course.moveBy(min(CritterTuning.mouseSpeed * dt, distance))
    }

    private companion object {
        /** A mouse this close to its target has got there. */
        const val arrivedDistance = 0.5f
    }
}
