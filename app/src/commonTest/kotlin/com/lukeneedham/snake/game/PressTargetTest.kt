package com.lukeneedham.snake.game

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class PressTargetTest {

    private val world = GameWorld(Arena(108f, 234f), seed = 3)
    private val topInset = 8

    private fun target(x: Float, y: Float) = PressTarget.at(world, topInset, x, y)

    @Test
    fun theMiddleOfTheSceneTurnsTheSnake() {
        assertEquals(PressTarget.Turn, target(54f, 117f))
    }

    @Test
    fun theSprintCornerIsTheLeftOneByDefault() {
        assertEquals(PressTarget.Sprint, target(10f, 220f))
        assertEquals(PressTarget.Turn, target(100f, 220f))
    }

    @Test
    fun theSprintCornerFollowsTheSetting() {
        world.sprintSide = SprintSide.Right
        assertEquals(PressTarget.Sprint, target(100f, 220f))
        assertEquals(PressTarget.Turn, target(10f, 220f))
    }

    @Test
    fun anywhereInTheTopLeftAreaIsTheMenuButton() {
        val area = MenuLayout.menuArea(topInset)
        assertEquals(PressTarget.MenuButton, target(area.center.x, area.center.y))
        assertEquals(PressTarget.MenuButton, target(1f, 1f))
        assertEquals(PressTarget.MenuButton, target(area.right - 1f, area.bottom - 1f))
        val indicator = MenuLayout.menuIndicator(topInset)
        assertEquals(PressTarget.MenuButton, target(indicator.center.x, indicator.center.y))
        assertEquals(PressTarget.Turn, target(area.right + 5f, 1f))
        assertEquals(PressTarget.Turn, target(1f, area.bottom + 5f))
    }

    @Test
    fun withTheMenuOpenOnlyItsButtonsAreTargets() {
        world.start()
        world.pause()
        val items = world.menuItems
        items.forEachIndexed { index, item ->
            val rect = MenuLayout.item(world.arena, items.size, index)
            assertEquals(PressTarget.Item(item), target(rect.center.x, rect.center.y))
        }
        assertEquals(PressTarget.Backdrop, target(10f, 220f))
        val area = MenuLayout.menuArea(topInset)
        assertEquals(PressTarget.Backdrop, target(area.center.x, area.center.y))
    }

    @Test
    fun theMenuButtonsDoNotOverlapAndFitOnTheScreen() {
        val arena = Arena(108f, 234f)
        for (count in 2..3) {
            val rects = (0 until count).map { MenuLayout.item(arena, count, it) }
            rects.zipWithNext { upper, lower -> assertEquals(true, upper.bottom < lower.top) }
            assertEquals(true, rects.first().top > 20f && rects.last().bottom < arena.height)
            assertEquals(true, rects.all { it.left >= 0f && it.right <= arena.width })
        }
    }

    @Test
    fun theColumnOfArrowsInTheBottomRightCornerOpensTheStatusList() {
        world.start()
        assertEquals(PressTarget.Turn, target(100f, 224f))
        world.powerUps.apply(BirdKind.Crow)
        assertEquals(PressTarget.StatusCorner, target(100f, 224f))
        assertEquals(PressTarget.Turn, target(54f, 224f))
    }

    @Test
    fun theStomachInTheTopRightCornerOpensTheHungerPage() {
        world.start()
        assertEquals(PressTarget.Stomach, target(100f, 12f))
        assertEquals(PressTarget.Turn, target(54f, 12f))
    }

    @Test
    fun openingAPageOfTheOverlayPausesTheGameAndTheCloseButtonCarriesOn() {
        world.start()
        world.openOverlay(PausePage.Hunger)
        assertEquals(GamePhase.Paused, world.phase)
        assertEquals(PausePage.Hunger, world.overlay.page)
        val close = MenuLayout.closeButton(topInset)
        assertEquals(PressTarget.CloseButton, target(close.center.x, close.center.y))
        assertEquals(PressTarget.Backdrop, target(54f, 117f))
        world.closeMenu()
        assertEquals(GamePhase.Playing, world.phase)
        assertFalse(world.isMenuOpen)
    }

    @Test
    fun theButtonsAreOnlyOnTheFirstPage() {
        world.start()
        world.openOverlay(PausePage.Status)
        val first = MenuLayout.item(world.arena, world.menuItems.size, 0)
        assertEquals(PressTarget.Backdrop, target(first.center.x, first.center.y))
        world.overlay.goTo(PausePage.Pause, animate = false)
        assertEquals(PressTarget.Item(MenuItem.Resume), target(first.center.x, first.center.y))
    }

    @Test
    fun theDotsOfThePageIndicatorSelectPages() {
        world.start()
        world.pause()
        val dot = MenuLayout.pageDot(world.arena, PausePage.Status.ordinal)
        assertEquals(PressTarget.PageDot(PausePage.Status), target(dot.center.x, dot.center.y))
    }
}
