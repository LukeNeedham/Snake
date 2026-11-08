package com.lukeneedham.snake.render

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.Canvas as GraphicsCanvas
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.geometry.Size
import com.lukeneedham.snake.game.Arena
import com.lukeneedham.snake.game.GameTuning
import com.lukeneedham.snake.game.GameWorld
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random

private val GrassBase = Color(0xFF1C4528)
private val GrassSpeckDark = Color(0xFF173B22)
private val GrassSpeckLight = Color(0xFF235632)
private val BladeBack = listOf(Color(0xFF1F4F2C), Color(0xFF2A6034), Color(0xFF357A3E), Color(0xFF45934A))
private val BladeFront = listOf(Color(0xFF2F7339), Color(0xFF3E8B45), Color(0xFF55A453))
private val BladeTip = Color(0xFF9BD577)
private val SandRim = Color(0xFF8F7648)
private val SandEdge = Color(0xFFB19659)
private val Sand = Color(0xFFD2B87E)
private val SandLight = Color(0xFFDCC896)
private val SandDark = Color(0xFFC4A96F)

/** Draws the static scene once into a bitmap: the sand of the oval, and the dense grass around it. */
internal fun createBackground(arena: Arena, density: Density, layoutDirection: LayoutDirection): ImageBitmap {
    val width = arena.width.toInt()
    val height = arena.height.toInt()
    val bitmap = ImageBitmap(width, height)
    CanvasDrawScope().draw(density, layoutDirection, GraphicsCanvas(bitmap), Size(width.toFloat(), height.toFloat())) {
        drawGround(arena, width, height)
        drawBackBlades(arena, width, height)
        drawPebbles(arena)
    }
    return bitmap
}

private fun noise(x: Int, y: Int): Float {
    var h = x * 374761393 + y * 668265263
    h = (h xor (h ushr 13)) * 1274126177
    h = h xor (h ushr 16)
    return (h and 0xFFFF) / 65535f
}

private fun DrawScope.drawGround(arena: Arena, width: Int, height: Int) {
    for (y in 0 until height) {
        var runStart = 0
        var runColor: Color? = null
        for (x in 0..width) {
            val color = if (x < width) groundColor(arena, x, y) else null
            if (color != runColor) {
                if (runColor != null) pixelRect(runStart, y, x - runStart, 1, runColor)
                runStart = x
                runColor = color
            }
        }
    }
}

private fun groundColor(arena: Arena, x: Int, y: Int): Color {
    val distance = arena.signedDistance(x + 0.5f, y + 0.5f)
    val n = noise(x, y)
    if (distance >= 0f) {
        return if (n < 0.1f) GrassSpeckDark else if (n > 0.92f) GrassSpeckLight else GrassBase
    }
    if (distance > -1.5f) return SandRim
    if (distance > -3f) return SandEdge
    return if (n < 0.06f) SandDark else if (n > 0.93f) SandLight else Sand
}

private fun DrawScope.drawBackBlades(arena: Arena, width: Int, height: Int) {
    val random = Random(4242)
    val blades = ArrayList<GrassBlade>()
    repeat(5200) {
        val x = random.nextInt(width)
        val y = random.nextInt(height + 10)
        if (arena.signedDistance(x + 0.5f, y + 0.5f) >= -2f) {
            blades.add(GrassBlade(x, y, 6 + random.nextInt(7), 0f, random.nextInt(4)))
        }
    }
    blades.sortBy { it.y }
    for (blade in blades) {
        val depth = arena.signedDistance(blade.x + 0.5f, blade.y + 0.5f)
        val shade = (blade.shade + if (depth > GameTuning.GRASS_DEPTH * 0.6f) -1 else 0).coerceIn(0, BladeBack.lastIndex)
        pixelRect(blade.x, blade.y - blade.height, 1, blade.height, BladeBack[shade])
        if (blade.shade == 3) pixelRect(blade.x + 1, blade.y - blade.height + 2, 1, blade.height - 2, BladeBack[shade])
        pixel(blade.x, blade.y - blade.height, BladeTip.copy(alpha = 0.55f))
    }
}

private fun DrawScope.drawPebbles(arena: Arena) {
    val random = Random(777)
    repeat(34) {
        val x = (arena.centerX + (random.nextFloat() * 2f - 1f) * arena.radiusX).roundToInt()
        val y = (arena.centerY + (random.nextFloat() * 2f - 1f) * arena.radiusY).roundToInt()
        if (arena.signedDistance(x.toFloat(), y.toFloat()) > -8f) return@repeat
        if (random.nextBoolean()) {
            pixelRect(x, y, 2, 1, Color(0xFFA8977A))
            pixel(x, y - 1, Color(0xFFE8DCC0))
            pixelRect(x, y + 1, 2, 1, Color(0xFF8F7E63))
        } else {
            pixel(x, y, Color(0xFF9C8B4F))
            pixel(x - 1, y - 1, Color(0xFF7D8F4B))
            pixel(x + 1, y - 1, Color(0xFF7D8F4B))
            pixel(x, y - 2, Color(0xFF7D8F4B))
        }
    }
}

/** The blades along the inner edge of the grass: drawn in front of the animals, so that an animal in the grass is hidden. */
internal fun createFrontGrass(arena: Arena): List<GrassBlade> {
    val random = Random(99)
    val width = arena.width.toInt()
    val height = arena.height.toInt()
    val blades = ArrayList<GrassBlade>()
    repeat(2400) {
        val x = random.nextInt(width)
        val y = random.nextInt(height)
        val distance = arena.signedDistance(x + 0.5f, y + 0.5f)
        if (distance in -0.5f..9f) {
            blades.add(GrassBlade(x, y, 6 + random.nextInt(6), random.nextFloat() * 2f * PI.toFloat(), random.nextInt(BladeFront.size)))
        }
    }
    blades.sortBy { it.y }
    return blades
}

internal fun DrawScope.drawFrontGrass(blades: List<GrassBlade>, world: GameWorld) {
    val arena = world.arena
    val hidden = world.critters.filter { arena.signedDistance(it.x, it.y) > -1f }
    for (blade in blades) {
        var sway = sin(world.time * 2.2f + blade.phase + blade.x * 0.15f) * 1.4f
        for (critter in hidden) {
            if (abs(blade.x - critter.x) < 7f && abs(blade.y - critter.y) < 7f) {
                sway += sin(world.time * 30f + blade.phase) * 1.7f
            }
        }
        val offset = sway.roundToInt()
        val color = BladeFront[blade.shade]
        val lower = blade.height / 2
        pixelRect(blade.x, blade.y - lower, 1, lower + 1, color)
        pixelRect(blade.x + offset, blade.y - blade.height, 1, blade.height - lower, color)
        pixel(blade.x + offset, blade.y - blade.height, BladeTip)
        if (blade.shade == 2) pixelRect(blade.x + 1, blade.y - lower, 1, lower, color)
    }
}

/** A few butterflies that drift over the sand. */
internal fun DrawScope.drawButterflies(world: GameWorld) {
    val arena = world.arena
    val wings = listOf(Color(0xFFFFFFFF), Color(0xFFFFD1E8), Color(0xFFBFE3FF))
    for (i in 0 until 3) {
        val t = world.time * (0.22f + 0.05f * i) + i * 2.1f
        val x = arena.centerX + sin(t) * arena.radiusX * 0.7f
        val y = arena.centerY + cos(t * 0.8f + i) * arena.radiusY * 0.7f
        val flap = ((world.time * 12f + i).toInt() % 2) == 0
        val color = wings[i % wings.size]
        pixel(x.roundToInt(), y.roundToInt(), Color(0xFF2B2230))
        if (flap) {
            pixelRect(x.roundToInt() - 2, y.roundToInt() - 1, 2, 2, color)
            pixelRect(x.roundToInt() + 1, y.roundToInt() - 1, 2, 2, color)
        } else {
            pixel(x.roundToInt() - 1, y.roundToInt(), color)
            pixel(x.roundToInt() + 1, y.roundToInt(), color)
        }
    }
}
