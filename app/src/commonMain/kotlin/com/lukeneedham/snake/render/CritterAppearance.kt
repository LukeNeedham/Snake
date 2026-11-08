package com.lukeneedham.snake.render

import com.lukeneedham.snake.game.Critter
import com.lukeneedham.snake.game.Effect
import com.lukeneedham.snake.game.GameWorld
import com.lukeneedham.snake.game.ToadKind

/** What an animal looks like: a sprite, and the colour of the toad if it is one. */
internal class CritterAppearance(val art: SpriteArt, val kind: ToadKind?)

/** While the snake is disguise-blind, each animal picks one other animal and stays like it. */
internal fun appearanceOf(critter: Critter, world: GameWorld): CritterAppearance {
    if (world.effects.level(Effect.Disguised) >= 0.5f) {
        val disguises = SpriteArt.disguises
        return CritterAppearance(disguises[(critter.id * 7 + 1) % disguises.size], null)
    }
    return when (critter) {
        is Critter.Mouse -> CritterAppearance(SpriteArt.Mouse, null)
        is Critter.Toad -> CritterAppearance(SpriteArt.Toad, critter.kind)
    }
}
