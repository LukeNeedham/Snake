package com.lukeneedham.snake.render

import androidx.compose.ui.graphics.drawscope.DrawScope
import com.lukeneedham.snake.game.GameWorld
import kotlin.math.roundToInt

/** The name of the power up that was just eaten rises up over the arena, under the banner of a toad's power down, in the colours of its bird, and fades. */
internal fun DrawScope.drawPowerUpBanner(world: GameWorld, texts: SceneTexts) {
    val kind = world.powerUps.bannerKind ?: return
    val time = world.powerUps.bannerTime
    if (time <= 0f) return
    val palette = birdPalette(kind)
    val fade = (time / 0.5f).coerceIn(0f, 1f)
    val rise = ((2f - time) * 4f).coerceAtMost(8f).roundToInt()
    val text = texts.powerUpNames[kind.powerUp.ordinal]
    val textWidth = PixelFont.width(text, scale = 2)
    val left = world.arena.width.toInt() / 2 - (ArrowIcon.width + arrowGap + textWidth) / 2
    val y = world.arena.height.toInt() / 4 + 14 - rise
    drawArrow(left, y + 1, isUp = true, color = palette.barLight.copy(alpha = fade), outline = PixelFont.outline.copy(alpha = fade))
    drawPixelText(text, left + ArrowIcon.width + arrowGap, y, palette.barLight.copy(alpha = fade), scale = 2, outline = PixelFont.outline.copy(alpha = fade))
}

/** The space between the arrow and the name. */
private const val arrowGap = 3

