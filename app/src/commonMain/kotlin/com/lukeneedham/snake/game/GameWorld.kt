package com.lukeneedham.snake.game

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import kotlin.math.max
import kotlin.random.Random

/**
 * One game: the phase it is in, the snake and the animals. The parts with a job of their own are classes of their own
 * ([EffectTracker], [PowerUpTracker], [CritterSpawner], [Feeding], [ToadBlast], [ParticleSystem], [Hunger], [SprintControl]); this one only steps them in order. It is mutable and is
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

    /** Whether the menu is over the scene. A paused game always has it open. */
    var isMenuOpen = false
        private set

    /** The corner of the screen that sprints: it comes from the settings. */
    var sprintSide = SprintSide.Left

    /** The buttons of the menu: restarting only makes sense in a game that is paused. */
    val menuItems: List<MenuItem>
        get() = if (phase == GamePhase.Paused) listOf(MenuItem.Resume, MenuItem.Restart, MenuItem.Settings) else listOf(MenuItem.Resume, MenuItem.Settings)

    val snake = Snake(arena)
    private val mutableCritters = ArrayList<Critter>()

    /** The animals that are out of the grass, in the oval or on their way. */
    val critters: List<Critter> get() = mutableCritters
    val effects = EffectTracker()
    val powerUps = PowerUpTracker()
    private val boosts = PowerUpBoosts(powerUps)
    val hunger = Hunger()
    val sprint = SprintControl()

    private val random = Random(seed)
    val particleSystem = ParticleSystem(random)
    private val spawner = CritterSpawner(arena, random)
    private val feeding = Feeding(arena)
    private val toadBlast = ToadBlast(particleSystem)
    private val effectParticles = EffectParticleEmitter(particleSystem, random)
    private var seenBounces = 0

    val canRestart: Boolean get() = phase == GamePhase.GameOver && gameOverTime > restartDelay

    fun start() {
        if (phase != GamePhase.Ready) return
        phase = GamePhase.Playing
        spawner.reset()
    }

    /** Pausing opens the menu: the game carries on when it is closed. */
    fun pause() {
        if (phase != GamePhase.Playing) return
        phase = GamePhase.Paused
        isMenuOpen = true
        sprint.clear()
        redraw()
    }

    fun resume() {
        if (phase != GamePhase.Paused) return
        phase = GamePhase.Playing
        isMenuOpen = false
        redraw()
    }

    /** Opens the menu: a game that is being played is paused, the title and game over screens just show it over them. */
    fun openMenu() {
        if (phase == GamePhase.Playing) {
            pause()
        } else if (phase != GamePhase.Paused) {
            isMenuOpen = true
            redraw()
        }
    }

    fun closeMenu() {
        if (phase == GamePhase.Paused) {
            resume()
        } else {
            isMenuOpen = false
            redraw()
        }
    }

    /** A paused world is not stepped, so the drawing is told by hand to draw again when the menu opens or closes. */
    private fun redraw() {
        tick++
    }

    /** Turn the other way: a tap anywhere but in the [SprintZone]. A confused snake ignores it. */
    fun tap() {
        if (phase != GamePhase.Playing) return
        if (effects.isOn(Effect.Confused)) {
            rejectedTapTime = rejectedTapSeconds
            return
        }
        snake.toggleTurn()
    }

    /** A finger went down in the [SprintZone]: the snake sprints for as long as it is held. It never turns the snake. */
    fun sprintPress(pointerId: Long) {
        if (phase == GamePhase.Playing) sprint.press(pointerId)
    }

    fun sprintRelease(pointerId: Long) = sprint.release(pointerId)

    fun applyEffect(kind: ToadKind) = effects.apply(kind)

    /** Puts a critter in the world: the tests use it to place one where they need it. */
    internal fun addCritter(critter: Critter) {
        mutableCritters.add(critter)
    }

    fun endGame() {
        if (phase != GamePhase.Playing) return
        phase = GamePhase.GameOver
        sprint.clear()
        gameOverTime = 0f
        shake = startingShake
        particleSystem.explode(snake.x, snake.y, listOf(Color(0xFFE8465A), Color(0xFF52C27D), Color(0xFFF4D35E)))
    }

    fun update(dt: Float) {
        if (phase == GamePhase.Paused) return
        time += dt
        sprint.update(dt, canSprint = phase == GamePhase.Playing)
        effects.update(dt, isCountingDown = phase == GamePhase.Playing, isShowing = phase != GamePhase.GameOver)
        powerUps.update(dt, isCountingDown = phase == GamePhase.Playing)
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
        snake.step(dt, (1f + boosts.sprintBoost * sprint.level) * boosts.speedFactor)
        hunger.update(dt, sprint.level, boosts.sprintDrainFactor)
        dustAtNewBounces()
        spawner.update(dt, mutableCritters, score, Vec2(snake.x, snake.y), boosts.attractsMice, allowsToads = !boosts.keepsToadsAway)
        updateCritters(dt)
        feeding.update(dt, snake, mutableCritters, boosts.mouthFactor, boosts.mouthIsFull).forEach { eat(it) }
        effectParticles.emit(effects, sprint.level, snake, critters)
        if (hunger.isStarving || (effects.isOn(Effect.Lupus) && snake.hitsItself())) endGame()
    }

    private fun dustAtNewBounces() {
        if (snake.bounceCount == seenBounces) return
        seenBounces = snake.bounceCount
        particleSystem.dust(snake.lastBounceX, snake.lastBounceY, 6, dustColor)
    }

    private fun updateCritters(dt: Float) {
        val snakeHead = Vec2(snake.x, snake.y)
        val aura = boosts.aura
        for (critter in critters) {
            critter.update(dt, arena, random, snakeHead, aura)
            if (critter.landed) {
                critter.landed = false
                particleSystem.dust(critter.x, critter.y, 3, dustColor)
            }
            if (critter is Critter.Mouse && critter.isMoving && random.nextFloat() < runDustChance) {
                particleSystem.dust(critter.x, critter.y, 1, dustColor)
            }
        }
        mutableCritters.removeAll { it.isGone }
    }

    private fun eat(critter: Critter) {
        when (critter) {
            is Critter.Mouse -> {
                score += ScoreTuning.mouse
                hunger.feed(HungerTuning.mouseFills)
                snake.grow(1)
                snake.addBulge()
                particleSystem.floatText("+${ScoreTuning.mouse}", snake.x, snake.y - 6f, Color(0xFFFFF3B0))
                particleSystem.sparkles(
                    critter.x, critter.y - 3f, 9,
                    listOf(Color(0xFFFFFFFF), Color(0xFFF28FA9), Color(0xFFFFF3B0)),
                )
            }
            is Critter.Bird -> {
                score += ScoreTuning.bird
                hunger.feed(HungerTuning.birdFills)
                powerUps.apply(critter.kind)
                if (critter.kind.powerUp == PowerUp.ToadFree) toadBlast.blowUp(mutableCritters)
                snake.grow(1)
                snake.addBulge()
                particleSystem.floatText("+${ScoreTuning.bird}", snake.x, snake.y - 6f, Color(0xFFFFF3B0))
                particleSystem.sparkles(
                    critter.x, critter.y - 3f, 14,
                    listOf(Color(0xFFFFFFFF), Color(0xFFF4D35E), Color(0xFFA6EDE2)),
                )
            }
            is Critter.Toad -> {
                score += ScoreTuning.toad
                effects.apply(critter.kind)
                hunger.feed(HungerTuning.toadFills)
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
