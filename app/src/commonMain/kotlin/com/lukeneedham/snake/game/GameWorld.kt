package com.lukeneedham.snake.game

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

/**
 * Everything that lives in one game: the snake, the animals, the effects and the specks of dust. It is mutable and is stepped
 * with [update] once per frame; [tick] changes with every step so that a drawing that reads it is drawn again.
 */
class GameWorld(val arena: Arena, seed: Int = 1) {

    /** Read it while drawing to be drawn again after every step. */
    var tick by mutableLongStateOf(0L)
        private set

    var phase = GamePhase.READY
        private set

    var score = 0
        internal set

    /** Seconds of animation, which stops while paused. */
    var time = 0f
        private set

    var gameOverTime = 0f
        private set

    /** How hard the screen shakes after the snake hit itself. */
    var shake = 0f
        private set

    val snake = Snake(arena)
    val critters = ArrayList<Critter>()
    val particles = ArrayList<Particle>()
    val floatingTexts = ArrayList<FloatingText>()

    /** The toad whose poison was eaten last, shown as a banner for a moment. */
    var bannerKind: ToadKind? = null
        private set
    var bannerTime = 0f
        private set

    /** Counts down after a tap that the confused snake ignored. */
    var rejectedTapTime = 0f
        private set

    val canRestart: Boolean get() = phase == GamePhase.GAME_OVER && gameOverTime > 0.8f

    private val random = Random(seed)
    private val remaining = FloatArray(Effect.entries.size)
    private val levels = FloatArray(Effect.entries.size)
    private var nextCritterId = 1
    private var mouseTimer = 0f
    private var toadTimer = 0f
    private var seenBounces = 0

    /** 0 to 1: how strongly the effect shows right now, so that it fades in and out. */
    fun effectLevel(effect: Effect): Float = levels[effect.ordinal]

    fun effectRemaining(effect: Effect): Float = remaining[effect.ordinal]

    fun start() {
        if (phase != GamePhase.READY) return
        phase = GamePhase.PLAYING
        mouseTimer = 0.5f
        toadTimer = 7f
    }

    fun pause() {
        if (phase == GamePhase.PLAYING) phase = GamePhase.PAUSED
    }

    fun resume() {
        if (phase == GamePhase.PAUSED) phase = GamePhase.PLAYING
    }

    /** The one control of the game: turn the other way. A confused snake ignores it. */
    fun tap() {
        if (phase != GamePhase.PLAYING) return
        if (remaining[Effect.CONFUSED.ordinal] > 0f) {
            rejectedTapTime = 0.4f
            return
        }
        snake.toggleTurn()
    }

    fun applyEffect(kind: ToadKind) {
        remaining[kind.effect.ordinal] = kind.effect.durationSeconds
        bannerKind = kind
        bannerTime = 2f
    }

    fun endGame() {
        if (phase != GamePhase.PLAYING) return
        phase = GamePhase.GAME_OVER
        gameOverTime = 0f
        shake = 3f
        val colors = listOf(Color(0xFFE8465A), Color(0xFF52C27D), Color(0xFFF4D35E))
        repeat(24) {
            val angle = random.nextFloat() * 2f * PI.toFloat()
            val speed = 10f + random.nextFloat() * 28f
            particles.add(
                Particle(
                    snake.x, snake.y, cos(angle) * speed, sin(angle) * speed, 0.9f,
                    colors[random.nextInt(colors.size)], 2, 30f, ParticleShape.SQUARE,
                ),
            )
        }
    }

    fun update(dt: Float) {
        if (phase == GamePhase.PAUSED) return
        time += dt
        updateEffects(dt)
        when (phase) {
            GamePhase.READY -> snake.step(dt, 0.6f)
            GamePhase.PLAYING -> updatePlaying(dt)
            GamePhase.GAME_OVER -> {
                gameOverTime += dt
                shake = max(0f, shake - dt * 5f)
            }
            GamePhase.PAUSED -> Unit
        }
        updateParticles(dt)
        bannerTime = max(0f, bannerTime - dt)
        rejectedTapTime = max(0f, rejectedTapTime - dt)
        tick++
    }

    private fun updateEffects(dt: Float) {
        for (effect in Effect.entries) {
            val i = effect.ordinal
            if (phase == GamePhase.PLAYING) remaining[i] = max(0f, remaining[i] - dt)
            val target = if (remaining[i] > 0f && phase != GamePhase.GAME_OVER) 1f else 0f
            levels[i] += (target - levels[i]).coerceIn(-dt * 2.5f, dt * 2.5f)
        }
    }

    private fun updatePlaying(dt: Float) {
        val speed = 1f + GameTuning.SPEEDY_FACTOR * levels[Effect.SPEEDY.ordinal]
        snake.step(dt, speed)
        if (snake.bounceCount != seenBounces) {
            seenBounces = snake.bounceCount
            dust(snake.lastBounceX, snake.lastBounceY, 6, Color(0xFFE6D3A0))
        }
        spawnCritters(dt)
        for (critter in critters) {
            critter.update(dt, arena, random)
            if (critter.landed) {
                critter.landed = false
                dust(critter.x, critter.y, 3, Color(0xFFE6D3A0))
            }
            if (critter.type == CritterType.MOUSE && critter.isMoving && random.nextFloat() < 0.12f) {
                dust(critter.x, critter.y, 1, Color(0xFFE6D3A0))
            }
        }
        critters.removeAll { it.isGone }
        checkEating(dt)
        emitEffectParticles()
        if (snake.hitsItself()) endGame()
    }

    private fun spawnCritters(dt: Float) {
        mouseTimer -= dt
        toadTimer -= dt
        val mice = critters.count { it.type == CritterType.MOUSE }
        val toads = critters.count { it.type == CritterType.TOAD }
        if (mice < min(2 + score / 5, 5) && mouseTimer <= 0f) {
            spawnCritter(CritterType.MOUSE)
            mouseTimer = 1f + random.nextFloat() * 1.2f
        }
        if (toads < min(1 + score / 6, 3) && toadTimer <= 0f) {
            spawnCritter(CritterType.TOAD)
            toadTimer = 6f + random.nextFloat() * 4f
        }
    }

    private fun spawnCritter(type: CritterType) {
        val angle = random.nextFloat() * 2f * PI.toFloat()
        val reach = GameTuning.GRASS_DEPTH * 0.75f
        val x = arena.centerX + (arena.radiusX + reach) * cos(angle)
        val y = arena.centerY + (arena.radiusY + reach) * sin(angle)
        val kind = if (type == CritterType.TOAD) ToadKind.entries[random.nextInt(ToadKind.entries.size)] else null
        critters.add(Critter(nextCritterId++, type, kind, x, y, lifetime = 14f + random.nextFloat() * 8f))
    }

    private fun checkEating(dt: Float) {
        val mouthX = snake.x + cos(snake.heading) * (GameTuning.HEAD_RADIUS - 0.2f)
        val mouthY = snake.y + sin(snake.heading) * (GameTuning.HEAD_RADIUS - 0.2f)
        val eatLimit = GameTuning.EAT_RADIUS * GameTuning.EAT_RADIUS
        var nearest = Float.MAX_VALUE
        for (i in critters.indices.reversed()) {
            val critter = critters[i]
            if (arena.signedDistance(critter.x, critter.y) > -1f) continue
            val dx = critter.x - mouthX
            val dy = critter.y - 3f - mouthY
            val distance = dx * dx + dy * dy
            if (distance < eatLimit) {
                eat(critter)
                critters.removeAt(i)
            } else {
                nearest = min(nearest, distance)
            }
        }
        val target = if (nearest < 12f * 12f) 1f else 0f
        snake.mouthOpen += (target - snake.mouthOpen) * min(1f, dt * 14f)
    }

    private fun eat(critter: Critter) {
        when (critter.type) {
            CritterType.MOUSE -> {
                score++
                snake.grow(1)
                snake.addBulge()
                floatingTexts.add(FloatingText("+1", snake.x, snake.y - 6f, Color(0xFFFFF3B0)))
                sparkles(critter.x, critter.y - 3f, 9, listOf(Color(0xFFFFFFFF), Color(0xFFF28FA9), Color(0xFFFFF3B0)))
            }
            CritterType.TOAD -> {
                val kind = critter.toadKind ?: ToadKind.PURPLE
                applyEffect(kind)
                sparkles(critter.x, critter.y - 3f, 12, listOf(kind.body, kind.light, Color(0xFFFFFFFF)))
            }
        }
    }

    private fun emitEffectParticles() {
        if (levels[Effect.SPEEDY.ordinal] > 0.3f && random.nextFloat() < 0.5f) {
            val tail = snake.body.getOrNull(3) ?: return
            particles.add(
                Particle(
                    tail.x + (random.nextFloat() - 0.5f) * 4f, tail.y + (random.nextFloat() - 0.5f) * 4f,
                    0f, 0f, 0.4f, Color(0xFFFFB35C), 1, 0f, ParticleShape.SQUARE,
                ),
            )
        }
        if (levels[Effect.DISGUISED.ordinal] > 0.3f && random.nextFloat() < 0.1f && critters.isNotEmpty()) {
            val critter = critters[random.nextInt(critters.size)]
            sparkles(critter.x, critter.y - 6f, 1, listOf(Color(0xFFFFFFFF), Color(0xFFA6EDE2)))
        }
    }

    private fun dust(x: Float, y: Float, count: Int, color: Color) {
        repeat(count) {
            val angle = random.nextFloat() * 2f * PI.toFloat()
            val speed = 3f + random.nextFloat() * 8f
            particles.add(
                Particle(x, y, cos(angle) * speed, sin(angle) * speed * 0.5f - 3f, 0.5f, color, 1, 8f, ParticleShape.SQUARE),
            )
        }
    }

    private fun sparkles(x: Float, y: Float, count: Int, colors: List<Color>) {
        repeat(count) {
            val angle = random.nextFloat() * 2f * PI.toFloat()
            val speed = 6f + random.nextFloat() * 18f
            particles.add(
                Particle(
                    x, y, cos(angle) * speed, sin(angle) * speed - 8f, 0.6f + random.nextFloat() * 0.4f,
                    colors[random.nextInt(colors.size)], 1, 18f, ParticleShape.SPARKLE,
                ),
            )
        }
    }

    private fun updateParticles(dt: Float) {
        for (particle in particles) particle.update(dt)
        particles.removeAll { it.life <= 0f }
        for (text in floatingTexts) text.update(dt)
        floatingTexts.removeAll { it.life <= 0f }
        if (particles.size > 300) particles.subList(0, particles.size - 300).clear()
    }
}
