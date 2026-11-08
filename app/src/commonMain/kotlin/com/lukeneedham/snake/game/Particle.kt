package com.lukeneedham.snake.game

import androidx.compose.ui.graphics.Color

enum class ParticleShape { SQUARE, SPARKLE }

/** A speck of dust, a sparkle or a bubble. It fades out over [maxLife] seconds. */
class Particle(
    var x: Float,
    var y: Float,
    private var vx: Float,
    private var vy: Float,
    val maxLife: Float,
    val color: Color,
    val size: Int,
    private val gravity: Float,
    val shape: ParticleShape,
) {
    var life = maxLife
        private set

    fun update(dt: Float) {
        life -= dt
        vy += gravity * dt
        x += vx * dt
        y += vy * dt
    }
}
