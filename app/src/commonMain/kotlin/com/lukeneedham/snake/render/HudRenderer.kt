package com.lukeneedham.snake.render

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.lukeneedham.snake.game.Effect
import com.lukeneedham.snake.game.GamePhase
import com.lukeneedham.snake.game.GameWorld
import com.lukeneedham.snake.game.ToadKind
import kotlin.math.roundToInt
import kotlin.math.sin

private val Gold = Color(0xFFFFE066)
private val White = Color(0xFFFFFFFF)

internal fun DrawScope.drawHud(world: GameWorld, texts: SceneTexts, topInset: Int) {
    val width = world.arena.width.toInt()
    val height = world.arena.height.toInt()
    val centerX = width / 2
    when (world.phase) {
        GamePhase.READY -> {
            val bounce = (sin(world.time * 3f) * 1.5f).roundToInt()
            drawCenteredPixelText(texts.title, centerX, topInset + 18 + bounce, Gold, scale = 3)
            if ((world.time * 1.6f).toInt() % 2 == 0) {
                drawCenteredPixelText(texts.tapToStart, centerX, height - 30, White, scale = 1)
            }
        }
        GamePhase.PLAYING -> drawPlayingHud(world, texts, topInset)
        GamePhase.PAUSED -> {
            drawPlayingHud(world, texts, topInset)
            pixelRect(0, 0, width, height, Color(0x88000000))
            drawCenteredPixelText(texts.paused, centerX, height / 2 - 6, Gold, scale = 2)
            drawCenteredPixelText(texts.tapToResume, centerX, height / 2 + 8, White)
        }
        GamePhase.GAME_OVER -> {
            val dim = (world.gameOverTime * 2f).coerceIn(0f, 1f) * 0.55f
            pixelRect(0, 0, width, height, Color.Black.copy(alpha = dim))
            if (world.gameOverTime > 0.35f) {
                val top = height / 2 - 24
                drawCenteredPixelText(texts.gameOver, centerX, top, Color(0xFFFF6B7D), scale = 2)
                drawCenteredPixelText(texts.score, centerX, top + 16, White, scale = 2)
                drawCenteredPixelText(texts.best, centerX, top + 32, Color(0xFFBFE3FF))
                if (texts.newBest.isNotEmpty()) {
                    val bounce = (sin(world.time * 8f) * 1.5f).roundToInt()
                    drawCenteredPixelText(texts.newBest, centerX, top + 42 + bounce, Gold)
                }
            }
            if (world.canRestart && (world.time * 1.6f).toInt() % 2 == 0) {
                drawCenteredPixelText(texts.tapToRetry, centerX, height - 30, White)
            }
        }
    }
}

private fun DrawScope.drawPlayingHud(world: GameWorld, texts: SceneTexts, topInset: Int) {
    val width = world.arena.width.toInt()
    val height = world.arena.height.toInt()
    drawCenteredPixelText(world.score.toString(), width / 2, topInset + 3, White, scale = 2)

    var row = 0
    for (effect in Effect.entries) {
        val remaining = world.effectRemaining(effect)
        if (remaining <= 0f) continue
        val kind = ToadKind.entries.first { it.effect == effect }
        val y = height - 9 - row * 8
        pixelRect(width / 2 - 26, y - 1, 7, 7, Color(0xFF1B1422))
        pixelRect(width / 2 - 25, y, 5, 5, kind.body)
        pixel(width / 2 - 24, y + 1, kind.light)
        pixelRect(width / 2 - 17, y, 42, 5, Color(0xFF1B1422))
        pixelRect(width / 2 - 16, y + 1, 40, 3, Color(0xFF3A3340))
        val fill = (40f * remaining / effect.durationSeconds).roundToInt()
        pixelRect(width / 2 - 16, y + 1, fill, 3, kind.body)
        row++
    }

    val banner = world.bannerKind
    if (banner != null && world.bannerTime > 0f) {
        val fade = (world.bannerTime / 0.5f).coerceIn(0f, 1f)
        val rise = ((2f - world.bannerTime) * 4f).coerceAtMost(8f).roundToInt()
        drawCenteredPixelText(
            text = texts.effectNames[banner.effect.ordinal],
            centerX = width / 2,
            y = height / 4 - rise,
            color = banner.light.copy(alpha = fade),
            scale = 2,
            outline = banner.shade.copy(alpha = fade),
        )
    }
}
