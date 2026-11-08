package com.lukeneedham.snake.game

import kotlin.random.Random

/** An animal that comes out of the grass: a mouse to eat, or a poisonous toad to avoid. */
sealed interface Critter {
    val id: Int
    val x: Float
    val y: Float

    /** 1 looks right, -1 looks left. */
    val facing: Int
    val mode: CritterMode

    /** Running for a mouse, in the air or about to be for a toad. */
    val isMoving: Boolean

    /** How high a hopping toad is above the ground. */
    val height: Float

    /** Set when a toad lands, for the dust: the reader clears it. */
    var landed: Boolean
    val isGone: Boolean

    fun update(dt: Float, arena: Arena, random: Random)

    /** Runs fast for a moment, stands still and looks around, then runs again. */
    class Mouse(override val id: Int, x: Float, y: Float, lifetime: Float) : Critter {
        private val course = CritterCourse(x, y)
        private val visit = CritterVisit(lifetime)
        private val gait = MouseGait(course, visit)

        override val x: Float get() = course.x
        override val y: Float get() = course.y
        override val facing: Int get() = course.facing
        override val mode: CritterMode get() = visit.mode
        override val isMoving: Boolean get() = gait.isRunning
        override val height: Float get() = 0f
        override var landed = false
        override val isGone: Boolean get() = visit.isGone

        override fun update(dt: Float, arena: Arena, random: Random) {
            if (visit.update(dt, arena.signedDistance(course.x, course.y))) gait.update(dt, arena, random)
        }
    }

    /** Sits still, then hops a short way in a random direction. Eating one has the effect of its [kind]. */
    class Toad(override val id: Int, val kind: ToadKind, x: Float, y: Float, lifetime: Float) : Critter {
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
}
