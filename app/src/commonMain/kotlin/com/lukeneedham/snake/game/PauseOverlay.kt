package com.lukeneedham.snake.game

import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * The pause overlay that all the menus are pages of: it is open or closed, and shows one [PausePage] at a time, in a horizontal
 * pager. A finger drags the pages ([dragBy]) and lets go ([release]); the pages then slide to rest on one ([update]).
 */
class PauseOverlay {
    var isOpen = false
        private set

    /** The page the pager rests on, or is sliding to. */
    var page = PausePage.Pause
        private set

    /** Where the pager is, in pages: 0 is the first page, 1.5 is half way between the second and the third. */
    var position = 0f
        private set

    /** How far a finger has dragged the pages, in pages: to the right is positive, and shows the page before. */
    private var drag = 0f

    /** Where the pages are on the screen right now, in pages: page [PausePage.ordinal] is drawn at `ordinal - offset` pages from the middle. */
    val offset: Float get() = position - drag

    /** The page that is nearest to the middle of the screen. */
    val visiblePage: PausePage get() = PausePage.entries[offset.roundToInt().coerceIn(0, lastIndex)]

    fun open(page: PausePage) {
        isOpen = true
        goTo(page, animate = false)
    }

    fun close() {
        isOpen = false
        drag = 0f
    }

    /** Slides to [page], or jumps there if not [animate]. */
    fun goTo(page: PausePage, animate: Boolean = true) {
        this.page = page
        drag = 0f
        if (!animate) position = page.ordinal.toFloat()
    }

    /** The finger moved by [pages] pages (a fraction of the screen width). The pages follow it, but not past the first and last. */
    fun dragBy(pages: Float) {
        drag = (drag + pages).coerceIn(position - lastIndex, position)
    }

    /** The finger let go: the pages go on to the next page if they were dragged far enough, else back. */
    fun release() {
        val visual = offset
        val target = when {
            drag > dragToTurn -> page.ordinal - 1
            drag < -dragToTurn -> page.ordinal + 1
            else -> page.ordinal
        }.coerceIn(0, lastIndex)
        page = PausePage.entries[target]
        position = visual
        drag = 0f
    }

    /** Slides the pages towards the page they rest on. */
    fun update(dt: Float) {
        if (!isOpen) return
        val target = page.ordinal.toFloat()
        val distance = target - position
        position = if (abs(distance) < 0.002f) target else position + distance * (dt * slideSpeed).coerceAtMost(1f)
    }

    private companion object {
        val lastIndex = PausePage.entries.size - 1

        /** A drag of this fraction of a page turns the page. */
        const val dragToTurn = 0.18f

        /** How fast the pages slide: the part of the way left that is covered per second. */
        const val slideSpeed = 14f
    }
}
