package com.lukeneedham.snake.game

import kotlin.random.Random

/** The body of a toad: it sits still, then hops a short way in a random direction. */
internal class ToadBody(x: Float, y: Float, lifetime: Float) : CritterBody {
    private val course = CritterCourse(x, y)
    private val visit = CritterVisit(lifetime)
    private val gait = ToadGait(course, visit)

    override val x: Float get() = course.x
    override val y: Float get() = course.y
    override val facing: Int get() = course.facing
    override val mode: CritterMode get() = visit.mode
    override val isMoving: Boolean get() = gait.isHopping
    override val height: Float get() = gait.height
    override var landed: Boolean
        get() = gait.landed
        set(value) {
            gait.landed = value
        }
    override val isGone: Boolean get() = visit.isGone

    override fun update(dt: Float, arena: Arena, random: Random) {
        if (visit.update(dt, arena.signedDistance(course.x, course.y))) gait.update(dt, arena, random)
    }
}
