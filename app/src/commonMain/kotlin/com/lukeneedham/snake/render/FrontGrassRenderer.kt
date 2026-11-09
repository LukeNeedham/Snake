package com.lukeneedham.snake.render

import androidx.compose.ui.graphics.drawscope.DrawScope
import com.lukeneedham.snake.game.DenLayout
import com.lukeneedham.snake.game.GameWorld
import com.lukeneedham.snake.game.WorldTuning
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random

/**
 * The blades along the inner edge of the grass, and along the edge of the den and its path: drawn in front of the animals, so that an
 * animal in the grass is hidden. They cover both screens of the world.
 */
internal fun createFrontGrass(den: DenLayout): List<GrassBlade> {
    val arena = den.arena
    val width = arena.width.toInt()
    val height = den.worldHeight.toInt()
    val blades = ArrayList<GrassBlade>()
    val random = Random(99)
    repeat(frontBladeTrials * 3) {
        val x = random.nextInt(width)
        val y = random.nextInt(height)
        // The stretch of the path that repeats gets its blades below, one period of them over and over
        if (y >= den.loopTopRow && y < den.loopBottomRow) return@repeat
        addFrontBlade(den, blades, random, x, y)
    }
    val tileRandom = Random(98)
    repeat(frontBladeTrials * den.loopPeriodRows / arena.height.toInt()) {
        val x = tileRandom.nextInt(width)
        val firstY = den.loopTopRow + tileRandom.nextInt(den.loopPeriodRows)
        val tile = ArrayList<GrassBlade>()
        addFrontBlade(den, tile, tileRandom, x, firstY)
        for (blade in tile) {
            var y = blade.y
            while (y < den.loopBottomRow) {
                blades.add(GrassBlade(blade.x, y, blade.height, blade.phase, blade.shade))
                y += den.loopPeriodRows
            }
        }
    }
    blades.sortBy { it.y }
    return blades
}

/** Adds a blade at [x], [y] to [blades], if it is along the edge of the ground and not near the den, whose floor stays clear of grass. */
private fun addFrontBlade(den: DenLayout, blades: MutableList<GrassBlade>, random: Random, x: Int, y: Int) {
    val colors = GrassPalette.bladesFront
    val bladeHeight = 6 + random.nextInt(6)
    val phase = random.nextFloat() * 2f * PI.toFloat()
    val shade = random.nextInt(colors.size)
    val distance = den.ground.signedDistance(x + 0.5f, y + 0.5f)
    if (distance !in -0.5f..WorldTuning.grassDepth) return
    if (den.den.signedDistance(x + 0.5f, y + 0.5f) < denClearance) return
    blades.add(GrassBlade(x, y, bladeHeight, phase, shade))
}

/** How many blades are tried over a screen. */
private const val frontBladeTrials = 2400

/** The blades sway all the time, and shake where an animal hides in the grass. */
internal fun DrawScope.drawFrontGrass(blades: List<GrassBlade>, world: GameWorld) {
    val arena = world.arena
    val hidden = world.critters.filter { arena.signedDistance(it.x, it.y) > -1f }
    val colors = GrassPalette.bladesFront
    val screenTop = world.cameraY - bladeReach
    val screenBottom = world.cameraY + arena.height + bladeReach
    for (blade in blades) {
        if (blade.y < screenTop || blade.y > screenBottom) continue
        var sway = sin(world.time * 2.2f + blade.phase + blade.x * 0.15f) * 1.4f
        for (critter in hidden) {
            if (abs(blade.x - critter.x) < 7f && abs(blade.y - critter.y) < 7f) {
                sway += sin(world.time * 30f + blade.phase) * 1.7f
            }
        }
        val offset = sway.roundToInt()
        val color = colors[blade.shade]
        val lower = blade.height / 2
        pixelRect(blade.x, blade.y - lower, 1, lower + 1, color)
        pixelRect(blade.x + offset, blade.y - blade.height, 1, blade.height - lower, color)
        pixel(blade.x + offset, blade.y - blade.height, GrassPalette.bladeTip)
        if (blade.shade == 2) pixelRect(blade.x + 1, blade.y - lower, 1, lower, color)
    }
}

/** The tallest a blade is, with its sway: a blade further than this from the screen cannot be seen. */
private const val bladeReach = 20f

/** How far from the edge of the den no front blade grows: a blade is as tall as this. */
private const val denClearance = 13f
