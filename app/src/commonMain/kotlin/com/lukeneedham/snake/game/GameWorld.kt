package com.lukeneedham.snake.game

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

/**
 * One game: the phase it is in, the snake and the animals. The parts with a job of their own are classes of their own
 * ([EffectTracker], [PowerUpTracker], [CritterSpawner], [Feeding], [ToadBlast], [ParticleSystem], [Hunger], [SprintControl]); this one only steps them in order. It is mutable and is
 * stepped with [update] once per frame; [tick] changes with every step so that a drawing that reads it is drawn again. A world with an
 * intro starts with the snake asleep in its den, under the oval: the first tap wakes it and plays the story, which hands over to
 * [GamePhase.Playing] once the player has steered the snake out of the den and along the path into the oval. After that the snake can
 * go anywhere on the oval, the path and the den.
 */
class GameWorld(val arena: Arena, seed: Int = 1, hasIntro: Boolean = false) {

    /** Read it while drawing to be drawn again after every step. */
    var tick by mutableLongStateOf(0L)
        private set

    var phase = GamePhase.Ready
        private set

    /** The phase a paused game goes back to when it is resumed. */
    private var phaseBeforePause = GamePhase.Playing

    /** Whether the story at the start is on, even if the menu paused it. */
    val isIntro: Boolean get() = phase == GamePhase.Intro || (phase == GamePhase.Paused && phaseBeforePause == GamePhase.Intro)

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

    val den = DenLayout(arena)

    /** The story at the start, or null in a world that has none. */
    val intro: IntroDirector? = if (hasIntro) IntroDirector() else null

    var snake: Snake = if (hasIntro) den.curledSnake() else Snake(arena, den.ground)
        private set

    /** The world y at the top of the screen: the screen shows the oval, unless the snake is on the path and the camera follows it. */
    var cameraY = if (hasIntro) den.denCameraY else 0f
        private set

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
        if (intro != null) {
            phase = GamePhase.Intro
            intro.wake()
        } else {
            phase = GamePhase.Playing
            spawner.reset()
        }
    }

    /** Pausing opens the menu: the game (or the story) carries on when it is closed. */
    fun pause() {
        if (phase != GamePhase.Playing && phase != GamePhase.Intro) return
        phaseBeforePause = phase
        phase = GamePhase.Paused
        isMenuOpen = true
        sprint.clear()
        redraw()
    }

    fun resume() {
        if (phase != GamePhase.Paused) return
        phase = phaseBeforePause
        isMenuOpen = false
        redraw()
    }

    /** Opens the menu: a game that is being played is paused, the title and game over screens just show it over them. */
    fun openMenu() {
        if (phase == GamePhase.Playing || phase == GamePhase.Intro) {
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
        if (phase == GamePhase.Intro) {
            tapInIntro()
            return
        }
        if (phase != GamePhase.Playing) return
        if (effects.isOn(Effect.Confused)) {
            rejectedTapTime = rejectedTapSeconds
            return
        }
        snake.toggleTurn()
    }

    /** During the story a tap does nothing; once the lesson is on it turns the snake, and that is what it was there to teach. */
    private fun tapInIntro() {
        val director = intro ?: return
        if (director.stage.isStory) return
        snake.toggleTurn()
        director.turned()
    }

    /**
     * A finger went down during the story. In the sprint zone it sprints, once the lesson of the sprint has begun, and a finger
     * anywhere else (and in the zone before that) is a tap.
     */
    fun introPress(pointerId: Long, isInSprintZone: Boolean) {
        if (phase != GamePhase.Intro) return
        val director = intro ?: return
        if (isInSprintZone && director.reachedHalfway) sprint.press(pointerId) else tap()
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
        sprint.update(dt, canSprint = phase == GamePhase.Playing || phase == GamePhase.Intro)
        effects.update(dt, isCountingDown = phase == GamePhase.Playing, isShowing = phase != GamePhase.GameOver)
        powerUps.update(dt, isCountingDown = phase == GamePhase.Playing)
        when (phase) {
            GamePhase.Ready -> stepSnake(dt, intro?.snakeSpeed ?: readySpeed)
            GamePhase.Intro -> updateIntro(dt)
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

    private fun updateIntro(dt: Float) {
        val director = intro ?: return
        val stageBefore = director.stage
        director.update(dt)
        if (stageBefore != IntroStage.Popped && director.stage == IntroStage.Popped) popBubble()
        stepSnake(dt, director.snakeSpeed * (1f + boosts.sprintBoost * sprint.level))
        if (snake.y < den.halfwayY) director.halfway()
        director.trackSprint(sprint.isHeld, dt)
        // Until the player has learned to sprint the path goes on and on: the snake is put back a stretch, with the camera, and nobody can tell
        if (!director.hasZoomed && snake.y < den.loopWrapY) {
            snake.shift(den.loopPeriod)
            cameraY += den.loopPeriod
        }
        followSnake(dt)
        if (arena.signedDistance(snake.x, snake.y) < -DenTuning.exitDepth) finishIntro()
    }

    /** Steps the snake, unless it lies still. */
    private fun stepSnake(dt: Float, speed: Float) {
        if (speed > 0f) snake.step(dt, speed)
    }

    private fun popBubble() {
        val bubble = den.bubbles.last()
        particleSystem.sparkles(bubble.x, bubble.y, 18, listOf(Color(0xFFFFFFFF), Color(0xFFBFE3FF), Color(0xFFF28FA9)))
    }

    /** The snake is in the oval: from here on it is the game. */
    private fun finishIntro() {
        phase = GamePhase.Playing
        spawner.reset()
    }

    /** The screen stays on the oval, and follows the snake once it leaves the oval for the path, to the den and back. */
    private fun followSnake(dt: Float) {
        val target = if (snake.y > den.mouthY) (snake.y - arena.height * DenTuning.followLine).coerceIn(0f, den.denCameraY) else 0f
        cameraY += (target - cameraY) * min(1f, dt * DenTuning.cameraEase)
        if (abs(target - cameraY) < cameraSettleDistance) cameraY = target
    }

    private fun updatePlaying(dt: Float) {
        followSnake(dt)
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

        /** The camera is close enough to where it is going to be there. */
        const val cameraSettleDistance = 0.05f

        /** The snake on the title screen circles slowly. */
        const val readySpeed = 0.6f
        const val runDustChance = 0.12f
        val dustColor = Color(0xFFE6D3A0)
    }
}
