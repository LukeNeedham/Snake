package com.lukeneedham.snake.render

import androidx.compose.ui.graphics.drawscope.DrawScope
import com.lukeneedham.snake.game.GameWorld
import kotlin.math.roundToInt

/** The name of the power up that was just eaten rises up over the arena, under the banner of a toad's effect, in the colours of its bird, and fades. */
internal fun DrawScope.drawPowerUpBanner(world: GameWorld, texts: SceneTexts) {
    val kind = world.powerUps.bannerKind ?: return
    val time = world.powerUps.bannerTime
    if (time <= 0f) return
    val palette = birdPalette(kind)
    val fade = (time / 0.5f).coerceIn(0f, 1f)
    val rise = ((2f - time) * 4f).coerceAtMost(8f).roundToInt()
    drawCenteredPixelText(
        text = texts.powerUpNames[kind.powerUp.ordinal],
        centerX = world.arena.width.toInt() / 2,
        y = world.arena.height.toInt() / 4 + 14 - rise,
        color = palette.barLight.copy(alpha = fade),
        scale = 2,
        outline = PixelFont.outline.copy(alpha = fade),
    )
}
