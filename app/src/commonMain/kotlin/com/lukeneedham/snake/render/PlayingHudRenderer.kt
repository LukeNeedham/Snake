package com.lukeneedham.snake.render

import androidx.compose.ui.graphics.drawscope.DrawScope
import com.lukeneedham.snake.game.GameWorld

/** What is on the screen while playing: the score, the hunger bar, the sprint button, the bars of the effects that are on, and the banner of the last one. */
internal fun DrawScope.drawPlayingHud(world: GameWorld, texts: SceneTexts, topInset: Int) {
    drawCenteredPixelText(world.score.toString(), world.arena.width.toInt() / 2, topInset + 3, HudPalette.white, scale = 2)
    drawHungerBar(world, topInset)
    drawEffectBars(world)
    drawSprintButton(world)
    drawEffectBanner(world, texts)
}
