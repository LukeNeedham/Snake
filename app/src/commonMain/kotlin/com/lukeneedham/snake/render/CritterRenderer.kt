package com.lukeneedham.snake.render

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.lukeneedham.snake.game.Critter
import com.lukeneedham.snake.game.Effect
import com.lukeneedham.snake.game.GameWorld
import com.lukeneedham.snake.game.ToadKind
import kotlin.math.roundToInt
import kotlin.math.sin

/** Animals deeper in the grass than this are hidden by it. */
private const val HIDDEN_DEPTH = 4f

private val ShadowColor = Color(0x44000000)
private val TailColor = Color(0xFFF28FA9)

private class Appearance(val art: SpriteArt, val kind: ToadKind?)

/** What the animal looks like: while the snake is disguise-blind, each animal picks one other animal and stays like it. */
private fun appearance(critter: Critter, world: GameWorld): Appearance {
    if (world.effectLevel(Effect.DISGUISED) >= 0.5f) {
        val disguises = SpriteArt.disguises
        return Appearance(disguises[(critter.id * 7 + 1) % disguises.size], null)
    }
    return when (critter) {
        is Critter.Mouse -> Appearance(SpriteArt.MOUSE, null)
        is Critter.Toad -> Appearance(SpriteArt.TOAD, critter.kind)
    }
}

internal fun DrawScope.drawCritterShadows(world: GameWorld) {
    for (critter in world.critters) {
        if (world.arena.signedDistance(critter.x, critter.y) > HIDDEN_DEPTH) continue
        val x = critter.x.roundToInt()
        val y = critter.y.roundToInt()
        val lift = (critter.height / 3f).roundToInt()
        pixelRect(x - 3 + lift, y, 7 - lift * 2, 1, ShadowColor)
        pixelRect(x - 2 + lift, y + 1, 5 - lift * 2, 1, ShadowColor)
    }
}

/** Animals deep in the grass are not drawn: only those peeking out are, and the front blades then hide their feet. */
internal fun DrawScope.drawCritters(world: GameWorld) {
    for (critter in world.critters.sortedBy { it.y }) {
        if (world.arena.signedDistance(critter.x, critter.y) > HIDDEN_DEPTH) continue
        val look = appearance(critter, world)
        val art = look.art
        val flip = critter.facing < 0
        val left = critter.x.roundToInt() - art.width / 2
        var top = (critter.y - critter.height).roundToInt() - art.height + 1
        if (critter.isMoving && critter is Critter.Mouse && (world.time * 14f).toInt() % 2 == 0) top -= 1
        if (!critter.isMoving && critter is Critter.Toad && sin(world.time * 5f + critter.id) > 0.6f) top -= 1
        if (art == SpriteArt.MOUSE) drawMouseTail(critter, world, top + art.height - 2)
        drawSprite(art, look.kind, left, top, flip)
    }
}

private fun DrawScope.drawMouseTail(critter: Critter, world: GameWorld, y: Int) {
    val back = -critter.facing
    val x = critter.x.roundToInt() + back * 6
    val wag = if (sin(world.time * 9f + critter.id) > 0f) 0 else 1
    pixel(x, y, TailColor)
    pixel(x + back, y - wag, TailColor)
    pixel(x + back * 2, y - 1 - wag, TailColor)
}
