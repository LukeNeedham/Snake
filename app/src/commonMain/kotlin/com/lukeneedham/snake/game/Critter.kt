package com.lukeneedham.snake.game

/** An animal in the oval: a mouse to eat, a poisonous toad to avoid, or a bird that flies in and gives a power up. */
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

    /**
     * Flies in from off the screen at [x], [y] and lands at [landX], [landY], stays for [staySeconds], and flies off. It flies off
     * sooner if the snake comes close. Eating one gives the power up of its [kind]. With [isLanded] it is on the ground already, at
     * [x], [y]: the tests use it to put a bird where they need it.
     */
    class Bird(
        override val id: Int,
        val kind: BirdKind,
        x: Float,
        y: Float,
        landX: Float,
        landY: Float,
        staySeconds: Float,
        isLanded: Boolean = false,
    ) : Critter,
        CritterBody by BirdBody(x, y, landX, landY, kind.alarmDistance, staySeconds, isLanded)
}
