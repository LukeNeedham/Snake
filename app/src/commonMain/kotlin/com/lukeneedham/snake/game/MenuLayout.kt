package com.lukeneedham.snake.game

import androidx.compose.ui.geometry.Rect

/** Where the menu button and the buttons of the menu are, in world units: the drawing and the touches both use it. */
object MenuLayout {
    private const val buttonMargin = 3f
    private const val buttonSize = 14f

    /** A finger is bigger than the button, so a touch this far from it still counts. */
    private const val touchSlack = 3f

    const val itemWidth = 70f
    const val itemHeight = 15f
    private const val itemGap = 5f

    /** The small button in the top left corner, under the status bar of height [topInset]. */
    fun menuButton(topInset: Int): Rect = Rect(buttonMargin, topInset + buttonMargin, buttonMargin + buttonSize, topInset + buttonMargin + buttonSize)

    fun isOnMenuButton(topInset: Int, x: Float, y: Float): Boolean {
        val rect = menuButton(topInset)
        return x >= rect.left - touchSlack && x <= rect.right + touchSlack && y >= rect.top - touchSlack && y <= rect.bottom + touchSlack
    }

    /** The button of the [index]th of [count] items: they are stacked in the middle of the screen. */
    fun item(arena: Arena, count: Int, index: Int): Rect {
        val total = count * itemHeight + (count - 1) * itemGap
        val top = (arena.height - total) / 2f + index * (itemHeight + itemGap)
        val left = (arena.width - itemWidth) / 2f
        return Rect(left, top, left + itemWidth, top + itemHeight)
    }
}
