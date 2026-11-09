package com.lukeneedham.snake.game

import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.roundToInt

/**
 * The pause overlay that all the menus are pages of: it is open or closed, and shows one [PausePage] at a time, in a horizontal
 * pager that wraps around, so the last page is followed by the first. A finger drags the pages ([dragBy]) and lets go
 * ([release]); the pages then slide to rest on one ([update]).
 */
class PauseOverlay {
    var isOpen = false
        private set

    /** The page the pager is sliding to, counted from the first page and going on past the last: 3 is the first page again. */
    private var targetIndex = 0

    /** The page the pager rests on, or is sliding to. */
    val page: PausePage get() = pageAt(targetIndex)

    /** Where the pager is, in pages, counted like [targetIndex]: 1.5 is half way between the second and the third page. */
    var position = 0f
        private set

    /** How far a finger has dragged the pages, in pages: to the right is positive, and shows the page before. */
    private var drag = 0f

    /** Where the pages are on the screen right now, in pages, counted like [targetIndex]. */
    val offset: Float get() = position - drag

    /** The page that is nearest to the middle of the screen. */
    val visiblePage: PausePage get() = pageAt(offset.roundToInt())

    /**
     * How many pages the [page] is from the middle of the screen, to the right if positive: because the pager wraps around, it is
     * the shorter way round, so there is always a page on each side.
     */
    fun pagesAway(page: PausePage): Float {
        val distance = page.ordinal - offset
        return distance - count * floor(distance / count + 0.5f)
    }

    fun open(page: PausePage) {
        isOpen = true
        targetIndex = page.ordinal
        position = targetIndex.toFloat()
        drag = 0f
    }

    fun close() {
        isOpen = false
        drag = 0f
    }

    /** Slides to [page] the shorter way round, or jumps there if not [animate]. */
    fun goTo(page: PausePage, animate: Boolean = true) {
        drag = 0f
        var delta = page.ordinal - this.page.ordinal
        if (delta > count / 2) delta -= count
        if (delta < -count / 2) delta += count
        targetIndex += delta
        if (!animate) position = targetIndex.toFloat()
    }

    /** The finger moved by [pages] pages (a fraction of the screen width). The pages follow it, but not by more than a page. */
    fun dragBy(pages: Float) {
        drag = (drag + pages).coerceIn(-1f, 1f)
    }

    /** The finger let go: the pages go on to the next (or the one before) if they were dragged far enough, else back. */
    fun release() {
        val visual = offset
        targetIndex += when {
            drag > dragToTurn -> -1
            drag < -dragToTurn -> 1
            else -> 0
        }
        position = visual
        drag = 0f
    }

    /** Slides the pages towards the page they rest on. */
    fun update(dt: Float) {
        if (!isOpen) return
        val distance = targetIndex - position
        position = if (abs(distance) < 0.002f) targetIndex.toFloat() else position + distance * (dt * slideSpeed).coerceAtMost(1f)
        if (position == targetIndex.toFloat() && drag == 0f) keepNumbersSmall()
    }

    /** Once the pages rest, the counting starts again from the first page, so that the numbers never grow. */
    private fun keepNumbersSmall() {
        val shift = targetIndex.floorDiv(count) * count
        targetIndex -= shift
        position -= shift
    }

    private fun pageAt(index: Int): PausePage = PausePage.entries[index.mod(count)]

    private companion object {
        val count = PausePage.entries.size

        /** A drag of this fraction of a page turns the page. */
        const val dragToTurn = 0.18f

        /** How fast the pages slide: the part of the way left that is covered per second. */
        const val slideSpeed = 14f
    }
}
