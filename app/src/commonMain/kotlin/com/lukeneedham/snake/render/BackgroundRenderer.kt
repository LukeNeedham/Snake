package com.lukeneedham.snake.render

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import com.lukeneedham.snake.game.Bounds
import com.lukeneedham.snake.game.DenTuning
import com.lukeneedham.snake.game.WorldLayout
import com.lukeneedham.snake.game.WorldTuning
import kotlin.math.floor
import kotlin.math.roundToInt
import kotlin.random.Random

private val sandRim = Color(0xFF8F7648)
private val sandEdge = Color(0xFFB19659)
private val sand = Color(0xFFD2B87E)
private val sandLight = Color(0xFFDCC896)
private val sandDark = Color(0xFFC4A96F)

/** Raw stone: patches of lighter and darker grey, with thin cracks, and specks. */
private val stoneDark = Color(0xFF6B6870)
private val stoneMid = Color(0xFF7F7C85)
private val stoneLight = Color(0xFF908D97)
private val stoneHighlight = Color(0xFFA29FAA)
private val stoneCrack = Color(0xFF4F4C57)
private val stoneEdge = Color(0xFF4D4A54)

/**
 * Draws the static scene once into a bitmap: the sand of the stage and of the room of the tutorial, the paths, the stone floor of the
 * house, and the dense grass all round. The bitmap is the whole map, drawn in world coordinates: its left edge is the left edge of the world.
 */
internal fun createBackground(layout: WorldLayout, density: Density, layoutDirection: LayoutDirection): ImageBitmap {
    val width = layout.worldWidth.toInt()
    val height = layout.worldHeight.toInt()
    val bitmap = ImageBitmap(width, height)
    CanvasDrawScope().draw(density, layoutDirection, Canvas(bitmap), Size(width.toFloat(), height.toFloat())) {
        translate(left = -layout.worldLeft, top = 0f) {
            drawGround(layout, width, height)
            drawBackBlades(layout, width, height)
            val arena = layout.arena
            drawPebbles(arena, arena.centerX, arena.centerY, arena.radiusX, arena.radiusY, seed = 777)
            val room = layout.room
            drawPebbles(room, room.centerX, room.centerY, room.radiusX, room.radiusY, seed = 778)
            drawHouseRocks(layout)
            drawHousePebbles(layout)
        }
    }
    return bitmap
}

/**
 * The grass that grows across the way in to the stage, once the stage is sealed: a patch of the map where the path meets the oval, drawn as it
 * looks with the path cut off, so that the oval is a perfect oval (its sand, its rim, the grass round it, with the blades of the back grass).
 * The blades of the front grass, which are drawn over the animals, come with it.
 */
internal fun createSealPatch(layout: WorldLayout, density: Density, layoutDirection: LayoutDirection): SealPatch {
    val arena = layout.arena
    val halfWidth = (DenTuning.pathHalfWidth + sealMargin).toInt()
    val left = arena.centerX.toInt() - halfWidth
    val width = halfWidth * 2
    val top = (arena.height - sealAbove).toInt()
    val height = (arena.height + sealBelow).toInt() - top
    val terrain = layout.sealedTerrain
    val area = (width * height).toFloat() / (arena.width * arena.height)

    val random = Random(6161)
    val back = ArrayList<GrassBlade>()
    repeat((backBladeTrials * area).toInt()) {
        val x = left + random.nextInt(width)
        val y = top + random.nextInt(height)
        val bladeHeight = 6 + random.nextInt(7)
        val shade = random.nextInt(4)
        if (terrain.signedDistance(x + 0.5f, y + 0.5f) < -2f) return@repeat
        back.add(GrassBlade(x, y, bladeHeight, 0f, shade))
    }
    back.sortBy { it.y }

    val bitmap = ImageBitmap(width, height)
    CanvasDrawScope().draw(density, layoutDirection, Canvas(bitmap), Size(width.toFloat(), height.toFloat())) {
        translate(left = -left.toFloat(), top = -top.toFloat()) {
            for (y in top until top + height) {
                var runStart = left
                var runColor: Color? = null
                for (x in left..left + width) {
                    val color = if (x < left + width) groundColor(layout, terrain, x, y) else null
                    if (color != runColor) {
                        if (runColor != null) pixelRect(runStart, y, x - runStart, 1, runColor)
                        runStart = x
                        runColor = color
                    }
                }
            }
            paintBackBlades(back, terrain)
        }
    }

    val colors = GrassPalette.bladesFront
    val front = ArrayList<GrassBlade>()
    val frontRandom = Random(6162)
    repeat((frontBladeTrials * area).toInt()) {
        val x = left + frontRandom.nextInt(width)
        val y = top + frontRandom.nextInt(height)
        val bladeHeight = 6 + frontRandom.nextInt(6)
        val phase = frontRandom.nextFloat() * 2f * kotlin.math.PI.toFloat()
        val shade = frontRandom.nextInt(colors.size)
        if (terrain.signedDistance(x + 0.5f, y + 0.5f) !in -0.5f..WorldTuning.grassDepth) return@repeat
        front.add(GrassBlade(x, y, bladeHeight, phase, shade))
    }
    front.sortBy { it.y }
    return SealPatch(bitmap, left, top, front)
}

/** The patch reaches this far beside the path, up into the oval, and down past the bottom of the screen of the stage. */
private const val sealMargin = 8
private const val sealAbove = 30
private const val sealBelow = 6

private fun noise(x: Int, y: Int): Float {
    var h = x * 374761393 + y * 668265263
    h = (h xor (h ushr 13)) * 1274126177
    h = h xor (h ushr 16)
    return (h and 0xFFFF) / 65535f
}

/** Noise that changes smoothly: patches of about one unit of [x] and [y] wide. */
private fun smoothNoise(x: Float, y: Float): Float {
    val ix = floor(x).toInt()
    val iy = floor(y).toInt()
    val fx = x - ix
    val fy = y - iy
    val sx = fx * fx * (3f - 2f * fx)
    val sy = fy * fy * (3f - 2f * fy)
    val top = noise(ix, iy) * (1f - sx) + noise(ix + 1, iy) * sx
    val bottom = noise(ix, iy + 1) * (1f - sx) + noise(ix + 1, iy + 1) * sx
    return top * (1f - sy) + bottom * sy
}

private fun DrawScope.drawGround(layout: WorldLayout, width: Int, height: Int) {
    val left = layout.worldLeft.toInt()
    for (y in 0 until height) {
        var runStart = left
        var runColor: Color? = null
        for (x in left..left + width) {
            val color = if (x < left + width) groundColor(layout, layout.terrain, x, y) else null
            if (color != runColor) {
                if (runColor != null) pixelRect(runStart, y, x - runStart, 1, runColor)
                runStart = x
                runColor = color
            }
        }
    }
}

private fun groundColor(layout: WorldLayout, terrain: Bounds, x: Int, y: Int): Color {
    val distance = terrain.signedDistance(x + 0.5f, y + 0.5f)
    val n = noise(x, y)
    if (distance >= 0f) {
        return if (n < 0.1f) GrassPalette.speckDark else if (n > 0.92f) GrassPalette.speckLight else GrassPalette.base
    }
    val insideHouse = layout.house.signedDistance(x + 0.5f, y + 0.5f)
    // The floor of the house is raw stone, up to its edge, but not where a path leaves it
    if (insideHouse < -1.2f) return rawStone(x, y)
    if (insideHouse < 0f && distance > -1.2f) return stoneEdge
    if (distance > -1.5f) return sandRim
    if (distance > -3f) return sandEdge
    return if (n < 0.06f) sandDark else if (n > 0.93f) sandLight else sand
}

/** Rough stone, not slabs: broad patches of tone, finer grain, a few thin cracks, and specks. */
private fun rawStone(x: Int, y: Int): Color {
    val broad = smoothNoise(x / 7f, y / 7f)
    val grain = smoothNoise(x / 2.5f + 40f, y / 2.5f + 40f)
    val tone = broad * 0.7f + grain * 0.3f
    val isCrack = kotlin.math.abs(smoothNoise(x / 5f + 90f, y / 5f + 17f) - 0.5f) < 0.018f
    val speck = noise(x, y)
    return when {
        isCrack -> stoneCrack
        speck < 0.04f -> stoneDark
        speck > 0.97f -> stoneHighlight
        tone < 0.38f -> stoneDark
        tone < 0.58f -> stoneMid
        tone < 0.78f -> stoneLight
        else -> stoneHighlight
    }
}

private fun DrawScope.drawBackBlades(layout: WorldLayout, width: Int, height: Int) {
    val left = layout.worldLeft.toInt()
    val blades = ArrayList<GrassBlade>()
    val random = Random(4242)
    val trials = (backBladeTrials * (layout.worldWidth * layout.worldHeight) / (layout.arena.width * layout.arena.height)).toInt()
    repeat(trials) {
        val x = left + random.nextInt(width)
        val y = random.nextInt(height + 10)
        val bladeHeight = 6 + random.nextInt(7)
        val shade = random.nextInt(4)
        if (layout.terrain.signedDistance(x + 0.5f, y + 0.5f) < -2f) return@repeat
        // A blade that grows from the grass below the house must not reach up into its floor
        if (layout.touchesFloor(x + 0.5f, y + 0.5f, bladeHeight.toFloat(), 2f)) return@repeat
        blades.add(GrassBlade(x, y, bladeHeight, 0f, shade))
    }
    blades.sortBy { it.y }
    paintBackBlades(blades, layout.terrain)
}

/** Draws the blades of the back grass, which are darker the further they are in the grass. */
private fun DrawScope.paintBackBlades(blades: List<GrassBlade>, terrain: Bounds) {
    val colors = GrassPalette.bladesBack
    for (blade in blades) {
        val depth = terrain.signedDistance(blade.x + 0.5f, blade.y + 0.5f)
        val shade = (blade.shade + if (depth > WorldTuning.grassDepth * 0.6f) -1 else 0).coerceIn(0, colors.lastIndex)
        pixelRect(blade.x, blade.y - blade.height, 1, blade.height, colors[shade])
        if (blade.shade == 3) pixelRect(blade.x + 1, blade.y - blade.height + 2, 1, blade.height - 2, colors[shade])
        pixel(blade.x, blade.y - blade.height, GrassPalette.bladeTip.copy(alpha = 0.55f))
    }
}

/** How many blades are tried over a screen: it is the density of the grass. */
private const val backBladeTrials = 5200

/** A few pebbles and sprouts of grass on the sand of an oval: [oval] is its edge, which they keep away from. */
private fun DrawScope.drawPebbles(oval: Bounds, centerX: Float, centerY: Float, radiusX: Float, radiusY: Float, seed: Int) {
    val random = Random(seed)
    repeat(34) {
        val x = (centerX + (random.nextFloat() * 2f - 1f) * radiusX).roundToInt()
        val y = (centerY + (random.nextFloat() * 2f - 1f) * radiusY).roundToInt()
        if (oval.signedDistance(x.toFloat(), y.toFloat()) > -8f) return@repeat
        if (random.nextBoolean()) drawSandPebble(x, y) else drawSproutOfGrass(x, y)
    }
}

private fun DrawScope.drawSandPebble(x: Int, y: Int) {
    pixelRect(x, y, 2, 1, Color(0xFFA8977A))
    pixel(x, y - 1, Color(0xFFE8DCC0))
    pixelRect(x, y + 1, 2, 1, Color(0xFF8F7E63))
}

private fun DrawScope.drawSproutOfGrass(x: Int, y: Int) {
    val green = Color(0xFF7D8F4B)
    pixel(x, y, Color(0xFF9C8B4F))
    pixel(x - 1, y - 1, green)
    pixel(x + 1, y - 1, green)
    pixel(x, y - 2, green)
}
