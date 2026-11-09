package com.lukeneedham.snake.game

import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PauseOverlayTest {

    private fun opened(page: PausePage = PausePage.Pause) = PauseOverlay().apply { open(page) }

    private fun PauseOverlay.settle() = repeat(240) { update(1f / 60f) }

    @Test
    fun itOpensOnThePageItIsGiven() {
        val overlay = opened(PausePage.Hunger)
        assertTrue(overlay.isOpen)
        assertEquals(PausePage.Hunger, overlay.visiblePage)
        assertEquals(0f, overlay.pagesAway(PausePage.Hunger))
        overlay.close()
        assertFalse(overlay.isOpen)
    }

    @Test
    fun aLongDragToTheLeftTurnsToTheNextPage() {
        val overlay = opened()
        overlay.dragBy(-0.3f)
        overlay.release()
        overlay.settle()
        assertEquals(PausePage.Hunger, overlay.page)
        assertEquals(0f, overlay.pagesAway(PausePage.Hunger))
    }

    @Test
    fun aShortDragGoesBackToTheSamePage() {
        val overlay = opened(PausePage.Hunger)
        overlay.dragBy(0.1f)
        overlay.release()
        overlay.settle()
        assertEquals(PausePage.Hunger, overlay.page)
        assertEquals(0f, overlay.pagesAway(PausePage.Hunger))
    }

    @Test
    fun theLastPageIsFollowedByTheFirst() {
        val overlay = opened(PausePage.Status)
        overlay.dragBy(-0.3f)
        overlay.release()
        overlay.settle()
        assertEquals(PausePage.Pause, overlay.page)
        assertEquals(0f, overlay.pagesAway(PausePage.Pause))
    }

    @Test
    fun theFirstPageIsPrecededByTheLast() {
        val overlay = opened()
        overlay.dragBy(0.3f)
        overlay.release()
        overlay.settle()
        assertEquals(PausePage.Status, overlay.page)
    }

    @Test
    fun thereIsAlwaysAPageOnEachSideOfTheMiddle() {
        val overlay = opened(PausePage.Status)
        assertEquals(1f, overlay.pagesAway(PausePage.Pause))
        assertEquals(-1f, overlay.pagesAway(PausePage.Hunger))
        overlay.dragBy(-0.5f)
        assertEquals(0.5f, overlay.pagesAway(PausePage.Pause))
    }

    @Test
    fun aDragCannotGoFurtherThanAPage() {
        val overlay = opened()
        overlay.dragBy(-3f)
        assertEquals(-1f, overlay.pagesAway(PausePage.Pause))
        overlay.release()
        overlay.settle()
        assertEquals(PausePage.Hunger, overlay.page)
    }

    @Test
    fun goingToAPageSlidesThereTheShorterWayRound() {
        val overlay = opened()
        overlay.goTo(PausePage.Status)
        assertTrue(abs(overlay.pagesAway(PausePage.Status)) > 0f)
        overlay.settle()
        assertEquals(PausePage.Status, overlay.visiblePage)
        assertEquals(0f, overlay.pagesAway(PausePage.Status))
    }
}
