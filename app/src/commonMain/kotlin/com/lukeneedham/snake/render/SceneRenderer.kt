package com.lukeneedham.snake.render

import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.lukeneedham.snake.game.GameWorld

/** Draws the whole scene at one unit per art pixel, back to front. The caller draws it into a small bitmap and scales that up. */
internal fun DrawScope.drawScene(
    world: GameWorld,
    background: ImageBitmap,
    frontGrass: List<GrassBlade>,
    texts: SceneTexts,
    topInset: Int,
) {
    drawImage(background, filterQuality = FilterQuality.None)
    drawButterflies(world)
    drawCritterShadows(world)
    drawCritters(world)
    drawSnake(world)
    drawParticles(world)
    drawFrontGrass(frontGrass, world)
    drawHud(world, texts, topInset)
}
