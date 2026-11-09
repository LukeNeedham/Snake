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
    val text = texts.effectNames[kind.effect.ordinal]
    val textWidth = PixelFont.width(text, scale = 2)
    val left = world.arena.width.toInt() / 2 - (ArrowIcon.width + arrowGap + textWidth) / 2
    val y = world.arena.height.toInt() / 4 - rise
    drawArrow(left, y + 1, isUp = false, color = kind.light.copy(alpha = fade), outline = kind.shade.copy(alpha = fade))
    drawPixelText(text, left + ArrowIcon.width + arrowGap, y, kind.light.copy(alpha = fade), scale = 2, outline = kind.shade.copy(alpha = fade))
}

/** The space between the arrow and the name. */
private const val arrowGap = 3

