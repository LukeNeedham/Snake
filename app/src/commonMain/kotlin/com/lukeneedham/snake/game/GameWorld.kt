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
 * stepped with [update] once per frame; [tick] changes with every step so that a drawing that reads it is drawn again. A world that is not
 * opened in the [Opening.Oval] starts with the snake asleep in its den, under the oval: the first tap wakes it, and it hands over to
 * [GamePhase.Playing] once the player has steered the snake out of the den and along the path into the oval. With the [Opening.Tutorial]
 * the story and the lessons come first, along a path that goes on and on until they are learned. After that the snake can go anywhere on
 * the oval, the path and the den, and in a world with a door the snake can open it to be taught everything again.
 */
class GameWorld(val arena: Arena, seed: Int = 1, val opening: Opening = Opening.Oval) {

    private val hasIntro = opening != Opening.Oval

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

    val den = DenLayout(arena, isLong = opening == Opening.Tutorial)

    /** The story and the lessons at the start, or null in a world that has none (the snake wakes and goes, without a tutorial, in a short path). */
    val intro: IntroDirector? = if (hasIntro) IntroDirector(isTutorial = opening == Opening.Tutorial) else null

    /** Set once the player has been through the whole tutorial, and the snake is out in the oval: the page saves it. */
    var hasCompletedTutorial = false
        private set

    /** How far open the door of the tutorial is, from 0 to 1: the snake opens it by staying close to it. */
    var doorOpen = 0f
        private set

    /** Set when the snake has opened the door: the page starts the tutorial again. */
    var wantsTutorial = false
        private set

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
    private val feeding = Feeding(den.ground)
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
        if (effects.isOn(Effect.Confused)) {
            rejectedTapTime = rejectedTapSeconds
            return
        }
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
        val isPlayed = phase == GamePhase.Playing || phase == GamePhase.Intro
        effects.update(dt, isCountingDown = isPlayed, isShowing = phase != GamePhase.GameOver)
        powerUps.update(dt, isCountingDown = isPlayed)
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
        if (director.stage != stageBefore) onStageEntered(director.stage)
        val distanceBefore = snake.totalDistance
        stepSnake(dt, director.snakeSpeed * (1f + boosts.sprintBoost * sprint.level) * boosts.speedFactor)
        director.walked(snake.totalDistance - distanceBefore)
        if (snake.y < den.halfwayY) director.halfway()
        director.trackSprint(sprint.isHeld, dt)
        if (director.stage.isLesson) updateLesson(dt, director)
        // While the lessons last the path goes on and on: the snake is put back a stretch, with the camera and the animals, and nobody can tell
        if (director.loopsPath && snake.y < den.loopWrapY) {
            snake.shift(den.loopPeriod)
            cameraY += den.loopPeriod
            for (critter in mutableCritters) critter.shiftDown(den.loopPeriod)
        }
        followSnake(dt)
        if (arena.signedDistance(snake.x, snake.y) < -DenTuning.exitDepth) finishIntro()
    }

    /** One step of the lessons after the sprint: the hunger bar, and the animals that the snake has to eat to go on. */
    private fun updateLesson(dt: Float, director: IntroDirector) {
        if (director.stage == IntroStage.Hunger) hunger.drainDemo(dt, IntroTuning.hungerDemoLevel) else hunger.settle(dt)
        director.hungerShown(hunger.level)
        val snakeHead = Vec2(snake.x, snake.y)
        for (critter in critters) critter.update(dt, arena, random, snakeHead, boosts.aura)
        feeding.update(dt, snake, mutableCritters, boosts.mouthFactor, boosts.mouthIsFull).forEach {
            eat(it)
            director.ate(it)
        }
        director.dizzy(effects.isOn(Effect.Confused))
        // An animal that the snake went past is put ahead of it again
        val stage = director.stage
        if (stage == IntroStage.Mouse || stage == IntroStage.Toad || stage == IntroStage.Bird) {
            val animal = critters.firstOrNull()
            if (animal == null || animal.y > snake.y + IntroTuning.lessonBehind) placeLessonAnimal(stage)
        }
    }

    /** Something happens when a part of the story begins: the bubble pops, and the animal of a lesson shows. */
    private fun onStageEntered(stage: IntroStage) {
        if (stage == IntroStage.Popped) popBubble()
        if (stage == IntroStage.Mouse || stage == IntroStage.Toad || stage == IntroStage.Bird) placeLessonAnimal(stage)
    }

    /** Puts the animal that the lesson of [stage] is about on the path, ahead of the snake: it does not run, hop or fly off. The bird flies in. */
    private fun placeLessonAnimal(stage: IntroStage) {
        mutableCritters.clear()
        val x = arena.centerX + (random.nextFloat() - 0.5f) * lessonSpread
        val y = snake.y - IntroTuning.lessonAhead
        mutableCritters.add(
            when (stage) {
                IntroStage.Mouse -> Critter.Mouse(lessonAnimalId, x, y, lifetime = lessonLifetime, isTame = true)
                IntroStage.Toad -> Critter.Toad(lessonAnimalId, ToadKind.Pink, x, y, lifetime = lessonLifetime, isTame = true)
                else -> Critter.Bird(lessonAnimalId, BirdKind.BlueTit, x + 25f, y - 45f, x, y, staySeconds = lessonLifetime, isTame = true)
            },
        )
    }

    /** Puts the snake on the path in the lesson [stage], as if the player had got that far: for the tests and the screenshots. */
    internal fun jumpToLesson(stage: IntroStage) {
        val director = intro ?: return
        phase = GamePhase.Intro
        director.jumpTo(stage)
        snake = den.snakeOnPath(den.loopWrapY + 40f)
        cameraY = (snake.y - arena.height * DenTuning.followLine).coerceIn(0f, den.denCameraY)
        onStageEntered(stage)
    }

    /** Steps the snake, unless it lies still. */
    private fun stepSnake(dt: Float, speed: Float) {
        if (speed > 0f) snake.step(dt, speed)
    }

    private fun popBubble() {
        val bubble = den.bubbles.last()
        particleSystem.sparkles(bubble.x, bubble.y, 18, listOf(Color(0xFFFFFFFF), Color(0xFFBFE3FF), Color(0xFFF28FA9)))
    }

    /** The snake is in the oval: from here on it is the game. After the whole tutorial the score starts again from nothing. */
    private fun finishIntro() {
        phase = GamePhase.Playing
        if (opening == Opening.Tutorial) {
            hasCompletedTutorial = true
            score = 0
            mutableCritters.clear()
        }
        spawner.reset()
    }

    /** The snake opens the door in the wall of the den by staying near it; once it is open the page starts the tutorial again. */
    private fun updateDoor(dt: Float) {
        if (!den.hasDoor) return
        val dx = snake.x - den.doorX
        val dy = snake.y - den.doorY
        val isAtDoor = dx * dx + dy * dy < DenTuning.doorReach * DenTuning.doorReach
        doorOpen = (doorOpen + if (isAtDoor) dt / DenTuning.doorOpenSeconds else -dt / DenTuning.doorCloseSeconds).coerceIn(0f, 1f)
        if (doorOpen >= 1f) wantsTutorial = true
    }

    /** The screen stays on the oval, and follows the snake once it leaves the oval for the path, to the den and back. */
    private fun followSnake(dt: Float) {
        val target = if (snake.y > den.mouthY) (snake.y - arena.height * DenTuning.followLine).coerceIn(0f, den.denCameraY) else 0f
        cameraY += (target - cameraY) * min(1f, dt * DenTuning.cameraEase)
        if (abs(target - cameraY) < cameraSettleDistance) cameraY = target
    }

    private fun updatePlaying(dt: Float) {
        followSnake(dt)
        updateDoor(dt)
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

        /** The animals of the lessons: how far to the side of the middle of the path they sit at the most, their id, and how long they stay. */
        const val lessonSpread = 14f
        const val lessonAnimalId = 900
        const val lessonLifetime = 100000f

        /** The camera is close enough to where it is going to be there. */
        const val cameraSettleDistance = 0.05f

        /** The snake on the title screen circles slowly. */
        const val readySpeed = 0.6f
        const val runDustChance = 0.12f
        val dustColor = Color(0xFFE6D3A0)
    }
}
