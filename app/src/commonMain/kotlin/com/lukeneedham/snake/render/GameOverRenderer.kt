package com.lukeneedham.snake.render

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.lukeneedham.snake.game.GameWorld
import com.lukeneedham.snake.game.MenuLayout
import kotlin.math.roundToInt
import kotlin.math.sin

/** The game over screen: the scene fades down, the result comes up, and then a blinking line says to tap to retry. */
internal fun DrawScope.drawGameOver(world: GameWorld, texts: SceneTexts) {
    val width = world.arena.width.toInt()
    val height = world.arena.height.toInt()
    val centerX = width / 2
    val dim = (world.gameOverTime * 2f).coerceIn(0f, 1f) * 0.55f
    pixelRect(0, 0, width, height, Color.Black.copy(alpha = dim))
    if (world.isResultShown) {
        val top = height / 2 - 24
        val cause = world.deathCause
        if (cause != null) drawCenteredPixelText(texts.deathCauseNames[cause.ordinal], centerX, top, HudPalette.red, scale = 2)
        drawCenteredPixelText(texts.score, centerX, top + 16, HudPalette.white, scale = 2)
        drawCenteredPixelText(texts.best, centerX, top + 32, HudPalette.ice)
        val settings = MenuLayout.gameOverSettingsButton(world.arena)
        drawPixelButton(settings)
        drawCenteredPixelText(texts.menuSettings, (settings.left + settings.width / 2f).roundToInt(), settings.top.roundToInt() + 5, HudPalette.white)
        if (texts.newBest.isNotEmpty()) {
            val bounce = (sin(world.time * 8f) * 1.5f).roundToInt()
            drawCenteredPixelText(texts.newBest, centerX, top + 42 + bounce, HudPalette.gold)
        }
    }
    if (world.canRestart && (world.time * 1.6f).toInt() % 2 == 0) {
        drawCenteredPixelText(texts.tapToRetry, centerX, height - 30, HudPalette.white)
    }
}

