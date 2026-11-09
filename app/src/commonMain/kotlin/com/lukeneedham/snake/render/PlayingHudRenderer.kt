package com.lukeneedham.snake.render

import androidx.compose.ui.graphics.drawscope.DrawScope
import com.lukeneedham.snake.game.GameWorld

/** What is on the screen while playing: the score in the top left corner with the tiny pause indicator under it, the hunger stomach in the top right corner, the sprint button, the arrows of the effects and power ups that are on, and the banners of the last one of each. */
internal fun DrawScope.drawPlayingHud(world: GameWorld, texts: SceneTexts, topInset: Int) {
    // In the top left corner, with the tiny pause indicator under it
    drawPixelText(world.score.toString(), 4, topInset + 3, HudPalette.white, scale = 2)
    drawStomach(world, topInset)
    drawEffectBars(world)
    drawSprintButton(world)
    drawEffectBanner(world, texts)
    drawPowerUpBanner(world, texts)
}
