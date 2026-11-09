package com.lukeneedham.snake.render

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.translate
import com.lukeneedham.snake.game.GamePhase
import com.lukeneedham.snake.game.GameWorld
import com.lukeneedham.snake.game.MenuLayout
import com.lukeneedham.snake.game.PausePage
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * The pause overlay over the scene: it is dimmed, and its pages sit side by side in a horizontal pager, sliding as a finger drags them.
 * The pager wraps around. The close button in the top left corner and the page indicator at the bottom stay where they are.
 */
internal fun DrawScope.drawPauseOverlay(world: GameWorld, texts: SceneTexts, topInset: Int) {
    val width = world.arena.width.toInt()
    val height = world.arena.height.toInt()
    pixelRect(0, 0, width, height, Color(0xCC000000))
    val overlay = world.overlay
    for (page in PausePage.entries) {
        val pagesAway = overlay.pagesAway(page)
        if (abs(pagesAway) >= 1f) continue
        translate(left = (pagesAway * width).roundToInt().toFloat()) {
            when (page) {
                PausePage.Pause -> drawPausePage(world, texts, topInset)
                PausePage.Hunger -> drawHungerPage(world, texts, topInset)
                PausePage.Status -> drawStatusPage(world, texts, topInset)
            }
        }
    }
    drawPageTitles(world, texts, topInset)
    drawCloseButton(topInset)
    drawPageIndicator(world)
}

/**
 * The titles slide with their pages, but not past the left edge of the close button: they go in behind it, which is drawn over
 * them, and stay hidden there. They come out from behind it on the other side when a page slides back in.
 */
private fun DrawScope.drawPageTitles(world: GameWorld, texts: SceneTexts, topInset: Int) {
    val width = world.arena.width.toInt()
    clipRect(left = MenuLayout.closeButton(topInset).left) {
        for (page in PausePage.entries) {
            val pagesAway = world.overlay.pagesAway(page)
            if (abs(pagesAway) >= 1f) continue
            val title = when (page) {
                PausePage.Pause -> if (world.phase == GamePhase.Paused) texts.paused else texts.menuTitle
                PausePage.Hunger -> texts.hungerTitle
                PausePage.Status -> texts.statusTitle
            }
            translate(left = (pagesAway * width).roundToInt().toFloat()) {
                drawCenteredPixelText(title, width / 2, topInset + PageTitle.top, HudPalette.gold, scale = 2)
            }
        }
    }
}

/** The button in the top left corner that closes the overlay and resumes the game: a cross. */
private fun DrawScope.drawCloseButton(topInset: Int) {
    val rect = MenuLayout.closeButton(topInset)
    drawPixelButton(rect)
    val left = rect.left.roundToInt() + 3
    val top = rect.top.roundToInt() + 3
    for (i in 0 until 6) {
        pixel(left + i, top + i, HudPalette.white)
        pixel(left + 5 - i, top + i, HudPalette.white)
    }
}

/** One small diamond per page at the bottom of the screen, the page it is on lit. */
private fun DrawScope.drawPageIndicator(world: GameWorld) {
    for (page in PausePage.entries) {
        val rect = MenuLayout.pageDot(world.arena, page.ordinal)
        val left = rect.left.roundToInt()
        val top = rect.top.roundToInt()
        val color = if (page == world.overlay.visiblePage) HudPalette.white else HudPalette.barBack
        pixelRect(left, top - 1, 5, 7, PixelFont.outline)
        pixelRect(left - 1, top, 7, 5, PixelFont.outline)
        pixelRect(left + 1, top, 3, 5, color)
        pixelRect(left, top + 1, 5, 3, color)
    }
}
