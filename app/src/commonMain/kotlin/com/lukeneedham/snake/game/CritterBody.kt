package com.lukeneedham.snake.game

import kotlin.random.Random

/** What can be read from a critter, and how it moves: a [Critter] gets it from the body of its kind. */
interface CritterBody {
    val x: Float
    val y: Float

    /** 1 looks right, -1 looks left. */
    val facing: Int
    val mode: CritterMode

    /** Running for a mouse, in the air or about to be for a toad, hopping or flying for a bird. */
    val isMoving: Boolean

    /** A mouse that has seen the snake and runs from it, or a bird that has seen it and is about to take off. */
    val isFleeing: Boolean

    /** How high a hopping toad or a flying bird is above the ground. */
    val height: Float

    /** Set when a toad or a bird lands, or a bird takes off, for the dust: the reader clears it. */
    var landed: Boolean
    val isGone: Boolean

    /** Whether the snake can eat it right now: a bird that is up in the air cannot be reached. */
    val isEdible: Boolean get() = true

    /** In the air and flapping: a bird that flies in or flies off. */
    val isAirborne: Boolean get() = false

    /** Moves the critter [dy] down: the path that repeats puts the snake back a stretch, and the critters of the lessons with it. */
    fun shiftDown(dy: Float)

    /**
     * Steps the critter; [snakeHead] is where the head of the snake is, for the critters that run from it, and [aura] is what the
     * snake gives off while it has a power up.
     */
    fun update(dt: Float, arena: Arena, random: Random, snakeHead: Vec2? = null, aura: SnakeAura = SnakeAura.None)
}
