package com.lukeneedham.snake.game

/** Blows up every toad in the world: a burst of particles in the colours of each toad, and the toad is gone. It scores and feeds nothing. */
class ToadBlast(private val particles: ParticleSystem) {

    fun blowUp(critters: MutableList<Critter>) {
        val iterator = critters.iterator()
        while (iterator.hasNext()) {
            val critter = iterator.next()
            if (critter !is Critter.Toad) continue
            particles.explode(critter.x, critter.y - toadMiddle, listOf(critter.kind.body, critter.kind.shade, critter.kind.light))
            iterator.remove()
        }
    }

    private companion object {
        /** The burst comes from the middle of the toad, not from its feet. */
        const val toadMiddle = 3f
    }
}
