package com.lukeneedham.snake.game

import androidx.compose.ui.graphics.Color
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/** The specks of dust, the sparkles and the floating texts. */
class ParticleSystem(private val random: Random) {
    val particles = ArrayList<Particle>()
    val floatingTexts = ArrayList<FloatingText>()

    fun update(dt: Float) {
        for (particle in particles) particle.update(dt)
        particles.removeAll { it.life <= 0f }
        for (text in floatingTexts) text.update(dt)
        floatingTexts.removeAll { it.life <= 0f }
        if (particles.size > maxParticles) particles.subList(0, particles.size - maxParticles).clear()
    }

    fun floatText(text: String, x: Float, y: Float, color: Color) {
        floatingTexts.add(FloatingText(text, x, y, color))
    }

    /** Small puffs that fall back to the ground. */
    fun dust(x: Float, y: Float, count: Int, color: Color) {
        repeat(count) {
            val angle = random.nextFloat() * 2f * PI.toFloat()
            val speed = 3f + random.nextFloat() * 8f
            particles.add(
                Particle(x, y, cos(angle) * speed, sin(angle) * speed * 0.5f - 3f, 0.5f, color, 1, 8f, ParticleShape.Square),
            )
        }
    }

    /** Sparkles that fly out and fade. */
    fun sparkles(x: Float, y: Float, count: Int, colors: List<Color>) {
        repeat(count) {
            val angle = random.nextFloat() * 2f * PI.toFloat()
            val speed = 6f + random.nextFloat() * 18f
            particles.add(
                Particle(
                    x, y, cos(angle) * speed, sin(angle) * speed - 8f, 0.6f + random.nextFloat() * 0.4f,
                    colors[random.nextInt(colors.size)], 1, 18f, ParticleShape.Sparkle,
                ),
            )
        }
    }

    /** Bits that fly out and fall, when the snake dies. */
    fun explode(x: Float, y: Float, colors: List<Color>) {
        repeat(explosionSize) {
            val angle = random.nextFloat() * 2f * PI.toFloat()
            val speed = 10f + random.nextFloat() * 28f
            particles.add(
                Particle(x, y, cos(angle) * speed, sin(angle) * speed, 0.9f, colors[random.nextInt(colors.size)], 2, 30f, ParticleShape.Square),
            )
        }
    }

    /** One speck that stays where it was dropped and fades. */
    fun speck(x: Float, y: Float, color: Color) {
        particles.add(Particle(x, y, 0f, 0f, 0.4f, color, 1, 0f, ParticleShape.Square))
    }

    private companion object {
        const val maxParticles = 300
        const val explosionSize = 24
    }
}
