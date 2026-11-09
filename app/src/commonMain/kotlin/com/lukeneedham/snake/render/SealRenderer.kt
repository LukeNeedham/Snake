package com.lukeneedham.snake.render

import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.lukeneedham.snake.game.GameWorld
import kotlin.math.roundToInt

/**
 * What the way in to the stage looks like once it is sealed with grass: the [bitmap] of the map there with the path cut off, whose top left
 * corner is at [left], [top] in the world, and the [blades] of the front grass that grow along the edge of the oval across it.
 */
internal class SealPatch(val bitmap: ImageBitmap, val left: Int, val top: Int, val blades: List<GrassBlade>)

/** How many columns of the patch the grass has grown in from each side, by now: it moves in from both sides until it meets in the middle. */
private fun GameWorld.sealedColumns(patch: SealPatch): Int = (patch.bitmap.width / 2f * sealProgress).roundToInt()

/** The patch of the sealed way in, over the ground, as far as the grass has grown: from the left and the right, towards the middle of the path. */
internal fun DrawScope.drawSealPatch(world: GameWorld, patch: SealPatch) {
    val covered = world.sealedColumns(patch)
    if (covered <= 0) return
    val width = patch.bitmap.width
    val height = patch.bitmap.height
    drawImage(
        image = patch.bitmap,
        srcOffset = IntOffset(0, 0),
        srcSize = IntSize(covered, height),
        dstOffset = IntOffset(patch.left, patch.top),
        dstSize = IntSize(covered, height),
        filterQuality = FilterQuality.None,
    )
    drawImage(
        image = patch.bitmap,
        srcOffset = IntOffset(width - covered, 0),
        srcSize = IntSize(covered, height),
        dstOffset = IntOffset(patch.left + width - covered, patch.top),
        dstSize = IntSize(covered, height),
        filterQuality = FilterQuality.None,
    )
}

/** The blades of the front grass along the sealed way in, over the animals, where the grass has grown. */
internal fun DrawScope.drawSealBlades(world: GameWorld, patch: SealPatch) {
    val covered = world.sealedColumns(patch)
    if (covered <= 0) return
    val width = patch.bitmap.width
    val hidden = world.critters.filter { world.layout.ground.signedDistance(it.x, it.y) > -1f }
    for (blade in patch.blades) {
        val column = blade.x - patch.left
        if (column in covered until width - covered) continue
        drawFrontBlade(blade, world, hidden)
    }
}
