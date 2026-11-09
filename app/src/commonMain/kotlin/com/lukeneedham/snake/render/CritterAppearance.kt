package com.lukeneedham.snake.render

import com.lukeneedham.snake.game.Critter
import com.lukeneedham.snake.game.Effect
import com.lukeneedham.snake.game.GameWorld
import com.lukeneedham.snake.game.ToadKind

/** What a mouse, a toad or a disguised animal looks like: a sprite, and the colour of the toad if it is one. */
internal class CritterAppearance(val art: SpriteArt, val kind: ToadKind?)

/** Whether the animals look like other animals, because of the teal toad. */
internal fun isDisguised(world: GameWorld): Boolean = world.effects.level(Effect.Imposters) >= 0.5f

/**
 * While the snake is disguise-blind, each animal picks one other animal and stays like it. A bird that is not disguised has no
 * appearance of this kind: it is drawn by `drawBird`, so this is null.
 */
internal fun appearanceOf(critter: Critter, world: GameWorld): CritterAppearance? {
    if (isDisguised(world)) {
        val disguises = SpriteArt.disguises
        return CritterAppearance(disguises[(critter.id * 7 + 1) % disguises.size], null)
    }
    return when (critter) {
        is Critter.Mouse -> CritterAppearance(SpriteArt.Mouse, null)
        is Critter.Toad -> CritterAppearance(SpriteArt.Toad, critter.kind)
        is Critter.Bird -> null
    }
}
