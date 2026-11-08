package com.lukeneedham.snake.render

import androidx.compose.ui.graphics.drawscope.DrawScope
import com.lukeneedham.snake.game.GameWorld

/** What is on the screen while playing: the score beside the menu button in the top left corner, the hunger stomach in the top right corner, the sprint button, the bars of the effects and power ups that are on, and the banners of the last one of each. */
internal fun DrawScope.drawPlayingHud(world: GameWorld, texts: SceneTexts, topInset: Int) {
    // Beside the menu button, which is in the same corner
    drawPixelText(world.score.toString(), 21, topInset + 5, HudPalette.white, scale = 2)
    drawStomach(world, topInset)
    drawEffectBars(world)
    drawSprintButton(world)
    drawEffectBanner(world, texts)
    drawPowerUpBanner(world, texts)
}
