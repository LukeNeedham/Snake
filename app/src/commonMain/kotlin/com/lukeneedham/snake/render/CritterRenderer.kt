package com.lukeneedham.snake.render

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.lukeneedham.snake.game.Critter
import com.lukeneedham.snake.game.CritterType
import com.lukeneedham.snake.game.Effect
import com.lukeneedham.snake.game.GameWorld
import com.lukeneedham.snake.game.ToadKind
import kotlin.math.floor
import kotlin.math.roundToInt
import kotlin.math.sin

private val ShadowColor = Color(0x44000000)
private val TailColor = Color(0xFFF28FA9)

private class Appearance(val art: SpriteArt, val kind: ToadKind?)

/** What the animal looks like right now: while the snake is disguised-blind, a different animal every second or so. */
private fun appearance(critter: Critter, world: GameWorld): Appearance {
    if (world.effectLevel(Effect.DISGUISED) < 0.5f) {
        return if (critter.type == CritterType.MOUSE) Appearance(SpriteArt.MOUSE, null) else Appearance(SpriteArt.TOAD, critter.toadKind)
    }
    val slot = floor(world.time * 1.1f + critter.id * 0.37f).toInt()
    val hash = (slot * 31 + critter.id * 17) and 0x7FFFFFFF
    val arts = SpriteArt.entries
    return Appearance(arts[hash % arts.size], ToadKind.entries[(hash / 7) % ToadKind.entries.size])
}

internal fun DrawScope.drawCritterShadows(world: GameWorld) {
    for (critter in world.critters) {
        if (world.arena.signedDistance(critter.x, critter.y) > 6.5f) continue
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
        if (world.arena.signedDistance(critter.x, critter.y) > 6.5f) continue
        val look = appearance(critter, world)
        val art = look.art
        val flip = critter.facing < 0
        val left = critter.x.roundToInt() - art.width / 2
        var top = (critter.y - critter.height).roundToInt() - art.height + 1
        if (critter.isMoving && critter.type == CritterType.MOUSE && (world.time * 14f).toInt() % 2 == 0) top -= 1
        if (!critter.isMoving && critter.type == CritterType.TOAD && sin(world.time * 5f + critter.id) > 0.6f) top -= 1
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
