package com.lukeneedham.snake.render

import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.lukeneedham.snake.game.PowerDown
import com.lukeneedham.snake.game.GameWorld
import androidx.compose.ui.graphics.Color
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Scales the small [scene] up to the whole canvas without smoothing, so that its pixels stay square. While the snake
 * hallucinates the scene is drawn row by row with each row pushed sideways, which makes everything wavy.
 */
internal fun DrawScope.presentScene(scene: ImageBitmap, world: GameWorld) {
    val scale = size.width / scene.width
    val screenWidth = size.width.roundToInt()
    val screenHeight = size.height.roundToInt()
    val shakeX = (sin(world.time * 80f) * world.shake * scale).roundToInt()
    val shakeY = (sin(world.time * 63f + 1f) * world.shake * scale).roundToInt()
    val wave = world.powerDowns.level(PowerDown.Hallucinating)

    drawRect(GrassPalette.base)
    if (wave < 0.02f) {
        drawImage(
            image = scene,
            dstOffset = IntOffset(shakeX, shakeY),
            dstSize = IntSize(screenWidth, screenHeight),
            filterQuality = FilterQuality.None,
        )
        return
    }
    for (row in 0 until scene.height) {
        val offset = (sin(row * 0.22f + world.time * 5f) * 2.4f + sin(row * 0.07f - world.time * 3f) * 1.6f) * wave * scale
        val top = floor(row * scale).toInt()
        val bottom = ceil((row + 1) * scale).toInt()
        drawImage(
            image = scene,
            srcOffset = IntOffset(0, row),
            srcSize = IntSize(scene.width, 1),
            dstOffset = IntOffset(offset.roundToInt() + shakeX, top + shakeY),
            dstSize = IntSize(screenWidth, maxOf(1, minOf(bottom, screenHeight) - top)),
            filterQuality = FilterQuality.None,
        )
    }
    drawRect(Color.hsv((world.time * 70f) % 360f, 0.7f, 1f, 0.16f * wave))
}
