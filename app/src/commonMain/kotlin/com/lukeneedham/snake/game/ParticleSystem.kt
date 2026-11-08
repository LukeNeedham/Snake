package com.lukeneedham.snake.game

import androidx.compose.ui.graphics.Color
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/** The specks of dust, the sparkles and the floating texts. */
class ParticleSystem(private val random: Random) {
    private val mutableParticles = ArrayList<Particle>()
    private val mutableTexts = ArrayList<FloatingText>()

    val particles: List<Particle> get() = mutableParticles
    val floatingTexts: List<FloatingText> get() = mutableTexts

    fun update(dt: Float) {
        for (particle in mutableParticles) particle.update(dt)
        mutableParticles.removeAll { it.life <= 0f }
        for (text in mutableTexts) text.update(dt)
        mutableTexts.removeAll { it.life <= 0f }
        if (mutableParticles.size > maxParticles) mutableParticles.subList(0, mutableParticles.size - maxParticles).clear()
    }

    fun floatText(text: String, x: Float, y: Float, color: Color) {
        mutableTexts.add(FloatingText(text, x, y, color))
    }

    /** Small puffs that fall back to the ground. */
    fun dust(x: Float, y: Float, count: Int, color: Color) {
        repeat(count) {
            val angle = random.nextFloat() * 2f * PI.toFloat()
            val speed = 3f + random.nextFloat() * 8f
            mutableParticles.add(
                Particle(x, y, cos(angle) * speed, sin(angle) * speed * 0.5f - 3f, 0.5f, color, 1, 8f, ParticleShape.Square),
            )
        }
    }

    /** Sparkles that fly out and fade. */
    fun sparkles(x: Float, y: Float, count: Int, colors: List<Color>) {
        repeat(count) {
            val angle = random.nextFloat() * 2f * PI.toFloat()
            val speed = 6f + random.nextFloat() * 18f
            mutableParticles.add(
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
            mutableParticles.add(
                Particle(x, y, cos(angle) * speed, sin(angle) * speed, 0.9f, colors[random.nextInt(colors.size)], 2, 30f, ParticleShape.Square),
            )
        }
    }

    /** One speck that stays where it was dropped and fades. */
    fun speck(x: Float, y: Float, color: Color) {
        mutableParticles.add(Particle(x, y, 0f, 0f, 0.4f, color, 1, 0f, ParticleShape.Square))
    }

    private companion object {
        const val maxParticles = 300
        const val explosionSize = 24
    }
}
