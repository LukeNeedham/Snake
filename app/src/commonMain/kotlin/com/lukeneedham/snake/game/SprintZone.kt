package com.lukeneedham.snake.game

/**
 * The corner of the screen that sprints: a finger that goes down in it sprints for as long as it is held, and never turns the
 * snake. A finger anywhere else is a tap that turns the snake, so one thumb can turn while the other sprints. In world units.
 */
object SprintZone {
    /** The zone is the square of this size in the bottom corner of the chosen [SprintSide]. */
    const val size = 34f

    /**
     * How far the middle of the drawn button is from the bottom and the side edge. The button is only a small sign of the zone: a
     * finger anywhere in the whole corner square sprints, not just on the button.
     */
    const val buttonInset = 13f
    const val buttonRadius = 8f

    fun contains(arena: Arena, side: SprintSide, x: Float, y: Float): Boolean {
        if (y < arena.height - size) return false
        return when (side) {
            SprintSide.Left -> x <= size
            SprintSide.Right -> x >= arena.width - size
        }
    }

    fun buttonX(arena: Arena, side: SprintSide): Float = when (side) {
        SprintSide.Left -> buttonInset
        SprintSide.Right -> arena.width - buttonInset
    }

    fun buttonY(arena: Arena): Float = arena.height - buttonInset
}
