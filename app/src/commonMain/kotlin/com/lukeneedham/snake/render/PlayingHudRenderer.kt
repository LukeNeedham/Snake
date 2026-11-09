package com.lukeneedham.snake.render

import androidx.compose.ui.graphics.drawscope.DrawScope
import com.lukeneedham.snake.game.GameWorld

/** What is on the screen while playing: the score, the hunger bar, the sprint button, the arrows of the effects and power ups that are on, and the banners of the last one of each. */
internal fun DrawScope.drawPlayingHud(world: GameWorld, texts: SceneTexts, topInset: Int) {
    drawCenteredPixelText(world.score.toString(), world.arena.width.toInt() / 2, topInset + 3, HudPalette.white, scale = 2)
    drawHungerBar(world, topInset)
    drawEffectBars(world)
    drawSprintButton(world)
    drawEffectBanner(world, texts)
    drawPowerUpBanner(world, texts)
}
