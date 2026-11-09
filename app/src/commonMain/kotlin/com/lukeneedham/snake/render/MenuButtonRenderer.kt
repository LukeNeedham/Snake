package com.lukeneedham.snake.render

import androidx.compose.ui.graphics.drawscope.DrawScope
import com.lukeneedham.snake.game.GamePhase
import com.lukeneedham.snake.game.GameWorld
import com.lukeneedham.snake.game.MenuLayout
import kotlin.math.roundToInt

/** The small button in the top left corner, under the score, that opens the menu: two bars (pause) while playing, three lines (menu) on the other screens. */
internal fun DrawScope.drawMenuButton(world: GameWorld, topInset: Int) {
    val rect = MenuLayout.menuButton(topInset)
    drawPixelButton(rect)
    val left = rect.left.roundToInt()
    val top = rect.top.roundToInt()
    val centerX = left + rect.width.roundToInt() / 2
    val centerY = top + rect.height.roundToInt() / 2
    if (world.phase == GamePhase.Playing) {
        pixelRect(centerX - 3, centerY - 2, 2, 5, HudPalette.white)
        pixelRect(centerX + 1, centerY - 2, 2, 5, HudPalette.white)
    } else {
        for (row in -2..2 step 2) pixelRect(centerX - 2, centerY + row, 5, 1, HudPalette.white)
    }
}
