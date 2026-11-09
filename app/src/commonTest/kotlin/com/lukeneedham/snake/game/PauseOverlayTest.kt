package com.lukeneedham.snake.game

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PauseOverlayTest {

    private fun opened(page: PausePage = PausePage.Pause) = PauseOverlay().apply { open(page) }

    @Test
    fun itOpensOnThePageItIsGiven() {
        val overlay = opened(PausePage.Hunger)
        assertTrue(overlay.isOpen)
        assertEquals(PausePage.Hunger, overlay.visiblePage)
        assertEquals(1f, overlay.offset)
        overlay.close()
        assertFalse(overlay.isOpen)
    }

    @Test
    fun aLongDragToTheLeftTurnsToTheNextPage() {
        val overlay = opened()
        overlay.dragBy(-0.3f)
        overlay.release()
        repeat(120) { overlay.update(1f / 60f) }
        assertEquals(PausePage.Hunger, overlay.page)
        assertEquals(1f, overlay.offset)
    }

    @Test
    fun aShortDragGoesBackToTheSamePage() {
        val overlay = opened(PausePage.Hunger)
        overlay.dragBy(0.1f)
        overlay.release()
        repeat(120) { overlay.update(1f / 60f) }
        assertEquals(PausePage.Hunger, overlay.page)
        assertEquals(1f, overlay.offset)
    }

    @Test
    fun thePagesCannotBeDraggedPastTheFirstAndLast() {
        val first = opened()
        first.dragBy(0.5f)
        assertEquals(0f, first.offset)
        val last = opened(PausePage.Status)
        last.dragBy(-0.5f)
        assertEquals(PausePage.Status.ordinal.toFloat(), last.offset)
        first.release()
        assertEquals(PausePage.Pause, first.page)
    }

    @Test
    fun goingToAPageSlidesThere() {
        val overlay = opened()
        overlay.goTo(PausePage.Status)
        assertTrue(overlay.offset < PausePage.Status.ordinal)
        repeat(240) { overlay.update(1f / 60f) }
        assertEquals(PausePage.Status.ordinal.toFloat(), overlay.offset)
    }
}
