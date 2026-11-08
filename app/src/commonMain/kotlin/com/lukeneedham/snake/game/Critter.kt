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
        private val motion = CritterMotion(x, y, lifetime)
        override val x: Float get() = motion.x
        override val y: Float get() = motion.y
        override val facing: Int get() = motion.facing
        override val mode: CritterMode get() = motion.mode
        override val isMoving: Boolean get() = motion.isMoving
        override val height: Float get() = motion.height
        override var landed: Boolean
            get() = motion.landed
            set(value) { motion.landed = value }
        override val isGone: Boolean get() = motion.isGone

        override fun update(dt: Float, arena: Arena, random: Random) = motion.updateMouse(dt, arena, random)
    }

    /** Sits still, then hops a short way in a random direction. Eating one has the effect of its [kind]. */
    class Toad(override val id: Int, val kind: ToadKind, x: Float, y: Float, lifetime: Float) : Critter {
        private val motion = CritterMotion(x, y, lifetime)
        override val x: Float get() = motion.x
        override val y: Float get() = motion.y
        override val facing: Int get() = motion.facing
        override val mode: CritterMode get() = motion.mode
        override val isMoving: Boolean get() = motion.isMoving
        override val height: Float get() = motion.height
        override var landed: Boolean
            get() = motion.landed
            set(value) { motion.landed = value }
        override val isGone: Boolean get() = motion.isGone

        override fun update(dt: Float, arena: Arena, random: Random) = motion.updateToad(dt, arena, random)
    }
}
