package com.lukeneedham.snake.game

import kotlin.random.Random

/** What can be read from a critter, and how it moves: a [Critter] gets it from the body of its kind. */
interface CritterBody {
    val x: Float
    val y: Float

    /** 1 looks right, -1 looks left. */
    val facing: Int
    val mode: CritterMode

    /** Running for a mouse, in the air or about to be for a toad. */
    val isMoving: Boolean

    /** A mouse that has seen the snake and runs from it. */
    val isFleeing: Boolean

    /** How high a hopping toad is above the ground. */
    val height: Float

    /** Set when a toad lands, for the dust: the reader clears it. */
    var landed: Boolean
    val isGone: Boolean

    /** Steps the critter; [snakeHead] is where the head of the snake is, for the critters that run from it. */
    fun update(dt: Float, arena: Arena, random: Random, snakeHead: Vec2? = null)
}
