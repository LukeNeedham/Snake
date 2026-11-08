package com.lukeneedham.snake.game

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import kotlin.math.max
import kotlin.random.Random

/**
 * One game: the phase it is in, the snake and the animals. The parts with a job of their own are classes of their own
 * ([EffectTracker], [CritterSpawner], [Feeding], [ParticleSystem]); this one only steps them in order. It is mutable and is
 * stepped with [update] once per frame; [tick] changes with every step so that a drawing that reads it is drawn again.
 */
class GameWorld(val arena: Arena, seed: Int = 1) {

    /** Read it while drawing to be drawn again after every step. */
    var tick by mutableLongStateOf(0L)
        private set

    var phase = GamePhase.Ready
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

    /** Counts down after a tap that the confused snake ignored. */
    var rejectedTapTime = 0f
        private set

    val snake = Snake(arena)
    val critters = ArrayList<Critter>()
    val effects = EffectTracker()

    private val random = Random(seed)
    val particleSystem = ParticleSystem(random)
    private val spawner = CritterSpawner(arena, random)
    private val feeding = Feeding(arena)
    private val effectParticles = EffectParticleEmitter(particleSystem, random)
    private var seenBounces = 0

    val canRestart: Boolean get() = phase == GamePhase.GameOver && gameOverTime > restartDelay

    fun start() {
        if (phase != GamePhase.Ready) return
        phase = GamePhase.Playing
        spawner.reset()
    }

    fun pause() {
        if (phase == GamePhase.Playing) phase = GamePhase.Paused
    }

    fun resume() {
        if (phase == GamePhase.Paused) phase = GamePhase.Playing
    }

    /** The one control of the game: turn the other way. A confused snake ignores it. */
    fun tap() {
        if (phase != GamePhase.Playing) return
        if (effects.isOn(Effect.Confused)) {
            rejectedTapTime = rejectedTapSeconds
            return
        }
        snake.toggleTurn()
    }

    fun applyEffect(kind: ToadKind) = effects.apply(kind)

    fun endGame() {
        if (phase != GamePhase.Playing) return
        phase = GamePhase.GameOver
        gameOverTime = 0f
        shake = startingShake
        particleSystem.explode(snake.x, snake.y, listOf(Color(0xFFE8465A), Color(0xFF52C27D), Color(0xFFF4D35E)))
    }

    fun update(dt: Float) {
        if (phase == GamePhase.Paused) return
        time += dt
        effects.update(dt, isCountingDown = phase == GamePhase.Playing, isShowing = phase != GamePhase.GameOver)
        when (phase) {
            GamePhase.Ready -> snake.step(dt, readySpeed)
            GamePhase.Playing -> updatePlaying(dt)
            GamePhase.GameOver -> {
                gameOverTime += dt
                shake = max(0f, shake - dt * shakeDecay)
            }
            GamePhase.Paused -> Unit
        }
        particleSystem.update(dt)
        rejectedTapTime = max(0f, rejectedTapTime - dt)
        tick++
    }

    private fun updatePlaying(dt: Float) {
        snake.step(dt, 1f + SnakeTuning.speedyBoost * effects.level(Effect.Speedy))
        dustAtNewBounces()
        spawner.update(dt, critters, score)
        updateCritters(dt)
        feeding.update(dt, snake, critters).forEach { eat(it) }
        effectParticles.emit(effects, snake, critters)
        if (snake.hitsItself()) endGame()
    }

    private fun dustAtNewBounces() {
        if (snake.bounceCount == seenBounces) return
        seenBounces = snake.bounceCount
        particleSystem.dust(snake.lastBounceX, snake.lastBounceY, 6, dustColor)
    }

    private fun updateCritters(dt: Float) {
        for (critter in critters) {
            critter.update(dt, arena, random)
            if (critter.landed) {
                critter.landed = false
                particleSystem.dust(critter.x, critter.y, 3, dustColor)
            }
            if (critter is Critter.Mouse && critter.isMoving && random.nextFloat() < runDustChance) {
                particleSystem.dust(critter.x, critter.y, 1, dustColor)
            }
        }
        critters.removeAll { it.isGone }
    }

    private fun eat(critter: Critter) {
        when (critter) {
            is Critter.Mouse -> {
                score++
                snake.grow(1)
                snake.addBulge()
                particleSystem.floatText("+1", snake.x, snake.y - 6f, Color(0xFFFFF3B0))
                particleSystem.sparkles(
                    critter.x, critter.y - 3f, 9,
                    listOf(Color(0xFFFFFFFF), Color(0xFFF28FA9), Color(0xFFFFF3B0)),
                )
            }
            is Critter.Toad -> {
                effects.apply(critter.kind)
                particleSystem.sparkles(
                    critter.x, critter.y - 3f, 12,
                    listOf(critter.kind.body, critter.kind.light, Color(0xFFFFFFFF)),
                )
            }
        }
    }

    private companion object {
        /** How long after the game ends a tap starts a new one, so that a frantic tap does not skip the result. */
        const val restartDelay = 0.8f
        const val startingShake = 3f
        const val shakeDecay = 5f
        const val rejectedTapSeconds = 0.4f

        /** The snake on the title screen circles slowly. */
        const val readySpeed = 0.6f
        const val runDustChance = 0.12f
        val dustColor = Color(0xFFE6D3A0)
    }
}
