package com.lukeneedham.snake.render

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.lukeneedham.snake.game.GamePhase
import com.lukeneedham.snake.game.GameWorld
import com.lukeneedham.snake.game.MenuItem
import com.lukeneedham.snake.game.MenuLayout
import kotlin.math.roundToInt

/** The menu over the scene: it is dimmed, and the buttons are stacked in the middle of the screen. */
internal fun DrawScope.drawMenu(world: GameWorld, texts: SceneTexts) {
    val width = world.arena.width.toInt()
    val height = world.arena.height.toInt()
    pixelRect(0, 0, width, height, Color(0xAA000000))
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
    if (world.phase == GamePhase.Paused) {
        val top = MenuLayout.item(world.arena, items.size, 0).top.roundToInt()
        drawCenteredPixelText(texts.paused, width / 2, top - 18, HudPalette.gold, scale = 2)
    }
}
