package com.lukeneedham.snake.render

import androidx.compose.ui.graphics.drawscope.DrawScope
import com.lukeneedham.snake.game.GameWorld
import kotlin.math.roundToInt

/** The name of the effect that was just eaten rises up over the arena, in the colours of its toad, and fades. */
internal fun DrawScope.drawEffectBanner(world: GameWorld, texts: SceneTexts) {
    val kind = world.effects.bannerKind ?: return
    val time = world.effects.bannerTime
    if (time <= 0f) return
    val fade = (time / 0.5f).coerceIn(0f, 1f)
    val rise = ((2f - time) * 4f).coerceAtMost(8f).roundToInt()
    drawCenteredPixelText(
        text = texts.effectNames[kind.effect.ordinal],
        centerX = world.arena.width.toInt() / 2,
        y = world.arena.height.toInt() / 4 - rise,
        color = kind.light.copy(alpha = fade),
        scale = 2,
        outline = kind.shade.copy(alpha = fade),
    )
}
