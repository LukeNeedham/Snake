package com.lukeneedham.snake.game

/**
 * The corner of the screen that sprints: a finger that goes down in it sprints for as long as it is held, and never turns the
 * snake. A finger anywhere else is a tap that turns the snake, so one thumb can turn while the other sprints. In world units.
 */
object SprintZone {
    /** The zone is the square of this size in the bottom right corner. */
    const val size = 34f

    /** How far the middle of the drawn button is from the bottom and the right edge. */
    const val buttonInset = 17f
    const val buttonRadius = 11f

    fun contains(arena: Arena, x: Float, y: Float): Boolean = x >= arena.width - size && y >= arena.height - size

    fun buttonX(arena: Arena): Float = arena.width - buttonInset

    fun buttonY(arena: Arena): Float = arena.height - buttonInset
}
