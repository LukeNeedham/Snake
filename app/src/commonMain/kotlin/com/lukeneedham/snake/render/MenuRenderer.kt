package com.lukeneedham.snake.render

import androidx.compose.ui.graphics.drawscope.DrawScope
import com.lukeneedham.snake.game.GamePhase
import com.lukeneedham.snake.game.GameWorld
import com.lukeneedham.snake.game.MenuItem
import com.lukeneedham.snake.game.MenuLayout
import kotlin.math.roundToInt

/** The first page of the pause overlay: the score and the best score under the title (which the overlay draws), and the buttons stacked in the middle of the screen. */
internal fun DrawScope.drawPausePage(world: GameWorld, texts: SceneTexts, topInset: Int) {
    val width = world.arena.width.toInt()
    // The score of this game under the title, and the best score under that, smaller
    drawCenteredPixelText(texts.score, width / 2, topInset + PageTitle.top + 16, HudPalette.white, scale = 2)
    drawCenteredPixelText(texts.best, width / 2, topInset + PageTitle.top + 30, HudPalette.white.copy(alpha = 0.7f))
    val items = world.menuItems
    items.forEachIndexed { index, item ->
        val rect = MenuLayout.item(world.arena, items.size, index)
        drawPixelButton(rect)
        val label = when (item) {
            MenuItem.Resume -> if (world.phase == GamePhase.Paused) texts.menuResume else texts.menuBack
            MenuItem.Restart -> texts.menuRestart
            MenuItem.Settings -> texts.menuSettings
        }
        val centerX = (rect.left + rect.width / 2f).roundToInt()
        drawCenteredPixelText(label, centerX, rect.top.roundToInt() + 5, HudPalette.white)
    }
}
