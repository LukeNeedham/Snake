package com.lukeneedham.snake.game

import androidx.compose.ui.graphics.Color
import kotlin.random.Random

/** Drops the specks and sparkles that show the effects of the toads: a trail behind a speedy snake, glints on disguised animals. */
class EffectParticleEmitter(private val particles: ParticleSystem, private val random: Random) {

    fun emit(effects: EffectTracker, snake: Snake, critters: List<Critter>) {
        if (effects.level(Effect.Speedy) > shownLevel && random.nextFloat() < 0.5f) {
            snake.body.points.getOrNull(3)?.let { tail ->
                particles.speck(
                    tail.x + (random.nextFloat() - 0.5f) * 4f,
                    tail.y + (random.nextFloat() - 0.5f) * 4f,
                    Color(0xFFFFB35C),
                )
            }
        }
        if (effects.level(Effect.Disguised) > shownLevel && random.nextFloat() < 0.1f && critters.isNotEmpty()) {
            val critter = critters[random.nextInt(critters.size)]
            particles.sparkles(critter.x, critter.y - 6f, 1, listOf(Color(0xFFFFFFFF), Color(0xFFA6EDE2)))
        }
    }

    private companion object {
        /** An effect has to show this strongly before it makes particles. */
        const val shownLevel = 0.3f
    }
}
