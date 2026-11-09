package com.lukeneedham.snake.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lukeneedham.snake.data.BestScoreStore
import com.lukeneedham.snake.data.BestScoreTracker
import com.lukeneedham.snake.data.SettingsStore
import com.lukeneedham.snake.game.Arena
import com.lukeneedham.snake.game.GamePhase
import com.lukeneedham.snake.game.GameWorld
import com.lukeneedham.snake.game.MenuItem
import com.lukeneedham.snake.game.Opening
import com.lukeneedham.snake.game.PausePage
import com.lukeneedham.snake.game.PressTarget
import com.lukeneedham.snake.game.SprintSide
import com.lukeneedham.snake.game.WorldTuning
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/** Connects the game to the page: it steps the world every frame, passes the fingers and the pause on, and publishes what the page shows. */
class GameViewModel(bestScoreStore: BestScoreStore, private val settingsStore: SettingsStore) : ViewModel() {

    private var sprintSide = SprintSide.Left

    /** Whether the player has been through the whole tutorial: from then on the path is short. Until the settings are read it is not known. */
    private var isTutorialDone = false
    private var world = GameWorld(Arena(WorldTuning.width.toFloat(), defaultHeight), opening = Opening.Tutorial)
    private val bestScores = BestScoreTracker(bestScoreStore, viewModelScope) { publish() }

    private val _uiState = MutableStateFlow(buildState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    init {
        bestScores.start()
        viewModelScope.launch {
            settingsStore.isTutorialDone.collect { isDone ->
                isTutorialDone = isDone
                // The title is already up, with the long path: a player who knows it all gets the short one
                if (isDone && world.phase == GamePhase.Ready && world.opening == Opening.Tutorial) newWorld(world.arena.height, Opening.Returning)
            }
        }
        viewModelScope.launch {
            settingsStore.sprintSide.collect {
                sprintSide = it
                world.sprintSide = it
            }
        }
    }

    /** The arena is an oval that fills the screen, so the world takes the aspect ratio of the screen. */
    fun onSceneSize(widthPx: Int, heightPx: Int) {
        if (widthPx <= 0 || heightPx <= 0) return
        val height = (WorldTuning.width.toFloat() * heightPx / widthPx).roundToInt()
        if (height == world.arena.height.toInt()) return
        // A game in progress is lost when the screen changes size; the title or the story begins again
        val keepsOpening = world.phase == GamePhase.Ready || world.isIntro
        newWorld(height.toFloat(), if (keepsOpening) world.opening else Opening.Oval)
    }

    /** Called once per frame with the seconds since the last frame. */
    fun onFrame(seconds: Float) {
        world.update(seconds.coerceIn(0f, maxStep))
        if (world.hasCompletedTutorial && !isTutorialDone) {
            isTutorialDone = true
            viewModelScope.launch { settingsStore.setTutorialDone(true) }
        }
        if (world.phase == GamePhase.GameOver) bestScores.gameOver(world.score)
        publish()
    }

    /**
     * A finger pressed [target]. With the pause overlay open only its close button, page dots and buttons do anything (the page opens
     * the settings itself); the overlay is pressed when the finger lets go, so that a swipe between pages presses nothing. With it
     * closed, the top left corner opens it, the stomach and the arrows in the bottom right corner open it on their pages, and any other
     * finger acts at once: in the sprint zone it sprints while it is held during a game, anywhere else it turns the snake, or starts,
     * or restarts the game.
     */
    fun onPress(pointerId: Long, target: PressTarget) {
        if (world.isMenuOpen) {
            when (target) {
                is PressTarget.Item -> onMenuItem(target.item)
                PressTarget.CloseButton -> world.closeMenu()
                is PressTarget.PageDot -> world.overlay.goTo(target.page)
                else -> Unit
            }
        } else if (target == PressTarget.StatusCorner && world.phase == GamePhase.Playing) {
            world.openOverlay(PausePage.Status)
        } else if (target == PressTarget.Stomach && world.phase == GamePhase.Playing) {
            world.openOverlay(PausePage.Hunger)
        } else if (target == PressTarget.MenuButton) {
            world.openMenu()
        } else {
            when (world.phase) {
                GamePhase.Ready -> world.start()
                GamePhase.Intro -> world.introPress(pointerId, isInSprintZone = target == PressTarget.Sprint)
                GamePhase.Playing -> if (target == PressTarget.Sprint) world.sprintPress(pointerId) else world.tap()
                GamePhase.Paused -> Unit
                GamePhase.GameOver -> restartIfAllowed()
            }
        }
        publish()
    }

    /** The back button was pressed while the pause overlay is open: it closes, and the game carries on if it was paused. */
    fun onBack() {
        world.closeMenu()
        publish()
    }

    /** A finger drags the pages of the pause overlay by [pages] pages (a fraction of the screen width). */
    fun onPageDrag(pages: Float) {
        world.overlay.dragBy(pages)
        publish()
    }

    /** The finger that dragged the pages of the overlay let go. */
    fun onPageRelease() {
        world.overlay.release()
        publish()
    }

    fun onRelease(pointerId: Long) {
        world.sprintRelease(pointerId)
        publish()
    }

    private fun onMenuItem(item: MenuItem) {
        when (item) {
            MenuItem.Resume -> world.closeMenu()
            MenuItem.Restart -> {
                newWorld(world.arena.height, Opening.Oval)
                world.start()
            }
            MenuItem.Settings -> Unit // the page opens it
        }
    }

    private fun restartIfAllowed() {
        if (!world.canRestart) return
        newWorld(world.arena.height, Opening.Oval)
        world.start()
    }

    /** The game stops while the app is in the background: it comes back to the menu. */
    fun onPause() {
        world.pause()
        publish()
    }

    /** The story and the lessons at the start are only told in the first game: a restart goes straight to the oval. */
    private fun newWorld(height: Float, opening: Opening) {
        world = GameWorld(Arena(WorldTuning.width.toFloat(), height), seed = (world.time * 1000f).toInt(), opening = opening)
            .also { it.sprintSide = sprintSide }
        bestScores.newGame()
        publish()
    }

    private fun publish() {
        val state = buildState()
        if (state != _uiState.value) _uiState.value = state
    }

    private fun buildState() = GameUiState(
        world = world,
        phase = world.phase,
        score = world.score,
        bestScore = bestScores.best,
        isNewBest = bestScores.isNewBest,
        isMenuOpen = world.isMenuOpen,
    )

    private companion object {
        const val defaultHeight = 234f
        const val maxStep = 0.05f
    }
}
