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

    /** How high a hopping toad is above the ground. */
    val height: Float

    /** Set when a toad lands, for the dust: the reader clears it. */
    var landed: Boolean
    val isGone: Boolean

    fun update(dt: Float, arena: Arena, random: Random)
}
