package com.lukeneedham.snake.game

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect

/** Where the menu indicator, the area that opens the menu, and the buttons of the menu are, in world units: the drawing and the touches both use it. */
object MenuLayout {
    private const val margin = 3f

    /** The pause indicator is tiny: a few pixels under the score. */
    private const val indicatorSize = 5f

    /** The score is above the indicator, in the same corner, at the scale of 2. */
    private const val scoreHeight = 10f

    /** Any press in this top left area of the screen opens the menu, so the finger does not have to find the indicator. */
    private const val areaWidth = 48f
    private const val areaBottom = margin + scoreHeight + margin + indicatorSize + margin

    private const val closeSize = 12f

    /** The title of a page of the overlay is this far under the status bar, and is this tall (the pixel font at the scale of 2). */
    const val pageTitleTop = 4f
    const val pageTitleHeight = 10f

    /** The dots of the page indicator at the bottom of the overlay. */
    const val dotSize = 5f
    private const val dotGap = 4f
    private const val dotBottom = 8f
    private const val dotSlack = 4f

    const val itemWidth = 70f
    const val itemHeight = 15f
    private const val itemGap = 5f

    /** The little pause (or menu) indicator in the top left corner under the score, which is under the status bar of height [topInset]. */
    fun menuIndicator(topInset: Int): Rect {
        val top = topInset + margin + scoreHeight + margin
        return Rect(margin, top, margin + indicatorSize, top + indicatorSize)
    }

    /** The top left part of the screen, from the top edge down to under the indicator: a press anywhere in it opens the menu. */
    fun menuArea(topInset: Int): Rect = Rect(0f, 0f, areaWidth, topInset + areaBottom)

    fun isOnMenuButton(topInset: Int, x: Float, y: Float): Boolean = menuArea(topInset).contains(Offset(x, y))

    /** The close button in the top left corner of the overlay, under the status bar of height [topInset]: centred on the title of the page. */
    fun closeButton(topInset: Int): Rect {
        val top = topInset + pageTitleTop + pageTitleHeight / 2f - closeSize / 2f
        return Rect(margin, top, margin + closeSize, top + closeSize)
    }

    fun isOnCloseButton(topInset: Int, x: Float, y: Float): Boolean {
        val rect = closeButton(topInset)
        return x >= rect.left - dotSlack && x <= rect.right + dotSlack && y >= rect.top - dotSlack && y <= rect.bottom + dotSlack
    }

    /** The dot of the [index]th page in the page indicator, in the middle at the bottom of the screen. */
    fun pageDot(arena: Arena, index: Int): Rect {
        val count = PausePage.entries.size
        val total = count * dotSize + (count - 1) * dotGap
        val left = (arena.width - total) / 2f + index * (dotSize + dotGap)
        val top = arena.height - arena.bottomInset - dotBottom - dotSize
        return Rect(left, top, left + dotSize, top + dotSize)
    }

    /** The page whose dot a finger at [x], [y] is on, if any: the dots are small, so a touch near one counts. */
    fun pageAt(arena: Arena, x: Float, y: Float): PausePage? = PausePage.entries.firstOrNull { page ->
        val rect = pageDot(arena, page.ordinal)
        x >= rect.left - dotSlack && x <= rect.right + dotSlack && y >= rect.top - dotSlack && y <= rect.bottom + dotSlack
    }

    /** The button of the [index]th of [count] items: they are stacked in the middle of the screen. */
    fun item(arena: Arena, count: Int, index: Int): Rect {
        val total = count * itemHeight + (count - 1) * itemGap
        val top = (arena.height - total) / 2f + index * (itemHeight + itemGap)
        val left = (arena.width - itemWidth) / 2f
        return Rect(left, top, left + itemWidth, top + itemHeight)
    }
}
