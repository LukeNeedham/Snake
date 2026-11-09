package com.lukeneedham.snake.game

/** What a finger that goes down on the screen presses. */
sealed interface PressTarget {
    /** The scene: a tap that turns the snake, or starts, or restarts the game. */
    data object Turn : PressTarget

    /** The sprint zone, a corner of the screen. */
    data object Sprint : PressTarget

    /** The button in the top left corner that opens the menu. */
    data object MenuButton : PressTarget

    /** The column of arrows in the bottom right corner: it opens the pause overlay on the page of what is on. */
    data object StatusCorner : PressTarget

    /** The stomach in the top right corner: it opens the pause overlay on the hunger page. */
    data object Stomach : PressTarget

    /** The close button in the top left corner of the overlay: it resumes the game. */
    data object CloseButton : PressTarget

    /** A dot of the page indicator of the overlay. */
    data class PageDot(val page: PausePage) : PressTarget

    data class Item(val item: MenuItem) : PressTarget

    /** The overlay is open and the finger is not on any of its buttons. */
    data object Backdrop : PressTarget

    companion object {
        /** What is at [x], [y] of the world, where [topInset] is the height of the status bar. */
        fun at(world: GameWorld, topInset: Int, x: Float, y: Float): PressTarget {
            if (world.isMenuOpen) {
                if (MenuLayout.isOnCloseButton(topInset, x, y)) return CloseButton
                MenuLayout.pageAt(world.arena, x, y)?.let { return PageDot(it) }
                if (world.overlay.visiblePage == PausePage.Pause) {
                    val items = world.menuItems
                    items.forEachIndexed { index, item ->
                        val rect = MenuLayout.item(world.arena, items.size, index)
                        if (x >= rect.left && x <= rect.right && y >= rect.top && y <= rect.bottom) return Item(item)
                    }
                }
                return Backdrop
            }
            if (MenuLayout.isOnMenuButton(topInset, x, y)) return MenuButton
            if (StomachLayout.contains(world.arena, topInset, x, y)) return Stomach
            if (StatusLayout.contains(world.arena, world.sprintSide, world.statuses.size, x, y)) return StatusCorner
            if (SprintZone.contains(world.arena, world.sprintSide, x, y)) return Sprint
            return Turn
        }
    }
}
