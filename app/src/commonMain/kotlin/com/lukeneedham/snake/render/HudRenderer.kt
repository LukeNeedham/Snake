package com.lukeneedham.snake.render

import androidx.compose.ui.graphics.drawscope.DrawScope
import com.lukeneedham.snake.game.GamePhase
import com.lukeneedham.snake.game.GameWorld

/**
 * Draws what goes over the scene in the current phase of the game, then the menu button, or the menu itself if it is open.
 * [topInset] is the height of the status bar, in art pixels.
 */
internal fun DrawScope.drawHud(world: GameWorld, texts: SceneTexts, topInset: Int) {
    when (world.phase) {
        GamePhase.Ready -> drawTitle(world, texts, topInset)
        GamePhase.Intro -> drawIntroHud(world, texts)
        GamePhase.Playing -> drawPlayingHud(world, texts, topInset)
        GamePhase.Paused -> if (world.isIntro) drawIntroHud(world, texts) else drawPlayingHud(world, texts, topInset)
        GamePhase.GameOver -> drawGameOver(world, texts)
    }
    if (world.isMenuOpen) drawMenu(world, texts) else drawMenuButton(world, topInset)
}
