package com.lukeneedham.snake.render

import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.IntOffset
import com.lukeneedham.snake.game.GameWorld
import kotlin.math.roundToInt

/**
 * Draws the whole scene at one unit per art pixel, back to front. The caller draws it into a small bitmap and scales that up. The
 * background is the whole map: the world is drawn moved by the camera, the HUD is not.
 */
internal fun DrawScope.drawScene(
    world: GameWorld,
    background: ImageBitmap,
    frontGrass: List<GrassBlade>,
    texts: SceneTexts,
    topInset: Int,
) {
    translate(left = -world.cameraX.roundToInt().toFloat(), top = -world.cameraY.roundToInt().toFloat()) {
        drawImage(background, dstOffset = IntOffset(world.layout.worldLeft.roundToInt(), 0), filterQuality = FilterQuality.None)
        drawGateStones(world)
        drawButterflies(world)
        drawCritterShadows(world)
        drawCritters(world)
        drawSnake(world)
        drawParticles(world)
        drawFrontGrass(frontGrass, world)
        drawFlyingBirds(world)
        drawIntroOverlay(world)
    }
    drawHud(world, texts, topInset)
}
