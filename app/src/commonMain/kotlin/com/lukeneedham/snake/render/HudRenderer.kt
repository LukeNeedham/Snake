package com.lukeneedham.snake.render

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.lukeneedham.snake.game.GamePhase
import com.lukeneedham.snake.game.GameWorld

/** Draws what goes over the scene in the current phase of the game. [topInset] is the height of the status bar, in art pixels. */
internal fun DrawScope.drawHud(world: GameWorld, texts: SceneTexts, topInset: Int) {
    when (world.phase) {
        GamePhase.Ready -> drawTitle(world, texts, topInset)
        GamePhase.Playing -> drawPlayingHud(world, texts, topInset)
        GamePhase.Paused -> {
            drawPlayingHud(world, texts, topInset)
            drawPaused(world, texts)
        }
        GamePhase.GameOver -> drawGameOver(world, texts)
    }
}

private fun DrawScope.drawPaused(world: GameWorld, texts: SceneTexts) {
    val width = world.arena.width.toInt()
    val height = world.arena.height.toInt()
    pixelRect(0, 0, width, height, Color(0x88000000))
    drawCenteredPixelText(texts.paused, width / 2, height / 2 - 6, HudPalette.gold, scale = 2)
    drawCenteredPixelText(texts.tapToResume, width / 2, height / 2 + 8, HudPalette.white)
}
