package com.lukeneedham.snake.game

/** What a finger that goes down on the screen presses. */
sealed interface PressTarget {
    /** The scene: a tap that turns the snake, or starts, or restarts the game. */
    data object Turn : PressTarget

    /** The sprint zone, a corner of the screen. */
    data object Sprint : PressTarget

    /** The button in the top left corner that opens the menu. */
    data object MenuButton : PressTarget

    data class Item(val item: MenuItem) : PressTarget

    /** The menu is open and the finger is not on any of its buttons. */
    data object Backdrop : PressTarget

    companion object {
        /** What is at [x], [y] of the world, where [topInset] is the height of the status bar. */
        fun at(world: GameWorld, topInset: Int, x: Float, y: Float): PressTarget {
            if (world.isMenuOpen) {
                val items = world.menuItems
                items.forEachIndexed { index, item ->
                    val rect = MenuLayout.item(world.arena, items.size, index)
                    if (x >= rect.left && x <= rect.right && y >= rect.top && y <= rect.bottom) return Item(item)
                }
                return Backdrop
            }
            if (MenuLayout.isOnMenuButton(topInset, x, y)) return MenuButton
            if (SprintZone.contains(world.arena, world.sprintSide, x, y)) return Sprint
            return Turn
        }
    }
}
