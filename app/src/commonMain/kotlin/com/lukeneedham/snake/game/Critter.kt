package com.lukeneedham.snake.game

/** An animal in the oval: a mouse to eat, a poisonous toad to avoid, or a bird that flies in and gives a power up. */
sealed interface Critter : CritterBody {
    val id: Int

    /** Runs fast for a moment (as fast as the level makes it, by its [speedFactor]), stands still and looks around, then runs again. */
    class Mouse(override val id: Int, x: Float, y: Float, lifetime: Float, tameTarget: Vec2? = null, speedFactor: Float = 1f) :
        Critter,
        CritterBody by MouseBody(x, y, lifetime, tameTarget, speedFactor)

    /** Sits still, then hops a short way in a random direction. Eating one has the power down of its [kind]. */
    class Toad(override val id: Int, val kind: ToadKind, x: Float, y: Float, lifetime: Float, tameTarget: Vec2? = null) :
        Critter,
        CritterBody by ToadBody(x, y, lifetime, tameTarget)

    /**
     * Flies in from off the screen at [x], [y] and lands at [landX], [landY], stays for [staySeconds], and flies off. It flies off
     * sooner if the snake comes close. Eating one gives the power up of its [kind]. With [isLanded] it is on the ground already, at
     * [x], [y]: the tests use it to put a bird where they need it. A tame critter (the ones of the lessons) comes to its target, and stays there
     * for as long as it takes to be eaten: a mouse runs there, a toad hops there and a bird flies there, and none of them flees. A tame mouse or
     * toad is given its `tameTarget`, a bird its landing spot.
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
        isTame: Boolean = false,
    ) : Critter,
        CritterBody by BirdBody(x, y, landX, landY, kind.alarmDistance, staySeconds, isLanded, isTame)
}
