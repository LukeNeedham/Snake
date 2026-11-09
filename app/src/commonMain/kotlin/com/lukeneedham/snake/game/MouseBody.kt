package com.lukeneedham.snake.game

import kotlin.random.Random

/** The body of a mouse: it runs fast for a moment, stands still and looks around, then runs again. */
internal class MouseBody(x: Float, y: Float, lifetime: Float, private val isTame: Boolean = false) : CritterBody {
    private val course = CritterCourse(x, y)
    private val visit = CritterVisit(lifetime)
    private val gait = MouseGait(course, visit)

    override val x: Float get() = course.x
    override val y: Float get() = course.y
    override val facing: Int get() = course.facing
    override val mode: CritterMode get() = if (isTame) CritterMode.Roaming else visit.mode
    override val isMoving: Boolean get() = gait.isRunning
    override val isFleeing: Boolean get() = gait.isFleeing
    override val height: Float get() = 0f
    override var landed = false
    override val isGone: Boolean get() = visit.isGone

    override fun shiftDown(dy: Float) {
        course.y += dy
    }

    override fun update(dt: Float, arena: Arena, random: Random, snakeHead: Vec2?, aura: SnakeAura) {
        if (isTame) return
        if (visit.update(dt, arena.signedDistance(course.x, course.y))) gait.update(dt, arena, random, snakeHead, aura.noticeScale, aura.attractsMice)
    }
}
