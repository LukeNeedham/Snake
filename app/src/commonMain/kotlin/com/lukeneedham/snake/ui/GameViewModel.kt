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
import com.lukeneedham.snake.game.PressTarget
import com.lukeneedham.snake.game.SprintSide
import com.lukeneedham.snake.game.WorldTuning
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/** Connects the game to the page: it steps the world every frame, passes the fingers and the pause on, and publishes what the page shows. */
class GameViewModel(bestScoreStore: BestScoreStore, settingsStore: SettingsStore) : ViewModel() {

    private var sprintSide = SprintSide.Left
    private var world = GameWorld(Arena(WorldTuning.width.toFloat(), defaultHeight), hasIntro = true)
    private val bestScores = BestScoreTracker(bestScoreStore, viewModelScope) { publish() }

    private val _uiState = MutableStateFlow(buildState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    init {
        bestScores.start()
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
        newWorld(height.toFloat(), hasIntro = world.phase == GamePhase.Ready || world.isIntro)
    }

    /** Called once per frame with the seconds since the last frame. */
    fun onFrame(seconds: Float) {
        world.update(seconds.coerceIn(0f, maxStep))
        if (world.phase == GamePhase.GameOver) bestScores.gameOver(world.score)
        publish()
    }

    /**
     * A finger went down on [target]. With the menu open only its buttons do anything (the page opens the settings itself). With it
     * closed, the button in the corner opens it, and any other finger acts at once: in the sprint zone it sprints while it is held
     * during a game, anywhere else it turns the snake, or starts, or restarts the game.
     */
    fun onPress(pointerId: Long, target: PressTarget) {
        if (world.isMenuOpen) {
            if (target is PressTarget.Item) onMenuItem(target.item)
        } else if (target == PressTarget.MenuButton) {
            world.openMenu()
        } else {
            when (world.phase) {
                GamePhase.Ready -> world.start()
                GamePhase.Intro -> world.tap()
                GamePhase.Playing -> if (target == PressTarget.Sprint) world.sprintPress(pointerId) else world.tap()
                GamePhase.Paused -> Unit
                GamePhase.GameOver -> restartIfAllowed()
            }
        }
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
                newWorld(world.arena.height, hasIntro = false)
                world.start()
            }
            MenuItem.Settings -> Unit // the page opens it
        }
    }

    private fun restartIfAllowed() {
        if (!world.canRestart) return
        newWorld(world.arena.height, hasIntro = false)
        world.start()
    }

    /** The game stops while the app is in the background: it comes back to the menu. */
    fun onPause() {
        world.pause()
        publish()
    }

    /** The story at the start is only told in the first game: a restart goes straight to the oval. */
    private fun newWorld(height: Float, hasIntro: Boolean) {
        world = GameWorld(Arena(WorldTuning.width.toFloat(), height), seed = (world.time * 1000f).toInt(), hasIntro = hasIntro)
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
    )

    private companion object {
        const val defaultHeight = 234f
        const val maxStep = 0.05f
    }
}
