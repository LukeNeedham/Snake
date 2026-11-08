package com.lukeneedham.snake.render

import androidx.compose.ui.graphics.drawscope.DrawScope
import com.lukeneedham.snake.game.BirdSize
import com.lukeneedham.snake.game.Critter
import com.lukeneedham.snake.game.GameWorld
import kotlin.math.roundToInt
import kotlin.math.sin

/** How many times a second a flying bird flaps its wings. */
private const val flapsPerSecond = 7f

/** A bird: perched on the ground, or flapping above it. It is drawn lifted by its height; its shadow stays on the ground. */
internal fun DrawScope.drawBird(bird: Critter.Bird, world: GameWorld) {
    val art = birdArt(bird, world)
    val palette = birdPalette(bird.kind)
    val left = bird.x.roundToInt() - art.width / 2
    val top = (bird.y - bird.height).roundToInt() - art.height + 1 - breathe(bird, world)
    drawRows(art.rows, art.width, left, top, flip = bird.facing < 0, colorOf = palette::colorOf)
}

/** The birds that fly in or off are drawn over the snake and the grass, which they fly above. */
internal fun DrawScope.drawFlyingBirds(world: GameWorld) {
    if (isDisguised(world)) return
    for (critter in world.critters) {
        if (critter is Critter.Bird && critter.isAirborne) drawBird(critter, world)
    }
}

private fun birdArt(bird: Critter.Bird, world: GameWorld): BirdArt {
    val isLarge = bird.kind.size == BirdSize.Large
    if (!bird.isAirborne) return if (isLarge) BirdArt.LargePerched else BirdArt.SmallPerched
    val isWingUp = (world.time * flapsPerSecond + bird.id).toInt() % 2 == 0
    return when {
        isLarge && isWingUp -> BirdArt.LargeWingsUp
        isLarge -> BirdArt.LargeWingsDown
        isWingUp -> BirdArt.SmallWingsUp
        else -> BirdArt.SmallWingsDown
    }
}

/** A bird on the ground puffs up for a moment now and then; one that has seen the snake shivers. */
private fun breathe(bird: Critter.Bird, world: GameWorld): Int = when {
    bird.isAirborne || bird.isMoving -> 0
    bird.isFleeing -> if ((world.time * 30f).toInt() % 2 == 0) 1 else 0
    sin(world.time * 4f + bird.id) > 0.7f -> 1
    else -> 0
}
