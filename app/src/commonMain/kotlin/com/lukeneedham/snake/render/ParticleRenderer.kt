package com.lukeneedham.snake.render

import androidx.compose.ui.graphics.drawscope.DrawScope
import com.lukeneedham.snake.game.GameWorld
import com.lukeneedham.snake.game.Particle
import com.lukeneedham.snake.game.ParticleShape
import kotlin.math.roundToInt

internal fun DrawScope.drawParticles(world: GameWorld) {
    for (particle in world.particleSystem.particles) drawParticle(particle)
    for (text in world.particleSystem.floatingTexts) {
        val fade = (text.life / text.maxLife * 2f).coerceIn(0f, 1f)
        drawCenteredPixelText(
            text = text.text,
            centerX = text.x.roundToInt(),
            y = text.y.roundToInt(),
            color = text.color.copy(alpha = fade),
            outline = PixelFont.outline.copy(alpha = fade),
        )
    }
}

private fun DrawScope.drawParticle(particle: Particle) {
    val fade = (particle.life / particle.maxLife).coerceIn(0f, 1f)
    val color = particle.color.copy(alpha = particle.color.alpha * fade)
    val x = particle.x.roundToInt()
    val y = particle.y.roundToInt()
    when (particle.shape) {
        ParticleShape.Square -> pixelRect(x, y, particle.size, particle.size, color)
        ParticleShape.Sparkle -> {
            pixel(x, y, color)
            if (fade > 0.45f) {
                pixel(x - 1, y, color)
                pixel(x + 1, y, color)
                pixel(x, y - 1, color)
                pixel(x, y + 1, color)
            }
        }
    }
}
