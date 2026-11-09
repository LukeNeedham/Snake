package com.lukeneedham.snake.render

import androidx.compose.ui.graphics.drawscope.DrawScope
import com.lukeneedham.snake.game.Critter
import com.lukeneedham.snake.game.GameWorld
import com.lukeneedham.snake.game.WorldLayout
import com.lukeneedham.snake.game.WorldTuning
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random

/**
 * The blades along the edge of the grass round the stage, the room, the paths and the house: drawn in front of the animals, so that an
 * animal in the grass is hidden. They cover the whole map.
 */
internal fun createFrontGrass(layout: WorldLayout): List<GrassBlade> {
    val arena = layout.arena
    val left = layout.worldLeft.toInt()
    val width = layout.worldWidth.toInt()
    val height = layout.worldHeight.toInt()
    val random = Random(99)
    val blades = ArrayList<GrassBlade>()
    val trials = (frontBladeTrials * (layout.worldWidth * layout.worldHeight) / (arena.width * arena.height)).toInt()
    repeat(trials) {
        val x = left + random.nextInt(width)
        val y = random.nextInt(height)
        val colors = GrassPalette.bladesFront
        val bladeHeight = 6 + random.nextInt(6)
        val phase = random.nextFloat() * 2f * PI.toFloat()
        val shade = random.nextInt(colors.size)
        val distance = layout.terrain.signedDistance(x + 0.5f, y + 0.5f)
        if (distance !in -0.5f..WorldTuning.grassDepth) return@repeat
        // No tall grass round the house: it would hide the snake and the rocks
        if (layout.house.signedDistance(x + 0.5f, y + 0.5f) < houseClearance) return@repeat
        blades.add(GrassBlade(x, y, bladeHeight, phase, shade))
    }
    blades.sortBy { it.y }
    return blades
}

/** The blades sway all the time, and shake where an animal hides in the grass. */
internal fun DrawScope.drawFrontGrass(blades: List<GrassBlade>, world: GameWorld) {
    val arena = world.arena
    val hidden = world.critters.filter { world.layout.ground.signedDistance(it.x, it.y) > -1f }
    val screenTop = world.cameraY - bladeReach
    val screenBottom = world.cameraY + arena.height + bladeReach
    val screenLeft = world.cameraX - bladeReach
    val screenRight = world.cameraX + arena.width + bladeReach
    for (blade in blades) {
        if (blade.y < screenTop || blade.y > screenBottom || blade.x < screenLeft || blade.x > screenRight) continue
        drawFrontBlade(blade, world, hidden)
    }
}

/** One blade of the front grass: it sways all the time, and shakes where an animal hides in the grass. */
internal fun DrawScope.drawFrontBlade(blade: GrassBlade, world: GameWorld, hidden: List<Critter>) {
    val colors = GrassPalette.bladesFront
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

/** How many blades are tried over a screen. */
internal const val frontBladeTrials = 2400

/** The tallest a blade is, with its sway: a blade further than this from the screen cannot be seen. */
private const val bladeReach = 20f

/** How far from the edge of the house no front blade grows: a blade is as tall as this. */
private const val houseClearance = 13f
