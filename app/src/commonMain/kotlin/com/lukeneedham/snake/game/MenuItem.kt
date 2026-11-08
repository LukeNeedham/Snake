package com.lukeneedham.snake.game

/** The buttons of the menu: it opens from the button in the top left corner, and pauses the game while it is playing. */
enum class MenuItem {
    /** Closes the menu, and carries on with the game if it was paused. */
    Resume,

    /** Starts a new game. Only in the menu of a game that is paused. */
    Restart,

    /** Opens the settings page. */
    Settings,
}
