package com.lukeneedham.snake.game

/** An animal that comes out of the grass: a mouse to eat, or a poisonous toad to avoid. */
sealed interface Critter : CritterBody {
    val id: Int

    /** Runs fast for a moment, stands still and looks around, then runs again. */
    class Mouse(override val id: Int, x: Float, y: Float, lifetime: Float) :
        Critter,
        CritterBody by MouseBody(x, y, lifetime)

    /** Sits still, then hops a short way in a random direction. Eating one has the effect of its [kind]. */
    class Toad(override val id: Int, val kind: ToadKind, x: Float, y: Float, lifetime: Float) :
        Critter,
        CritterBody by ToadBody(x, y, lifetime)
}
