package com.lukeneedham.snake.render

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import com.lukeneedham.snake.game.Arena
import com.lukeneedham.snake.game.DenLayout
import com.lukeneedham.snake.game.WorldTuning
import kotlin.math.roundToInt
import kotlin.random.Random

private val sandRim = Color(0xFF8F7648)
private val sandEdge = Color(0xFFB19659)
private val sand = Color(0xFFD2B87E)
private val sandLight = Color(0xFFDCC896)
private val sandDark = Color(0xFFC4A96F)
private val stone = Color(0xFF8F8C94)
private val stoneLight = Color(0xFF9E9BA3)
private val stoneDark = Color(0xFF7C7981)
private val stoneJoint = Color(0xFF5F5C66)
private val stoneEdge = Color(0xFF4D4A54)

/** How big the slabs of the floor of the den are, in pixels. */
private const val slabWidth = 10
private const val slabHeight = 7

/**
 * Draws the static scene once into a bitmap: the sand of the oval, and the dense grass around it. The bitmap is two screens tall: under the
 * oval is the second screen, with the den of the snake and the bare path through the grass that leads from it to the oval.
 */
internal fun createBackground(den: DenLayout, density: Density, layoutDirection: LayoutDirection): ImageBitmap {
    val arena = den.arena
    val width = arena.width.toInt()
    val height = den.worldHeight.toInt()
    val bitmap = ImageBitmap(width, height)
    CanvasDrawScope().draw(density, layoutDirection, Canvas(bitmap), Size(width.toFloat(), height.toFloat())) {
        drawGround(den, width, height)
        drawBackBlades(den, width, height)
        drawPebbles(arena)
        drawDenRocks(den)
    }
    return bitmap
}

private fun noise(x: Int, y: Int): Float {
    var h = x * 374761393 + y * 668265263
    h = (h xor (h ushr 13)) * 1274126177
    h = h xor (h ushr 16)
    return (h and 0xFFFF) / 65535f
}

private fun DrawScope.drawGround(den: DenLayout, width: Int, height: Int) {
    for (y in 0 until height) {
        var runStart = 0
        var runColor: Color? = null
        for (x in 0..width) {
            val color = if (x < width) groundColor(den, x, y) else null
            if (color != runColor) {
                if (runColor != null) pixelRect(runStart, y, x - runStart, 1, runColor)
                runStart = x
                runColor = color
            }
        }
    }
}

private fun groundColor(den: DenLayout, x: Int, y: Int): Color {
    val distance = den.ground.signedDistance(x + 0.5f, y + 0.5f)
    // The stretch of the path that repeats must look the same every time: its noise comes from the rows of one period
    val n = noise(x, den.loopRow(y))
    if (distance >= 0f) {
        return if (n < 0.1f) GrassPalette.speckDark else if (n > 0.92f) GrassPalette.speckLight else GrassPalette.base
    }
    val insideDen = den.den.signedDistance(x + 0.5f, y + 0.5f)
    // The floor of the den is stone, up to its edge, but not where the path leaves it
    if (insideDen < -1.2f) return stoneColor(x, y)
    if (insideDen < 0f && distance > -1.2f) return stoneEdge
    if (distance > -1.5f) return sandRim
    if (distance > -3f) return sandEdge
    return if (n < 0.06f) sandDark else if (n > 0.93f) sandLight else sand
}

/** Flagstones: rows of slabs, each row shifted from the one above, with a joint between them and a tone of their own. */
private fun stoneColor(x: Int, y: Int): Color {
    val row = y / slabHeight
    val shift = if (row % 2 == 0) 0 else slabWidth / 2
    val column = (x + shift) / slabWidth
    if (y % slabHeight == 0 || (x + shift) % slabWidth == 0) return stoneJoint
    val tone = noise(column, row)
    val speck = noise(x, y)
    if (speck < 0.06f) return stoneDark
    if (speck > 0.95f) return stoneLight
    return if (tone < 0.33f) stoneDark else if (tone > 0.66f) stoneLight else stone
}

private fun DrawScope.drawBackBlades(den: DenLayout, width: Int, height: Int) {
    val blades = ArrayList<GrassBlade>()
    val random = Random(4242)
    repeat((backBladeTrials * den.worldHeight / den.arena.height).toInt()) {
        val x = random.nextInt(width)
        val y = random.nextInt(height + 10)
        // The stretch of the path that repeats gets its blades below, one period of them over and over
        if (y >= den.loopTopRow && y < den.loopBottomRow) return@repeat
        addBackBlade(den, blades, random, x, y)
    }
    val tileRandom = Random(4343)
    repeat(backBladeTrials * den.loopPeriodRows / den.arena.height.toInt()) {
        val x = tileRandom.nextInt(width)
        val firstY = den.loopTopRow + tileRandom.nextInt(den.loopPeriodRows)
        val tile = ArrayList<GrassBlade>()
        addBackBlade(den, tile, tileRandom, x, firstY)
        for (blade in tile) {
            var y = blade.y
            while (y < den.loopBottomRow) {
                blades.add(GrassBlade(blade.x, y, blade.height, 0f, blade.shade))
                y += den.loopPeriodRows
            }
        }
    }
    blades.sortBy { it.y }
    val colors = GrassPalette.bladesBack
    for (blade in blades) {
        val depth = den.ground.signedDistance(blade.x + 0.5f, blade.y + 0.5f)
        val shade = (blade.shade + if (depth > WorldTuning.grassDepth * 0.6f) -1 else 0).coerceIn(0, colors.lastIndex)
        pixelRect(blade.x, blade.y - blade.height, 1, blade.height, colors[shade])
        if (blade.shade == 3) pixelRect(blade.x + 1, blade.y - blade.height + 2, 1, blade.height - 2, colors[shade])
        pixel(blade.x, blade.y - blade.height, GrassPalette.bladeTip.copy(alpha = 0.55f))
    }
}

/** Adds a blade of grass at [x], [y] to [blades], unless that is not in the grass, or the blade would reach into the floor of the den. */
private fun addBackBlade(den: DenLayout, blades: MutableList<GrassBlade>, random: Random, x: Int, y: Int) {
    val height = 6 + random.nextInt(7)
    val shade = random.nextInt(4)
    if (den.ground.signedDistance(x + 0.5f, y + 0.5f) < -2f) return
    if (den.touchesFloor(x + 0.5f, y + 0.5f, height.toFloat(), 2f)) return
    blades.add(GrassBlade(x, y, height, 0f, shade))
}

/** How many blades are tried over a screen: it is the density of the grass. */
private const val backBladeTrials = 5200

private fun DrawScope.drawPebbles(arena: Arena) {
    val random = Random(777)
    repeat(34) {
        val x = (arena.centerX + (random.nextFloat() * 2f - 1f) * arena.radiusX).roundToInt()
        val y = (arena.centerY + (random.nextFloat() * 2f - 1f) * arena.radiusY).roundToInt()
        if (arena.signedDistance(x.toFloat(), y.toFloat()) > -8f) return@repeat
        if (random.nextBoolean()) drawPebble(x, y) else drawSproutOfGrass(x, y)
    }
}

private fun DrawScope.drawPebble(x: Int, y: Int) {
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
