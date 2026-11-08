package com.lukeneedham.snake.render

import androidx.compose.ui.graphics.drawscope.DrawScope
import com.lukeneedham.snake.game.GameWorld

/** What is on the screen while playing: the score in the top right corner, the hunger bar by the menu button in the top left corner, the sprint button, the bars of the effects and power ups that are on, and the banners of the last one of each. */
internal fun DrawScope.drawPlayingHud(world: GameWorld, texts: SceneTexts, topInset: Int) {
    val score = world.score.toString()
    drawPixelText(score, world.arena.width.toInt() - PixelFont.width(score, 2) - 4, topInset + 4, HudPalette.white, scale = 2)
    drawHungerBar(world, topInset)
    drawEffectBars(world)
    drawSprintButton(world)
    drawEffectBanner(world, texts)
    drawPowerUpBanner(world, texts)
}
