package com.lukeneedham.snake.game

import kotlin.test.Test
import kotlin.test.assertEquals

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
    fun theCornerAtTheTopLeftIsTheMenuButton() {
        val button = MenuLayout.menuButton(topInset)
        assertEquals(PressTarget.MenuButton, target(button.center.x, button.center.y))
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
        val button = MenuLayout.menuButton(topInset)
        assertEquals(PressTarget.Backdrop, target(button.center.x, button.center.y))
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
    fun openingTheStatusListPausesTheGameAndClosingItCarriesOn() {
        world.start()
        world.openStatus()
        assertEquals(GamePhase.Playing, world.phase)
        world.powerUps.apply(BirdKind.Crow)
        world.openStatus()
        assertEquals(GamePhase.Paused, world.phase)
        assertEquals(PressTarget.Backdrop, target(54f, 117f))
        world.closeStatus()
        assertEquals(GamePhase.Playing, world.phase)
    }
}
