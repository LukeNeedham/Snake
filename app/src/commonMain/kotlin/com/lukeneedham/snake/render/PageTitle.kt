package com.lukeneedham.snake.render

import com.lukeneedham.snake.game.MenuLayout

/** Every page of the pause overlay has its title at the same place at the top; the close button is centred on it. */
internal object PageTitle {
    /** How far under the status bar the title is, in art pixels. */
    val top: Int = MenuLayout.pageTitleTop.toInt()

    /** How far under the status bar the title ends: the content of a page starts a little under that. */
    val bottom: Int = top + MenuLayout.pageTitleHeight.toInt()
}
