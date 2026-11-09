package com.lukeneedham.snake.render

import androidx.compose.ui.graphics.drawscope.DrawScope
import com.lukeneedham.snake.game.GamePhase
import com.lukeneedham.snake.game.GameWorld
import com.lukeneedham.snake.game.MenuLayout
import kotlin.math.roundToInt

/**
 * The tiny indicator in the top left corner, under the score: two bars (pause) while playing or in the story at the start, three lines (menu) on the other
 * screens. It is not the button: a press anywhere in the top left area opens the menu.
 */
internal fun DrawScope.drawMenuButton(world: GameWorld, topInset: Int) {
    val rect = MenuLayout.menuIndicator(topInset)
    val left = rect.left.roundToInt()
    val top = rect.top.roundToInt()
    if (world.phase == GamePhase.Playing || world.phase == GamePhase.Intro) {
        for (x in intArrayOf(left, left + 3)) {
            pixelRect(x - 1, top - 1, 3, 7, PixelFont.outline)
        }
        for (x in intArrayOf(left, left + 3)) pixelRect(x, top, 1, 5, HudPalette.white)
    } else {
        for (row in 0..2) pixelRect(left - 1, top + row * 2 - 1, 7, 3, PixelFont.outline)
        for (row in 0..2) pixelRect(left, top + row * 2, 5, 1, HudPalette.white)
    }
}
