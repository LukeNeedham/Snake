package com.lukeneedham.snake.game

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

/**
 * One game: the phase it is in, the snake and the animals. The parts with a job of their own are classes of their own
 * ([EffectTracker], [PowerUpTracker], [CritterSpawner], [Feeding], [ToadBlast], [ParticleSystem], [Hunger], [SprintControl]); this one only steps them in order. It is mutable and is
 * stepped with [update] once per frame; [tick] changes with every step so that a drawing that reads it is drawn again. A world that is not
 * opened in the [Opening.Oval] starts with the snake asleep in its house, a screen under the stage: the first tap wakes it, and it hands over to
 * [GamePhase.Playing] once the player has steered the snake out of the house and up the path into the oval. With the [Opening.Tutorial]
 * the path up is blocked by stones, and the snake has to go to the room of the tutorial, to the left, where the lessons are, and come back.
 * The snake can go anywhere on the map ([WorldLayout]); going into the room of the tutorial again teaches the lessons again.
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

    /** What the snake died of, once the game is over. */
    var deathCause: DeathCause? = null
        private set

    /** How hard the screen shakes after the snake hit itself. */
    var shake = 0f
        private set

    /** Counts down after a tap that the confused snake ignored. */
    var rejectedTapTime = 0f
        private set

    /** The pause overlay, which all the menus are pages of. A paused game always has it open. */
    val overlay = PauseOverlay()

    /** Whether the pause overlay is over the scene. */
    val isMenuOpen: Boolean get() = overlay.isOpen

    /** The corner of the screen that sprints: it comes from the settings. */
    var sprintSide = SprintSide.Left

    /** The buttons of the menu: restarting only makes sense in a game that is paused. */
    val menuItems: List<MenuItem>
        get() = if (phase == GamePhase.Paused) listOf(MenuItem.Resume, MenuItem.Restart, MenuItem.Settings) else listOf(MenuItem.Resume, MenuItem.Settings)

    val layout = WorldLayout(arena).also {
        it.isGateClosed = opening == Opening.Tutorial
        // A game that starts in the stage starts in a perfect oval
        it.isStageSealed = opening == Opening.Oval && DenTuning.sealsStage
    }

    /** The story and the lessons at the start, or null in a world that has none. They come again when the snake goes into the room of the tutorial. */
    var intro: IntroDirector? = if (hasIntro) IntroDirector(isTutorial = opening == Opening.Tutorial) else null
        private set

    /** Set once the player has been through the whole tutorial, and the snake is out in the oval: the page saves it. */
    var hasCompletedTutorial = false
        private set

    var snake: Snake = if (hasIntro) layout.curledSnake() else Snake(arena, layout.ground)
        private set

    /** The world x and y at the top left of the screen: the screen shows the stage, unless the snake is on the path or in the house or the room, and the camera follows it. */
    var cameraX = 0f
        private set
    var cameraY = if (hasIntro) layout.maxCameraY else 0f
        private set

    /**
     * Set once the stage has been won: the way in, which grows shut behind the snake, is open again. There is no way to win a stage yet; each
     * stage will have win conditions of its own, and call [completeStage] when they are met.
     */
    var isStageComplete = false
        private set

    /** How far the grass has grown across the way in to the stage, from 0 (open) to 1 (shut). */
    var sealProgress = if (opening == Opening.Oval && DenTuning.sealsStage) 1f else 0f
        private set

    /** Whether the camera shows the room of the tutorial, to the left of the house, and not the house. */
    private var isRoomView = false

    /** Whether the snake was in the room of the tutorial a moment ago: it only teaches the lessons again when it goes in. */
    /** The room that the head of the snake was last in: on a path it is the room it came from. */
    private var lastRoom: Room? = Room.House
    private var wasInStage = false

    private val mutableCritters = ArrayList<Critter>()

    /** The animals that are out of the grass, in the oval or on their way. */
    val critters: List<Critter> get() = mutableCritters
    val effects = EffectTracker()
    val powerUps = PowerUpTracker()

    /** The effects and power ups that are on. */
    val statuses: List<StatusEntry> get() = ActiveStatuses.of(effects, powerUps)
    private val boosts = PowerUpBoosts(powerUps)
    val hunger = Hunger()
    val sprint = SprintControl()

    private val random = Random(seed)
    val particleSystem = ParticleSystem(random)
    private val spawner = CritterSpawner(arena, random).also { it.avoidsPath = !layout.isStageSealed }
    private val feeding = Feeding(layout.ground)
    private val toadBlast = ToadBlast(particleSystem)
    private val effectParticles = EffectParticleEmitter(particleSystem, random)
    private var seenBounces = 0

    /** Whether the result and the settings button of the game over screen show yet. */
    val isResultShown: Boolean get() = phase == GamePhase.GameOver && gameOverTime > GameOverTuning.resultDelay

    val canRestart: Boolean get() = phase == GamePhase.GameOver && gameOverTime > restartDelay

    fun start() {
        if (phase != GamePhase.Ready) return
        val director = intro
        if (director != null) {
            phase = GamePhase.Intro
            director.wake()
        } else {
            phase = GamePhase.Playing
            spawner.reset()
        }
    }

    /** Pausing opens the overlay: the game (or the story) carries on when it is closed. */
    fun pause() = openOverlay(PausePage.Pause)

    /** Pauses the game, if it is being played (or the story told), and opens the overlay on [page]. */
    fun openOverlay(page: PausePage) {
        if (phase != GamePhase.Playing && phase != GamePhase.Intro) return
        phaseBeforePause = phase
        phase = GamePhase.Paused
        overlay.open(page)
        sprint.clear()
        redraw()
    }

    fun resume() {
        if (phase != GamePhase.Paused) return
        phase = phaseBeforePause
        overlay.close()
        redraw()
    }

    /** Opens the menu: a game that is being played is paused, the title and game over screens just show it over them. */
    fun openMenu() {
        if (phase == GamePhase.Playing || phase == GamePhase.Intro) {
            pause()
        } else if (phase != GamePhase.Paused) {
            overlay.open(PausePage.Pause)
            redraw()
        }
    }

    fun closeMenu() {
        if (phase == GamePhase.Paused) {
            resume()
        } else {
            overlay.close()
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
        if (isInSprintZone && director.showsSprintButton) sprint.press(pointerId) else tap()
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

    fun endGame(cause: DeathCause) {
        if (phase != GamePhase.Playing) return
        phase = GamePhase.GameOver
        deathCause = cause
        sprint.clear()
        gameOverTime = 0f
        shake = startingShake
        particleSystem.explode(snake.x, snake.y, listOf(Color(0xFFE8465A), Color(0xFF52C27D), Color(0xFFF4D35E)))
    }

    fun update(dt: Float) {
        overlay.update(dt)
        if (phase == GamePhase.Paused) {
            tick++
            return
        }
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
                keepArenaGoing(dt)
            }
            GamePhase.Paused -> Unit
        }
        particleSystem.update(dt)
        rejectedTapTime = max(0f, rejectedTapTime - dt)
        growSeal(dt)
        tick++
    }

    private fun updateIntro(dt: Float) {
        val director = intro ?: return
        val stageBefore = director.stage
        director.update(dt)
        if (director.stage != stageBefore) onStageEntered(director.stage)
        stepSnake(dt, director.snakeSpeed * (1f + boosts.sprintBoost * sprint.level) * boosts.speedFactor)
        director.trackSprint(sprint.isHeld, dt)
        // Going into the room again, from the house, starts the lessons over as they were at the start, wherever they had got to
        if (entersRoom()) {
            // After the first time there are no lessons on the way, but going into the room starts them, any time
            if (!director.isTutorial) {
                startReplay()
                return
            }
            if (director.stage >= IntroStage.Hunger) restartLessons(director)
        }
        if (layout.room.signedDistance(snake.x, snake.y) < -DenTuning.roomDepth) director.enteredRoom()
        if (director.stage.isLesson) updateLesson(dt, director)
        // Back in the house the stones that blocked the path up are gone, and it can be taken
        if (director.stage == IntroStage.Return && layout.house.signedDistance(snake.x, snake.y) < -DenTuning.homeDepth) {
            layout.isGateClosed = false
            director.arrivedHome()
        }
        followSnake(dt)
        if (director.isReplay && director.stage == IntroStage.Return) {
            endReplay()
        } else if (arena.signedDistance(snake.x, snake.y) < -DenTuning.exitDepth) {
            finishIntro()
        }
    }

    /** One step of the lessons in the room of the tutorial: the hunger bar, and the animals that the snake has to eat to go on. */
    private fun updateLesson(dt: Float, director: IntroDirector) {
        when (director.stage) {
            // The bar runs down by itself to show it does, and then it runs down while the snake sprints, to show that zooming makes it hungry
            IntroStage.Hunger -> hunger.drainDemo(dt, IntroTuning.hungerDemoLevel)
            IntroStage.Sprint -> hunger.drainSprintDemo(dt, sprint.level, IntroTuning.sprintDemoFloor)
            else -> hunger.settle(dt)
        }
        director.hungerShown(hunger.level)
        if (director.takeAnimalDue()) placeLessonAnimal(director.stage)
        val snakeHead = Vec2(snake.x, snake.y)
        for (critter in critters) critter.update(dt, arena, random, snakeHead, boosts.aura)
        feeding.update(dt, snake, mutableCritters, boosts.mouthFactor, boosts.mouthIsFull).forEach {
            eat(it)
            director.ate(it)
        }
        director.dizzy(effects.isOn(Effect.Confused))
    }

    /** Something happens when a part of the story begins: the bubble pops. */
    private fun onStageEntered(stage: IntroStage) {
        if (stage == IntroStage.Popped) popBubble()
    }

    /**
     * Sends in the animal that the lesson of [stage] is about, from the grass at the edge of the room to the middle of it, where it stays until
     * it is eaten and does not mind the snake. It never comes from the side of the path to the house. The bird flies in from off the screen.
     */
    private fun placeLessonAnimal(stage: IntroStage) {
        mutableCritters.clear()
        val room = layout.room
        val angle = lessonAngleFromPath + random.nextFloat() * (2f * PI.toFloat() - 2f * lessonAngleFromPath)
        val reach = WorldTuning.grassDepth * 0.75f
        val x = room.centerX + (room.radiusX + reach) * cos(angle)
        val y = room.centerY + (room.radiusY + reach) * sin(angle)
        val middle = Vec2(room.centerX, room.centerY)
        mutableCritters.add(
            when (stage) {
                IntroStage.Mouse -> Critter.Mouse(lessonAnimalId, x, y, lifetime = lessonLifetime, tameTarget = middle)
                IntroStage.Toad -> Critter.Toad(lessonAnimalId, ToadKind.Pink, x, y, lifetime = lessonLifetime, tameTarget = middle)
                else -> Critter.Bird(
                    lessonAnimalId, BirdKind.BlueTit, room.centerX - birdStartAside, room.centerY - birdStartAbove,
                    middle.x, middle.y, staySeconds = lessonLifetime, isTame = true,
                )
            },
        )
    }

    /**
     * Puts the snake where the player would be in the lesson [stage], as if everything before it was done: in the middle of the room of the
     * tutorial, or in the house for the way up to the stage, which is open. For the tests and the screenshots.
     */
    internal fun jumpToLesson(stage: IntroStage) {
        val director = intro ?: return
        phase = GamePhase.Intro
        director.jumpTo(stage)
        if (stage == IntroStage.Leaving) {
            layout.isGateClosed = false
            snake = layout.curledSnake()
            lastRoom = Room.House
            isRoomView = false
            cameraX = 0f
        } else {
            snake = layout.snakeInRoom()
            lastRoom = Room.Tutorial
            isRoomView = true
            cameraX = layout.roomCameraX
        }
        cameraY = layout.maxCameraY
    }

    /** Steps the snake, unless it lies still. */
    private fun stepSnake(dt: Float, speed: Float) {
        if (speed > 0f) snake.step(dt, speed)
    }

    private fun popBubble() {
        val bubble = layout.bubbles.last()
        particleSystem.sparkles(bubble.x, bubble.y, 18, listOf(Color(0xFFFFFFFF), Color(0xFFBFE3FF), Color(0xFFF28FA9)))
    }

    /** The snake is in the oval: from here on it is the game. After the whole tutorial the score starts again from nothing. */
    private fun finishIntro() {
        phase = GamePhase.Playing
        if (opening == Opening.Tutorial && !hasCompletedTutorial) {
            hasCompletedTutorial = true
            score = 0
            mutableCritters.clear()
        }
        spawner.reset()
    }

    /** The lessons of a replay are done: the snake goes on playing, in the room, until it leaves it. */
    private fun endReplay() {
        phase = GamePhase.Playing
        mutableCritters.clear()
        lastRoom = Room.Tutorial
        spawner.reset()
    }

    /** The snake goes into the room of the tutorial: the lessons start again, from the hunger bar, until the bird is eaten. */
    private fun startReplay() {
        val replay = IntroDirector(isTutorial = true, isReplay = true)
        replay.jumpTo(IntroStage.Hunger)
        intro = replay
        phase = GamePhase.Intro
        resetLessonState()
    }

    /** The lessons start over from the hunger bar, with everything as it was at the start of them. */
    private fun restartLessons(director: IntroDirector) {
        director.jumpTo(IntroStage.Hunger)
        resetLessonState()
    }

    /** Everything the lessons change goes back to how it was at the start of them: a full belly, no poison, no power up, no animal, no sprint. */
    private fun resetLessonState() {
        hunger.refill()
        effects.clear()
        powerUps.clear()
        mutableCritters.clear()
        sprint.clear()
    }

    /**
     * Whether the head of the snake has just come into the room of the tutorial, from any other room: that is on the screen of the room, and
     * the last room it was on the screen of was another. Moving about in the room, or out onto the path and back, is not that.
     */
    private fun entersRoom(): Boolean {
        val room = layout.roomAt(snake.x, snake.y) ?: return false
        val enters = room == Room.Tutorial && lastRoom != Room.Tutorial
        lastRoom = room
        return enters
    }

    /**
     * Once the whole snake, tail and all, is in the stage, the way it came in by grows shut with grass, so that the stage is a perfect oval
     * and nothing gets in or out: the snake, the mice, the toads and the birds all stay. It stays shut until the stage is won.
     */
    private fun sealWhenInside() {
        if (!DenTuning.sealsStage || layout.isStageSealed || isStageComplete) return
        val tail = snake.body.points.lastOrNull() ?: return
        if (arena.signedDistance(snake.x, snake.y) > -DenTuning.exitDepth || arena.signedDistance(tail.x, tail.y) > -sealTailDepth) return
        layout.isStageSealed = true
        spawner.avoidsPath = false
    }

    /** Whenever the snake leaves the stage, down the path, everything about its state starts again: a full belly, no poison, no power up. */
    private fun resetWhenLeavingStage() {
        val depth = arena.signedDistance(snake.x, snake.y)
        if (depth < -DenTuning.exitDepth) {
            wasInStage = true
        } else if (depth > 0f && wasInStage) {
            wasInStage = false
            resetLessonState()
        }
    }

    /** The stage has been won: the way in opens again. */
    fun completeStage() {
        isStageComplete = true
        layout.isStageSealed = false
        spawner.avoidsPath = true
    }

    /** The grass grows across the way in, or goes away from it, in [DenTuning.sealSeconds]. */
    private fun growSeal(dt: Float) {
        val step = dt / DenTuning.sealSeconds
        sealProgress = if (layout.isStageSealed) min(1f, sealProgress + step) else max(0f, sealProgress - step)
    }

    /**
     * The camera shows the stage, unless the snake leaves it down the path: then it follows the snake, to the house. In the house, and in the
     * room of the tutorial to the left of it, the camera is still, and goes from one to the other when the snake passes between them.
     */
    private fun followSnake(dt: Float) {
        if (snake.x < -DenTuning.roomSwitch) isRoomView = true else if (snake.x > DenTuning.roomSwitch) isRoomView = false
        val targetX = if (isRoomView) layout.roomCameraX else 0f
        val targetY = when {
            isRoomView -> layout.maxCameraY
            layout.house.signedDistance(snake.x, snake.y) < 0f -> layout.maxCameraY
            snake.y > layout.mouthY -> (snake.y - arena.height * DenTuning.followLine).coerceIn(0f, layout.maxCameraY)
            else -> 0f
        }
        val ease = min(1f, dt * DenTuning.cameraEase)
        cameraX += (targetX - cameraX) * ease
        cameraY += (targetY - cameraY) * ease
        if (abs(targetX - cameraX) < cameraSettleDistance) cameraX = targetX
        if (abs(targetY - cameraY) < cameraSettleDistance) cameraY = targetY
    }

    private fun updatePlaying(dt: Float) {
        followSnake(dt)
        sealWhenInside()
        resetWhenLeavingStage()
        if (entersRoom()) {
            startReplay()
            return
        }
        snake.step(dt, (1f + boosts.sprintBoost * sprint.level) * boosts.speedFactor * lazyFactor())
        hunger.update(dt, sprint.level, boosts.sprintDrainFactor, ravenousFactor())
        val touchedGrass = snake.bounceCount != seenBounces
        dustAtNewBounces()
        spawner.update(dt, mutableCritters, score, Vec2(snake.x, snake.y), boosts.attractsMice, allowsToads = !boosts.keepsToadsAway)
        updateCritters(dt)
        feeding.update(dt, snake, mutableCritters, boosts.mouthFactor, boosts.mouthIsFull).forEach { eat(it) }
        effectParticles.emit(effects, sprint.level, snake, critters)
        causeOfDeath(touchedGrass)?.let { endGame(it) }
    }

    /** How much of the bar [amount] of food fills right now: a greedy snake gets only part of it. */
    fun foodValue(amount: Float): Float = if (effects.isOn(Effect.Greedy)) amount * EffectTuning.greedyFeedFactor else amount

    private fun feed(amount: Float) = hunger.feed(foodValue(amount))

    /** Ravenous makes the hunger bar drain faster, and fades in and out like its arrow. */
    private fun ravenousFactor(): Float = 1f + (EffectTuning.ravenousDrainFactor - 1f) * effects.level(Effect.Ravenous)

    /** Lazy slows the snake down, and fades in and out like its bar. */
    private fun lazyFactor(): Float = 1f + (EffectTuning.lazySpeedFactor - 1f) * effects.level(Effect.Lazy)

    /** What kills the snake right now, if anything. */
    private fun causeOfDeath(touchedGrass: Boolean): DeathCause? = when {
        hunger.isStarving -> DeathCause.Starvation
        effects.isOn(Effect.Hayfever) && touchedGrass -> DeathCause.Allergies
        effects.isOn(Effect.Cannibal) && snake.hitsItself() -> DeathCause.Cannibalism
        else -> null
    }

    private fun dustAtNewBounces() {
        if (snake.bounceCount == seenBounces) return
        seenBounces = snake.bounceCount
        particleSystem.dust(snake.lastBounceX, snake.lastBounceY, 6, dustColor)
    }

    /**
     * The snake is dead, but the oval carries on as normal behind it: mice come and go, toads hop, birds fly in. The animals take no notice
     * of the dead snake, and it eats nothing.
     */
    private fun keepArenaGoing(dt: Float) {
        spawner.update(dt, mutableCritters, score)
        updateCritters(dt, snakeHead = null)
    }

    private fun updateCritters(dt: Float, snakeHead: Vec2? = Vec2(snake.x, snake.y)) {
        val aura = if (snakeHead == null) SnakeAura.None else boosts.aura
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
                feed(HungerTuning.mouseFills)
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
                feed(HungerTuning.birdFills)
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
                feed(HungerTuning.toadFills)
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

        /** The tail of the snake has to be this far inside the oval for the way in to be sealed. */
        const val sealTailDepth = 3f

        /** The animals of the lessons: their id, how long they stay, and how they come in: not from the side of the path to the house, and the bird from above and to the side of the middle. */
        const val lessonAnimalId = 900
        const val lessonLifetime = 100000f
        const val lessonAngleFromPath = 0.7f
        const val birdStartAside = 60f
        const val birdStartAbove = 125f

        /** The camera is close enough to where it is going to be there. */
        const val cameraSettleDistance = 0.05f

        /** The snake on the title screen circles slowly. */
        const val readySpeed = 0.6f
        const val runDustChance = 0.12f
        val dustColor = Color(0xFFE6D3A0)
    }
}
